package org.opencodestudiogame.planarcube.world;

import org.opencodestudiogame.planarcube.engine.Renderer;
import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * 地形类，管理方块数据
 */
public class Terrain {
    private final BlockType[][][] blocks;
    private final int width;
    private final int height;
    private final int depth;
    
    // 渲染数据
    private int vao;
    private int vbo;
    private int ebo;
    private int vertexCount;
    private boolean meshDirty = true;
    
    public Terrain(int width, int height, int depth) {
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.blocks = new BlockType[width][height][depth];
        
        // 初始化所有方块为空气
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < depth; z++) {
                    blocks[x][y][z] = BlockType.AIR;
                }
            }
        }
        
        initRendering();
    }
    
    /**
     * 初始化渲染数据
     */
    private void initRendering() {
        vao = glGenVertexArrays();
        vbo = glGenBuffers();
        ebo = glGenBuffers();
        
        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        
        // 设置顶点属性（位置、纹理坐标、法线）
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);
        
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 8 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);
        
        glVertexAttribPointer(2, 3, GL_FLOAT, false, 8 * Float.BYTES, 5 * Float.BYTES);
        glEnableVertexAttribArray(2);
        
        glBindVertexArray(0);
    }
    
    /**
     * 设置方块
     */
    public void setBlock(int x, int y, int z, BlockType type) {
        if (x >= 0 && x < width && y >= 0 && y < height && z >= 0 && z < depth) {
            blocks[x][y][z] = type;
            meshDirty = true;
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
     * 检查方块是否可见（需要渲染）
     */
    private boolean isFaceVisible(int x, int y, int z, FaceDirection direction) {
        int nx = x + direction.dx;
        int ny = y + direction.dy;
        int nz = z + direction.dz;
        
        // 如果相邻方块在边界外，则面可见
        if (nx < 0 || nx >= width || ny < 0 || ny >= height || nz < 0 || nz >= depth) {
            return true;
        }
        
        // 如果相邻方块是透明的，则面可见
        return blocks[nx][ny][nz].isTransparent();
    }
    
    /**
     * 生成地形网格
     */
    private void generateMesh() {
        List<Float> vertices = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        
        // 遍历所有方块
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < depth; z++) {
                    BlockType block = blocks[x][y][z];
                    
                    // 跳过空气方块
                    if (block == BlockType.AIR) {
                        continue;
                    }
                    
                    // 检查每个面是否需要渲染
                    for (FaceDirection face : FaceDirection.values()) {
                        if (isFaceVisible(x, y, z, face)) {
                            addFaceVertices(vertices, indices, x, y, z, face, block);
                        }
                    }
                }
            }
        }
        
        // 上传数据到GPU
        uploadMeshData(vertices, indices);
        meshDirty = false;
    }
    
    /**
     * 添加面的顶点数据
     */
    private void addFaceVertices(List<Float> vertices, List<Integer> indices, 
                                 int x, int y, int z, 
                                 FaceDirection face, BlockType block) {
        float[] faceVertices = face.getVertices(x, y, z);
        float[] texCoords = block.getTextureCoordinates(face);
        float[] normals = face.getNormals();
        
        int baseIndex = vertices.size() / 8;
        
        // 添加4个顶点
        for (int i = 0; i < 4; i++) {
            // 位置
            vertices.add(faceVertices[i*3]);
            vertices.add(faceVertices[i*3 + 1]);
            vertices.add(faceVertices[i*3 + 2]);
            
            // 纹理坐标
            vertices.add(texCoords[i*2]);
            vertices.add(texCoords[i*2 + 1]);
            
            // 法线
            vertices.add(normals[0]);
            vertices.add(normals[1]);
            vertices.add(normals[2]);
        }
        
        // 添加索引（两个三角形）
        indices.add(baseIndex);
        indices.add(baseIndex + 1);
        indices.add(baseIndex + 2);
        indices.add(baseIndex + 2);
        indices.add(baseIndex + 3);
        indices.add(baseIndex);
    }
    
    /**
     * 上传网格数据到GPU
     */
    private void uploadMeshData(List<Float> vertices, List<Integer> indices) {
        // 转换列表到数组
        float[] vertexArray = new float[vertices.size()];
        for (int i = 0; i < vertices.size(); i++) {
            vertexArray[i] = vertices.get(i);
        }
        
        int[] indexArray = new int[indices.size()];
        for (int i = 0; i < indices.size(); i++) {
            indexArray[i] = indices.get(i);
        }
        
        vertexCount = indexArray.length;
        
        // 创建缓冲区
        FloatBuffer vertexBuffer = FloatBuffer.wrap(vertexArray);
        IntBuffer indexBuffer = IntBuffer.wrap(indexArray);
        
        // 绑定VAO
        glBindVertexArray(vao);
        
        // 上传顶点数据
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
        
        // 上传索引数据
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);
        
        glBindVertexArray(0);
    }
    
    /**
     * 渲染地形
     */
    public void render(Renderer renderer) {
        if (meshDirty) {
            generateMesh();
        }
        
        if (vertexCount == 0) {
            return;
        }
        
        glBindVertexArray(vao);
        glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);
    }
    
    /**
     * 清理资源
     */
    public void cleanup() {
        glDeleteVertexArrays(vao);
        glDeleteBuffers(vbo);
        glDeleteBuffers(ebo);
    }
    
    // Getter方法
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getDepth() { return depth; }
    
    /**
     * 面方向枚举
     */
    public enum FaceDirection {
        FRONT(0, 0, 1),
        BACK(0, 0, -1),
        LEFT(-1, 0, 0),
        RIGHT(1, 0, 0),
        TOP(0, 1, 0),
        BOTTOM(0, -1, 0);
        
        public final int dx, dy, dz;
        
        FaceDirection(int dx, int dy, int dz) {
            this.dx = dx;
            this.dy = dy;
            this.dz = dz;
        }
        
        public float[] getVertices(int x, int y, int z) {
            float fx = x;
            float fy = y;
            float fz = z;
            
            switch (this) {
                case FRONT:
                    return new float[] {
                        fx, fy, fz + 1,
                        fx + 1, fy, fz + 1,
                        fx + 1, fy + 1, fz + 1,
                        fx, fy + 1, fz + 1
                    };
                case BACK:
                    return new float[] {
                        fx + 1, fy, fz,
                        fx, fy, fz,
                        fx, fy + 1, fz,
                        fx + 1, fy + 1, fz
                    };
                case LEFT:
                    return new float[] {
                        fx, fy, fz,
                        fx, fy, fz + 1,
                        fx, fy + 1, fz + 1,
                        fx, fy + 1, fz
                    };
                case RIGHT:
                    return new float[] {
                        fx + 1, fy, fz + 1,
                        fx + 1, fy, fz,
                        fx + 1, fy + 1, fz,
                        fx + 1, fy + 1, fz + 1
                    };
                case TOP:
                    return new float[] {
                        fx, fy + 1, fz,
                        fx + 1, fy + 1, fz,
                        fx + 1, fy + 1, fz + 1,
                        fx, fy + 1, fz + 1
                    };
                case BOTTOM:
                    return new float[] {
                        fx, fy, fz + 1,
                        fx + 1, fy, fz + 1,
                        fx + 1, fy, fz,
                        fx, fy, fz
                    };
                default:
                    return new float[12];
            }
        }
        
        public float[] getNormals() {
            switch (this) {
                case FRONT: return new float[] {0, 0, 1};
                case BACK: return new float[] {0, 0, -1};
                case LEFT: return new float[] {-1, 0, 0};
                case RIGHT: return new float[] {1, 0, 0};
                case TOP: return new float[] {0, 1, 0};
                case BOTTOM: return new float[] {0, -1, 0};
                default: return new float[] {0, 0, 0};
            }
        }
    }
}