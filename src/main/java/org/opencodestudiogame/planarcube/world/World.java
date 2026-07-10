package org.opencodestudiogame.planarcube.world;

import org.opencodestudiogame.planarcube.engine.Renderer;
import com.flowpowered.noise.Noise;
import com.flowpowered.noise.NoiseQuality;

import java.util.Random;

/**
 * 游戏世界类
 * 管理地形生成和世界状态
 */
public class World {
    private final String gameMode;
    private final Terrain terrain;
    private final Random random;
    
    // 世界尺寸
    private final int worldWidth = 100;
    private final int worldHeight = 50;
    private final int worldDepth = 100;
    
    // 噪声生成器参数
    private double frequency = 0.05;
    private double lacunarity = 2.0;
    private double persistence = 0.5;
    private int octaves = 4;
    
    public World(String gameMode) {
        this.gameMode = gameMode;
        this.random = new Random();
        this.terrain = new Terrain(worldWidth, worldHeight, worldDepth);
        
        generateWorld();
    }
    
    /**
     * 生成世界地形
     */
    private void generateWorld() {
        System.out.println("生成 " + gameMode + " 模式世界...");
        
        if ("open".equals(gameMode)) {
            generateOpenWorld();
        } else {
            generateDefaultWorld();
        }
        
        System.out.println("世界生成完成");
    }
    
    /**
     * 生成开放世界（使用噪声生成随机地形）
     */
    private void generateOpenWorld() {
        System.out.println("使用Perlin噪声生成开放世界地形...");
        
        // 生成地形高度图
        float[][] heightMap = generateHeightMap(worldWidth, worldDepth);
        
        // 根据高度图生成地形块
        for (int x = 0; x < worldWidth; x++) {
            for (int z = 0; z < worldDepth; z++) {
                int height = (int) (heightMap[x][z] * worldHeight);
                
                // 生成地面
                for (int y = 0; y < height; y++) {
                    BlockType blockType;
                    
                    if (y == 0) {
                        blockType = BlockType.BEDROCK; // 底层基岩
                    } else if (y == height - 1) {
                        blockType = BlockType.GRASS; // 顶层草方块
                    } else if (y > height - 4) {
                        blockType = BlockType.DIRT; // 靠近表面的泥土
                    } else {
                        blockType = BlockType.STONE; // 中间的石头
                    }
                    
                    terrain.setBlock(x, y, z, blockType);
                }
                
                // 生成树木（在地面高度足够的地方）
                if (height > 10 && height < worldHeight - 10) {
                    if (random.nextFloat() < 0.02f) { // 2%的几率生成树
                        generateTree(x, height, z);
                    }
                }
            }
        }
        
        // 生成洞穴
        generateCaves();
        
        // 生成水体
        generateWater();
    }
    
    /**
     * 生成默认世界（平坦地形）
     */
    private void generateDefaultWorld() {
        System.out.println("生成默认平坦世界...");
        
        // 简单的平坦世界
        int groundHeight = 20;
        
        for (int x = 0; x < worldWidth; x++) {
            for (int z = 0; z < worldDepth; z++) {
                for (int y = 0; y < groundHeight; y++) {
                    BlockType blockType;
                    
                    if (y == 0) {
                        blockType = BlockType.BEDROCK;
                    } else if (y == groundHeight - 1) {
                        blockType = BlockType.GRASS;
                    } else if (y > groundHeight - 4) {
                        blockType = BlockType.DIRT;
                    } else {
                        blockType = BlockType.STONE;
                    }
                    
                    terrain.setBlock(x, y, z, blockType);
                }
            }
        }
    }
    
