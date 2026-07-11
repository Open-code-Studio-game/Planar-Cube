package org.opencodestudiogame.planarcube.entity;

import org.opencodestudiogame.planarcube.engine.Renderer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.stb.STBImage.*;

/**
 * MC皮肤类
 * 支持加载和渲染Minecraft皮肤文件
 */
public class Skin {
    private int textureId = -1;
    private int width = 64;
    private int height = 64;
    private ByteBuffer imageData;
    private boolean loaded = false;
    
    // 动画状态
    private float animationTime = 0.0f;
    private boolean walking = false;
    private boolean jumping = false;
    
    public Skin() {
        // 创建默认皮肤（史蒂夫皮肤）
        createDefaultSkin();
    }
    
    /**
     * 从文件加载皮肤
     */
    public boolean loadFromFile(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                System.err.println("皮肤文件不存在: " + filePath);
                return false;
            }
            
            // 使用STB Image加载图片
            IntBuffer w = IntBuffer.allocate(1);
            IntBuffer h = IntBuffer.allocate(1);
            IntBuffer comp = IntBuffer.allocate(1);
            
            imageData = stbi_load(filePath, w, h, comp, 4);
            if (imageData == null) {
                System.err.println("无法加载皮肤图像: " + stbi_failure_reason());
                return false;
            }
            
            width = w.get();
            height = h.get();
            
            // 创建OpenGL纹理
            createTexture();
            
            loaded = true;
            System.out.println("皮肤加载成功: " + filePath + " (" + width + "x" + height + ")");
            return true;
            
        } catch (Exception e) {
            System.err.println("加载皮肤失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 创建默认史蒂夫皮肤
     */
    private void createDefaultSkin() {
        // 创建一个简单的64x64默认皮肤
        width = 64;
        height = 64;
        
        // 分配缓冲区
        imageData = ByteBuffer.allocateDirect(width * height * 4);
        
        // 填充默认皮肤颜色（简化版）
        // 皮肤部分（浅棕色）
        fillRect(0, 0, 64, 64, 0xFF, 0xC6, 0x85, 0x5F); // 皮肤底色
        
        // 头部（8x8）
        fillRect(8, 8, 8, 8, 0xFF, 0x8B, 0x45, 0x13); // 头发/帽子
        
        // 身体（8x12）
        fillRect(20, 20, 8, 12, 0xFF, 0x00, 0x7F, 0x00); // 衣服
        
        // 手臂（4x12）
        fillRect(4, 20, 4, 12, 0xFF, 0xC6, 0x85, 0x5F); // 左臂
        fillRect(56, 20, 4, 12, 0xFF, 0xC6, 0x85, 0x5F); // 右臂
        
        // 腿部（4x12）
        fillRect(4, 36, 4, 12, 0xFF, 0x00, 0x00, 0x8B); // 左腿
        fillRect(56, 36, 4, 12, 0xFF, 0x00, 0x00, 0x8B); // 右腿
        
        // 创建纹理
        createTexture();
        loaded = true;
        
        System.out.println("创建默认史蒂夫皮肤");
    }
    
    /**
     * 填充矩形区域
     */
    private void fillRect(int x, int y, int w, int h, int r, int g, int b, int a) {
        for (int j = y; j < y + h; j++) {
            for (int i = x; i < x + w; i++) {
                int index = (j * width + i) * 4;
                if (index + 3 < imageData.capacity()) {
                    imageData.put(index, (byte) r);
                    imageData.put(index + 1, (byte) g);
                    imageData.put(index + 2, (byte) b);
                    imageData.put(index + 3, (byte) a);
                }
            }
        }
    }
    
    /**
     * 创建OpenGL纹理
     */
    private void createTexture() {
        if (imageData == null) return;
        
        textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);
        
        // 设置纹理参数
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        
        // 上传纹理数据
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, 
                    GL_RGBA, GL_UNSIGNED_BYTE, imageData);
        
        glBindTexture(GL_TEXTURE_2D, 0);
        
        // 释放图像数据
        if (imageData != null) {
            stbi_image_free(imageData);
            imageData = null;
        }
    }
    
    /**
     * 更新皮肤动画
     */
    public void update(double deltaTime) {
        animationTime += deltaTime;
        
        // 简单的动画逻辑
        if (walking) {
            // 行走动画
        }
        
        if (jumping) {
            // 跳跃动画
        }
    }
    
    /**
     * 渲染皮肤（2D精灵渲染）
     */
    public void render(Renderer renderer, float x, float y, int layer, float w, float h) {
        if (!loaded || textureId == -1) return;

        glBindTexture(GL_TEXTURE_2D, textureId);

        // 简单的2D四边形渲染（占位：带纹理的矩形）
        float[] vertices = {
            x, y,         0, 1, // 左上
            x + w, y,     1, 1, // 右上
            x + w, y + h, 1, 0, // 右下
            x, y + h,     0, 0, // 左下
        };

        // 暂时使用颜色占位（后续可改为纹理采样）
        // 渲染一个简单的人形轮廓
        renderPlayerShape(renderer, x, y, w, h);

        glBindTexture(GL_TEXTURE_2D, 0);
    }

    /**
     * 渲染玩家2D形状（占位矩形）
     */
    private void renderPlayerShape(Renderer renderer, float x, float y, float w, float h) {
        // 简单角色轮廓：头 + 身体 + 腿
        float headSize = w * 0.8f;
        float headX = x + (w - headSize) / 2;
        float headY = y + h - headSize;
        float bodyWidth = w * 0.5f;
        float bodyHeight = h * 0.45f;
        float bodyX = x + (w - bodyWidth) / 2;
        float bodyY = headY - bodyHeight;
        float legWidth = w * 0.22f;
        float legHeight = h * 0.35f;

        // 切换为颜色模式渲染（简单的矩形占位，通过Terrain类似的VAO渲染）
        // 这里暂时依赖 Renderer 的着色器，通过统一颜色渲染
        // 实现留待后续完善纹理采样
    }
    
    /**
     * 设置动画状态
     */
    public void setWalking(boolean walking) {
        this.walking = walking;
    }
    
    public void setJumping(boolean jumping) {
        this.jumping = jumping;
    }
    
    /**
     * 清理资源
     */
    public void cleanup() {
        if (textureId != -1) {
            glDeleteTextures(textureId);
            textureId = -1;
        }
        
        if (imageData != null) {
            stbi_image_free(imageData);
            imageData = null;
        }
        
        loaded = false;
    }
    
    // Getter方法
    public boolean isLoaded() { return loaded; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getTextureId() { return textureId; }
}