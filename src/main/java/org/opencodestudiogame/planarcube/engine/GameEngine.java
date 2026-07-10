package org.opencodestudiogame.planarcube.engine;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL.*;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;
import org.opencodestudiogame.planarcube.world.World;
import org.opencodestudiogame.planarcube.entity.Player;

/**
 * 游戏引擎主类
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
        
        // 初始化世界和玩家
        world = new World(config.getGameMode());
        player = new Player();
        
        // 设置输入回调
        setupInputCallbacks();
        
        running = true;
        System.out.println("游戏引擎初始化完成");
    }
    
    private void setupInputCallbacks() {
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            inputHandler.handleKey(key, action);
            
            // 处理游戏逻辑按键
            if (action == GLFW_PRESS || action == GLFW_REPEAT) {
                switch (key) {
                    case GLFW_KEY_W:
                        player.moveBetweenSpaces(1); // 向上一个空间
                        break;
                    case GLFW_KEY_S:
                        player.moveBetweenSpaces(-1); // 向下一个空间
                        break;
                    case GLFW_KEY_A:
                        player.moveInSpace(-1, 0); // 向左移动
                        break;
                    case GLFW_KEY_D:
                        player.moveInSpace(1, 0); // 向右移动
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
        double deltaTime;
        
        while (running && !glfwWindowShouldClose(window)) {
            double currentTime = glfwGetTime();
            deltaTime = currentTime - lastTime;
            lastTime = currentTime;
            
            // 处理输入
            glfwPollEvents();
            
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
    
    private void update(double deltaTime) {
        // 更新玩家状态
        player.update(deltaTime);
        
        // 更新世界状态
        world.update(deltaTime);
    }
    
    private void render() {
        // 清除颜色缓冲区
        glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        
        // 开始渲染
        renderer.begin();
        
        // 渲染世界
        world.render(renderer);
        
        // 渲染玩家
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