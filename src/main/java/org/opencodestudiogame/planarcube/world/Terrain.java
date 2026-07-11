package org.opencodestudiogame.planarcube.world;

import org.opencodestudiogame.planarcube.engine.Renderer;
import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * 地形类，管理3D体素方块数据，支持按层渲染2D截面
 */
public class Terrain {
    private final BlockType[][][] blocks;
    private final int width;
    private final int height;
    private final int depth;

    // 2D层渲染缓存：每个Z层一个VAO/VBO
    private final int[] layerVaos;
    private final int[] layerVbos;
    private final int[] layerVertexCounts;
    private final boolean[] layerDirty;

    public Terrain(int width, int height, int depth) {
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.blocks = new BlockType[width][height][depth];

        this.layerVaos = new int[depth];
        this.layerVbos = new int[depth];
        this.layerVertexCounts = new int[depth];
        this.layerDirty = new boolean[depth];

        // 初始化所有方块为空气
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < depth; z++) {
                    blocks[x][y][z] = BlockType.AIR;
                }
            }
        }

        // 为每层创建VAO/VBO
        for (int z = 0; z < depth; z++) {
            layerVaos[z] = glGenVertexArrays();
            layerVbos[z] = glGenBuffers();
            layerVertexCounts[z] = 0;
            layerDirty[z] = true;
        }

        System.out.println("地形初始化完成: " + width + "x" + height + "x" + depth);
    }

    /**
     * 设置方块并标记对应层需要重建
     */
    public void setBlock(int x, int y, int z, BlockType type) {
        if (x >= 0 && x < width && y >= 0 && y < height && z >= 0 && z < depth) {
            blocks[x][y][z] = type;
            layerDirty[z] = true;
        }
    }

    /**
     * 获取方块
     */
    public BlockType getBlock(int x, int y, int z) {
        if (x >= 0 && x < width && y >= 0 && y < height && z >= 0 && z < depth) {
            return blocks[x][y][z];
        }
        return BlockType.AIR;
    }

    /**
     * 渲染指定Z层的2D截面
     */
    public void renderLayer(Renderer renderer, int layerZ) {
        if (layerZ < 0 || layerZ >= depth) return;

        if (layerDirty[layerZ]) {
            buildLayerMesh(layerZ);
            layerDirty[layerZ] = false;
        }

        int vc = layerVertexCounts[layerZ];
        if (vc > 0) {
            glBindVertexArray(layerVaos[layerZ]);
            glDrawArrays(GL_TRIANGLES, 0, vc);
            glBindVertexArray(0);
        }
    }

    /**
     * 为指定Z层构建2D方块顶点数据
     */
    private void buildLayerMesh(int layerZ) {
        List<Float> vertices = new ArrayList<>();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                BlockType block = blocks[x][y][layerZ];
                if (block == BlockType.AIR) continue;

                float[] color = block.getColor();
                float cx = x, cy = y;
                float sz = 1.0f;

                // 2个三角形(6个顶点)，每个顶点: pos(2) + color(4) = 6 floats
                // 三角形1: 左下, 右下, 右上
                // 三角形2: 右上, 左上, 左下
                float[] quad = {
                    cx, cy,         color[0], color[1], color[2], color[3],
                    cx + sz, cy,     color[0], color[1], color[2], color[3],
                    cx + sz, cy + sz, color[0], color[1], color[2], color[3],

                    cx + sz, cy + sz, color[0], color[1], color[2], color[3],
                    cx, cy + sz,     color[0], color[1], color[2], color[3],
                    cx, cy,         color[0], color[1], color[2], color[3],
                };

                for (float v : quad) vertices.add(v);
            }
        }

        float[] arr = new float[vertices.size()];
        for (int i = 0; i < vertices.size(); i++) arr[i] = vertices.get(i);

        int vc = arr.length / 6;
        layerVertexCounts[layerZ] = vc;

        glBindVertexArray(layerVaos[layerZ]);
        glBindBuffer(GL_ARRAY_BUFFER, layerVbos[layerZ]);
        glBufferData(GL_ARRAY_BUFFER, FloatBuffer.wrap(arr), GL_STATIC_DRAW);

        // position (vec2)
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 6 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        // color (vec4)
        glVertexAttribPointer(1, 4, GL_FLOAT, false, 6 * Float.BYTES, 2 * Float.BYTES);
        glEnableVertexAttribArray(1);

        glBindVertexArray(0);
    }

    /**
     * 清理资源
     */
    public void cleanup() {
        for (int z = 0; z < depth; z++) {
            glDeleteVertexArrays(layerVaos[z]);
            glDeleteBuffers(layerVbos[z]);
        }
    }

    // Getter方法
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getDepth() { return depth; }
}