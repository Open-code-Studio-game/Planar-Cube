package org.opencodestudiogame.planarcube.engine;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.stb.STBImage.*;

import org.opencodestudiogame.planarcube.world.BlockType;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * 2D纹理图集类
 * 管理所有方块的2D纹理区域
 */
public class TextureAtlas {
    private int textureId = -1;
    private int width;
    private int height;
    private boolean loaded = false;

    // 每个BlockType对应的纹理区域 (u0, v0, u1, v1)
    private final Map<BlockType, float[]> textureRegions = new HashMap<>();

    public TextureAtlas() {
        initDefaultRegions();
    }

    /**
     * 初始化默认纹理区域（假设8列1行的图集布局）
     */
    private void initDefaultRegions() {
        int cols = 8;
        float cellW = 1.0f / cols;

        BlockType[] types = {BlockType.GRASS, BlockType.DIRT, BlockType.STONE,
                             BlockType.WOOD, BlockType.LEAVES, BlockType.WATER, BlockType.BEDROCK};
        for (int i = 0; i < types.length; i++) {
            float u0 = (i + 1) * cellW;
            float v0 = 0;
            float u1 = u0 + cellW;
            float v1 = 1.0f;
            textureRegions.put(types[i], new float[]{u0, v0, u1, v1});
        }
        // AIR占位
        textureRegions.put(BlockType.AIR, new float[]{0, 0, cellW, 1.0f});
    }

    /**
     * 获取某个BlockType的纹理坐标区域
     */
    public float[] getTextureRegion(BlockType type) {
        return textureRegions.getOrDefault(type, new float[]{0, 0, 0.25f, 1.0f});
    }
    private int textureId = -1;
    private int width;
    private int height;
    private boolean loaded = false;
    
    public TextureAtlas() {
    }
    
    /**
     * 加载纹理图集
     */
    public boolean load(String filePath) {
        try {
            IntBuffer w = IntBuffer.allocate(1);
            IntBuffer h = IntBuffer.allocate(1);
            IntBuffer comp = IntBuffer.allocate(1);
            
            ByteBuffer imageData = stbi_load(filePath, w, h, comp, 4);
            if (imageData == null) {
                System.err.println("无法加载纹理图集: " + stbi_failure_reason());
                return false;
            }
            
            width = w.get();
            height = h.get();
            
            // 创建OpenGL纹理
            textureId = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, textureId);
            
            // 设置纹理参数
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST_MIPMAP_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            
            // 上传纹理数据
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, 
                        GL_RGBA, GL_UNSIGNED_BYTE, imageData);
            glGenerateMipmap(GL_TEXTURE_2D);
            
            glBindTexture(GL_TEXTURE_2D, 0);
            
            // 释放图像数据
            stbi_image_free(imageData);
            
            loaded = true;
            System.out.println("纹理图集加载成功: " + filePath + " (" + width + "x" + height + ")");
            return true;
            
        } catch (Exception e) {
            System.err.println("加载纹理图集失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 创建默认纹理图集（程序生成）
     */
    public boolean createDefault() {
        width = 256;
        height = 256;
        
        // 创建简单的纹理图集
        ByteBuffer imageData = ByteBuffer.allocateDirect(width * height * 4);
        
        // 填充简单颜色
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int index = (y * width + x) * 4;
                
                // 创建简单的棋盘格图案
                boolean isDark = ((x / 32) + (y / 32)) % 2 == 0;
                
                if (isDark) {
                    imageData.put(index, (byte) 100);     // R
                    imageData.put(index + 1, (byte) 100); // G
                    imageData.put(index + 2, (byte) 100); // B
                    imageData.put(index + 3, (byte) 255); // A
                } else {
                    imageData.put(index, (byte) 150);     // R
                    imageData.put(index + 1, (byte) 150); // G
                    imageData.put(index + 2, (byte) 150); // B
                    imageData.put(index + 3, (byte) 255); // A
                }
            }
        }
        
        // 创建OpenGL纹理
        textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);
        
        // 设置纹理参数
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        
        // 上传纹理数据
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, 
                    GL_RGBA, GL_UNSIGNED_BYTE, imageData);
        
        glBindTexture(GL_TEXTURE_2D, 0);
        
        loaded = true;
        System.out.println("创建默认纹理图集: " + width + "x" + height);
        return true;
    }
    
    /**
     * 绑定纹理
     */
    public void bind() {
        if (loaded) {
            glBindTexture(GL_TEXTURE_2D, textureId);
        }
    }
    
    /**
     * 解绑纹理
     */
    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }
    
    /**
     * 清理资源
     */
    public void cleanup() {
        if (textureId != -1) {
            glDeleteTextures(textureId);
            textureId = -1;
        }
        loaded = false;
    }
    
    // Getter方法
    public boolean isLoaded() { return loaded; }
    public int getTextureId() { return textureId; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}