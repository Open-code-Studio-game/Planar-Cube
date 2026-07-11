package org.opencodestudiogame.planarcube.engine;

import org.joml.Matrix4f;
import static org.lwjgl.opengl.GL30.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * 2D渲染器，使用正交投影
 */
public class Renderer {
    private int shaderProgram;
    private int mvpUniformLoc;
    private Matrix4f projectionMatrix;
    private Matrix4f viewMatrix;
    private Matrix4f mvpMatrix;

    public Renderer() {
        mvpMatrix = new Matrix4f();
        projectionMatrix = new Matrix4f();
        viewMatrix = new Matrix4f();
        init();
    }

    private void init() {
        // 禁用深度测试（2D不需要）
        glDisable(GL_DEPTH_TEST);

        // 启用混合
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        // 禁用面剔除
        glDisable(GL_CULL_FACE);

        // 加载并编译着色器
        shaderProgram = loadShaders();
        if (shaderProgram != 0) {
            glUseProgram(shaderProgram);
            mvpUniformLoc = glGetUniformLocation(shaderProgram, "uMVP");
            glUseProgram(0);
        }

        System.out.println("2D渲染器初始化完成");
    }

    private int loadShaders() {
        try {
            String vertSrc = Files.readString(Paths.get("src/main/resources/shaders/vertex.glsl"));
            String fragSrc = Files.readString(Paths.get("src/main/resources/shaders/fragment.glsl"));
            return createShaderProgram(vertSrc, fragSrc);
        } catch (IOException e) {
            System.err.println("无法加载着色器文件: " + e.getMessage());
            return 0;
        }
    }

    /**
     * 设置正交投影
     */
    public void setOrthographicProjection(float left, float right, float bottom, float top, float near, float far) {
        projectionMatrix.identity();
        projectionMatrix.ortho(left, right, bottom, top, near, far);
    }

    /**
     * 设置视图矩阵（2D固定视角）
     */
    public void setViewMatrix(Matrix4f view) {
        viewMatrix.set(view);
    }

    /**
     * 获取MVP矩阵
     */
    public Matrix4f getMVPMatrix() {
        mvpMatrix.identity();
        mvpMatrix.mul(projectionMatrix);
        mvpMatrix.mul(viewMatrix);
        return mvpMatrix;
    }

    public void begin() {
        glClear(GL_COLOR_BUFFER_BIT);
        glClearColor(0.4f, 0.7f, 1.0f, 1.0f); // 天蓝色背景

        if (shaderProgram != 0) {
            glUseProgram(shaderProgram);
        }
    }

    public void uploadMVPMatrix(Matrix4f mvp) {
        if (shaderProgram != 0) {
            float[] buf = new float[16];
            mvp.get(buf);
            glUniformMatrix4fv(mvpUniformLoc, false, buf);
        }
    }

    public void end() {
        if (shaderProgram != 0) {
            glUseProgram(0);
        }
    }

    public int getShaderProgram() {
        return shaderProgram;
    }

    public void cleanup() {
        if (shaderProgram != 0) {
            glDeleteProgram(shaderProgram);
            shaderProgram = 0;
        }
        System.out.println("2D渲染器清理完成");
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
