package org.opencodestudiogame.planarcube.engine;

import static org.lwjgl.opengl.GL30.*;

/**
 * 渲染器类
 */
public class Renderer {
    private int currentShaderProgram;
    
    public Renderer() {
        init();
    }
    
    private void init() {
        // 启用深度测试
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);
        
        // 启用混合
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        
        // 启用面剔除
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        
        // 设置视口
        glViewport(0, 0, 1280, 720);
        
        System.out.println("渲染器初始化完成");
    }
    
    public void begin() {
        // 清除颜色和深度缓冲区
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }
    
    public void end() {
        // 当前不需要特殊操作
    }
    
    public void setShaderProgram(int program) {
        if (currentShaderProgram != program) {
            glUseProgram(program);
            currentShaderProgram = program;
        }
    }
    
    public void cleanup() {
        glDeleteProgram(currentShaderProgram);
        System.out.println("渲染器清理完成");
    }
    
    // 辅助方法：编译着色器
    public static int compileShader(int type, String source) {
        int shader = glCreateShader(type);
        glShaderSource(shader, source);
        glCompileShader(shader);
        
        if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
            String log = glGetShaderInfoLog(shader);
            System.err.println("着色器编译失败: " + log);
            glDeleteShader(shader);
            return 0;
        }
        
        return shader;
    }
    
    // 辅助方法：创建着色器程序
    public static int createShaderProgram(String vertexSource, String fragmentSource) {
        int vertexShader = compileShader(GL_VERTEX_SHADER, vertexSource);
        int fragmentShader = compileShader(GL_FRAGMENT_SHADER, fragmentSource);
        
        if (vertexShader == 0 || fragmentShader == 0) {
            return 0;
        }
        
        int program = glCreateProgram();
        glAttachShader(program, vertexShader);
        glAttachShader(program, fragmentShader);
        glLinkProgram(program);
        
        if (glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE) {
            String log = glGetProgramInfoLog(program);
            System.err.println("着色器程序链接失败: " + log);
            glDeleteProgram(program);
            return 0;
        }
        
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
        
        return program;
    }
}