    /**
     * 生成高度图（使用Perlin噪声）
     */
    private float[][] generateHeightMap(int width, int depth) {
        float[][] heightMap = new float[width][depth];
        
        // 使用多个噪声层叠加
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < depth; z++) {
                double value = 0.0;
                double amplitude = 1.0;
                double frequency = this.frequency;
                
                for (int o = 0; o < octaves; o++) {
                    double nx = x * frequency;
                    double nz = z * frequency;
                    
                    // 使用Perlin噪声
                    double noiseValue = Noise.gradientCoherentNoise3D(
                        nx, 0.0, nz, 
                        (int) (random.nextLong() & 0x7FFFFFFF), 
                        NoiseQuality.STANDARD
                    );
                    
                    value += noiseValue * amplitude;
                    amplitude *= persistence;
                    frequency *= lacunarity;
                }
                
                // 归一化到0-1范围
                heightMap[x][z] = (float) ((value + 1.0) / 2.0);
            }
        }
        
        return heightMap;
    }
    
    /**
     * 生成一棵树
     */
    private void generateTree(int x, int baseHeight, int z) {
        int treeHeight = 5 + random.nextInt(3); // 树高5-7格
        
        // 生成树干
        for (int y = baseHeight; y < baseHeight + treeHeight; y++) {
            terrain.setBlock(x, y, z, BlockType.WOOD);
        }
        
        // 生成树叶（简单的球形树冠）
        int leavesRadius = 2;
        int leavesCenterY = baseHeight + treeHeight - 1;
        
        for (int dx = -leavesRadius; dx <= leavesRadius; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -leavesRadius; dz <= leavesRadius; dz++) {
                    // 简单的球形判断
                    double distance = Math.sqrt(dx*dx + dy*dy + dz*dz);
                    if (distance <= leavesRadius) {
                        int leafX = x + dx;
                        int leafY = leavesCenterY + dy;
                        int leafZ = z + dz;
                        
                        // 确保在边界内且不是树干位置
                        if (leafX >= 0 && leafX < worldWidth &&
                            leafY >= 0 && leafY < worldHeight &&
                            leafZ >= 0 && leafZ < worldDepth &&
                            !(dx == 0 && dz == 0 && dy >= 0)) {
                            
                            terrain.setBlock(leafX, leafY, leafZ, BlockType.LEAVES);
                        }
                    }
                }
            }
        }
    }
    
    /**
     * 生成洞穴
     */
    private void generateCaves() {
        int numCaves = 10 + random.nextInt(10);
        
        for (int i = 0; i < numCaves; i++) {
            int startX = random.nextInt(worldWidth);
            int startY = 5 + random.nextInt(worldHeight - 10);
            int startZ = random.nextInt(worldDepth);
            
            generateCave(startX, startY, startZ);
        }
    }
    
    /**
     * 生成单个洞穴
     */
    private void generateCave(int startX, int startY, int startZ) {
        int caveLength = 20 + random.nextInt(30);
        float directionX = random.nextFloat() * 2 - 1;
        float directionY = random.nextFloat() * 2 - 1;
        float directionZ = random.nextFloat() * 2 - 1;
        
        // 归一化方向
        float length = (float) Math.sqrt(directionX*directionX + directionY*directionY + directionZ*directionZ);
        directionX /= length;
        directionY /= length;
        directionZ /= length;
        
        int currentX = startX;
        int currentY = startY;
        int currentZ = startZ;
        
        for (int i = 0; i < caveLength; i++) {
            // 生成洞穴球体
            int radius = 2 + random.nextInt(3);
            generateSphere(currentX, currentY, currentZ, radius, BlockType.AIR);
            
            // 移动洞穴中心
            currentX += (int) (directionX * (2 + random.nextInt(3)));
            currentY += (int) (directionY * (1 + random.nextInt(2)));
            currentZ += (int) (directionZ * (2 + random.nextInt(3)));
            
            // 随机改变方向
            if (random.nextFloat() < 0.3f) {
                directionX += random.nextFloat() * 0.5f - 0.25f;
                directionY += random.nextFloat() * 0.3f - 0.15f;
                directionZ += random.nextFloat() * 0.5f - 0.25f;
                
                // 重新归一化
                length = (float) Math.sqrt(directionX*directionX + directionY*directionY + directionZ*directionZ);
                directionX /= length;
                directionY /= length;
                directionZ /= length;
            }
            
            // 确保在边界内
            currentX = Math.max(0, Math.min(worldWidth - 1, currentX));
            currentY = Math.max(5, Math.min(worldHeight - 5, currentY));
            currentZ = Math.max(0, Math.min(worldDepth - 1, currentZ));
        }
    }
    
    /**
     * 生成水体
     */
    private void generateWater() {
        int waterLevel = 15; // 水位高度
        
        for (int x = 0; x < worldWidth; x++) {
            for (int z = 0; z < worldDepth; z++) {
                // 找到该位置的地面高度
                int groundHeight = 0;
                for (int y = worldHeight - 1; y >= 0; y--) {
                    if (terrain.getBlock(x, y, z) != BlockType.AIR) {
                        groundHeight = y;
                        break;
                    }
                }
                
                // 如果地面低于水位，填充水
                if (groundHeight < waterLevel) {
                    for (int y = groundHeight + 1; y <= waterLevel; y++) {
                        if (terrain.getBlock(x, y, z) == BlockType.AIR) {
                            terrain.setBlock(x, y, z, BlockType.WATER);
                        }
                    }
                }
            }
        }
    }
    
    /**
     * 生成球体
     */
    private void generateSphere(int centerX, int centerY, int centerZ, int radius, BlockType blockType) {
        int radiusSq = radius * radius;
        
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x*x + y*y + z*z <= radiusSq) {
                        int blockX = centerX + x;
                        int blockY = centerY + y;
                        int blockZ = centerZ + z;
                        
                        if (blockX >= 0 && blockX < worldWidth &&
                            blockY >= 0 && blockY < worldHeight &&
                            blockZ >= 0 && blockZ < worldDepth) {
                            
                            terrain.setBlock(blockX, blockY, blockZ, blockType);
                        }
                    }
                }
            }
        }
    }
    
    /**
     * 更新世界状态
     */
    public void update(double deltaTime) {
        // 更新世界逻辑（如生物、物理等）
        // 目前为空，可以添加更多功能
    }
    
    /**
     * 渲染世界
     */
    public void render(Renderer renderer) {
        terrain.render(renderer);
    }
    
    /**
     * 清理资源
     */
    public void cleanup() {
        terrain.cleanup();
    }
    
    // Getter方法
    public Terrain getTerrain() { return terrain; }
    public int getWorldWidth() { return worldWidth; }
    public int getWorldHeight() { return worldHeight; }
    public int getWorldDepth() { return worldDepth; }
    public String getGameMode() { return gameMode; }
}