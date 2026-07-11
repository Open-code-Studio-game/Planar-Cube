package org.opencodestudiogame.planarcube.engine;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL.*;
import static org.lwjgl.opengl.GL11.*;

import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;
import org.opencodestudiogame.planarcube.world.World;
import org.opencodestudiogame.planarcube.entity.Player;

/**
 * 游戏引擎主类 - 垂直切片2D平台游戏
 */
public class GameEngine {
    private final GameConfig config;
    private long window;
    private boolean running = false;
    private World world;
    private Player player;
    private InputHandler inputHandler;
    private Renderer renderer;
    
    public GameEngine(GameConfig config) {
        this.config = config;
    }
    
    public void run() {
        init();
        loop();
        cleanup();
    }
    
    private void init() {
        // 初始化GLFW
        GLFWErrorCallback.createPrint(System.err).set();
        
        if (!glfwInit()) {
            throw new IllegalStateException("无法初始化GLFW");
        }
        
        // 配置窗口
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        
        // 创建窗口
        window = glfwCreateWindow(
            config.getWidth(),
            config.getHeight(),
            config.getTitle(),
            config.isFullscreen() ? glfwGetPrimaryMonitor() : MemoryUtil.NULL,
            MemoryUtil.NULL
        );
        
        if (window == MemoryUtil.NULL) {
            throw new RuntimeException("无法创建GLFW窗口");
        }
        
        // 设置窗口位置居中
        var vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(
            window,
            (vidMode.width() - config.getWidth()) / 2,
            (vidMode.height() - config.getHeight()) / 2
        );
        
        // 设置OpenGL上下文
        glfwMakeContextCurrent(window);
        
        if (config.isVsync()) {
            glfwSwapInterval(1);
        }
        
        // 显示窗口
        glfwShowWindow(window);
        
        // 初始化OpenGL
        GL.createCapabilities();
        
        // 初始化游戏组件
        inputHandler = new InputHandler(window);
        renderer = new Renderer();
        
        // 初始化世界和玩家（先世界后玩家，玩家需要世界引用）
        world = new World(config.getGameMode());
        player = new Player(world);
        
        // 设置2D正交投影
        // 视口跟随玩家：以玩家X为中心，水平显示 ~40 个方块
        renderer.setOrthographicProjection(-20, 20, -2.5f, 37.5f, -1, 1);
        
        // 设置输入回调
        setupInputCallbacks();
        
        running = true;
        System.out.println("2D平台游戏引擎初始化完成");
        System.out.println("玩家初始位置: X=" + player.getX() + " Y=" + player.getY() + " 层=" + player.getCurrentLayer());
        System.out.println("操作: WASD移动/切换层, 空格跳跃, ESC退出");
    }
    
    private void setupInputCallbacks() {
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            inputHandler.handleKey(key, action);
            
            // 处理游戏逻辑按键（仅按下时触发一次）
            if (action == GLFW_PRESS) {
                switch (key) {
                    case GLFW_KEY_W:
                        player.moveToLayer(1);  // 向前移动到下一层 (Z+1)
                        break;
                    case GLFW_KEY_S:
                        player.moveToLayer(-1); // 向后移动到上一层 (Z-1)
                        break;
                    case GLFW_KEY_SPACE:
                        player.jump();          // 跳跃
                        break;
                    case GLFW_KEY_ESCAPE:
                        glfwSetWindowShouldClose(window, true);
                        break;
                }
            }
        });
    }
    
    private void loop() {
        double lastTime = glfwGetTime();
        
        while (running && !glfwWindowShouldClose(window)) {
            double currentTime = glfwGetTime();
            double deltaTime = currentTime - lastTime;
            lastTime = currentTime;
            
            // 限制deltaTime防止跳跃时物理异常
            if (deltaTime > 0.05) deltaTime = 0.05;
            
            // 处理输入
            glfwPollEvents();
            
            // 处理连续按键（A/D左右移动）
            processContinuousInput(deltaTime);
            
            // 更新游戏逻辑
            update(deltaTime);
            
            // 渲染
            render();
            
            // 交换缓冲区
            glfwSwapBuffers(window);
            
            // 限制帧率
            if (config.getTargetFps() > 0) {
                double targetFrameTime = 1.0 / config.getTargetFps();
                while (glfwGetTime() - currentTime < targetFrameTime) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
    }
    
    /**
     * 处理连续按键输入（A/D水平移动）
     */
    private void processContinuousInput(double deltaTime) {
        if (inputHandler.isKeyDown(GLFW_KEY_A)) {
            player.moveHorizontal(-1); // 向左移动
        }
        if (inputHandler.isKeyDown(GLFW_KEY_D)) {
            player.moveHorizontal(1);  // 向右移动
        }
    }
    
    private void update(double deltaTime) {
        // 更新玩家状态（物理、重力、碰撞）
        player.update(deltaTime);
        
        // 更新世界状态
        world.update(deltaTime);
        
        // 更新2D摄像机跟随玩家
        updateCamera();
    }
    
    /**
     * 更新2D摄像机，跟随玩家
     */
    private void updateCamera() {
        float playerX = player.getX();
        float playerY = player.getY();
        
        // 水平跟随：玩家居中，边界不超出世界范围
        float halfView = 20.0f;
        float left = playerX - halfView;
        float right = playerX + halfView;
        
        // 限制摄像机不超出世界边界
        if (left < 0) {
            left = 0;
            right = 2 * halfView;
        }
        if (right > world.getWorldWidth()) {
            right = world.getWorldWidth();
            left = right - 2 * halfView;
        }
        
        // 垂直：稍微让玩家偏下
        float bottom = playerY - 5.0f;
        float top = playerY + 35.0f;
        
        renderer.setOrthographicProjection(left, right, bottom, top, -1, 1);
    }
    
    private void render() {
        // 开始渲染（自动清除缓冲区）
        renderer.begin();
        
        // 计算并上传MVP矩阵
        Matrix4f mvp = renderer.getMVPMatrix();
        renderer.uploadMVPMatrix(mvp);
        
        // 渲染当前层的2D截面
        world.renderLayer(renderer, player.getCurrentLayer());
        
        // 渲染玩家精灵
        player.render(renderer);
        
        // 结束渲染
        renderer.end();
    }
    
    private void cleanup() {
        // 清理资源
        if (renderer != null) {
            renderer.cleanup();
        }
        
        if (world != null) {
            world.cleanup();
        }
        
        // 销毁窗口
        if (window != MemoryUtil.NULL) {
            glfwDestroyWindow(window);
        }
        
        // 终止GLFW
        glfwTerminate();
        glfwSetErrorCallback(null).free();
        
        System.out.println("游戏引擎清理完成");
    }
}