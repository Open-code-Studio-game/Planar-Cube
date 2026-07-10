package org.opencodestudiogame.planarcube.world;

import org.opencodestudiogame.planarcube.world.Terrain.FaceDirection;

/**
 * 方块类型枚举
 */
public enum BlockType {
    AIR(0, false, true, new float[]{0, 0, 0, 0, 0, 0}),
    GRASS(1, false, false, new float[]{0, 0, 0.25f, 0, 0.25f, 0}),
    DIRT(2, false, false, new float[]{0.25f, 0, 0.25f, 0, 0.25f, 0}),
    STONE(3, false, false, new float[]{0.5f, 0, 0.5f, 0, 0.5f, 0}),
    WOOD(4, false, false, new float[]{0.75f, 0, 0.75f, 0, 0.75f, 0}),
    LEAVES(5, true, false, new float[]{0, 0.25f, 0, 0.25f, 0, 0.25f}),
    WATER(6, true, true, new float[]{0.25f, 0.25f, 0.25f, 0.25f, 0.25f, 0.25f}),
    BEDROCK(7, false, false, new float[]{0.5f, 0.25f, 0.5f, 0.25f, 0.5f, 0.25f});
    
    private final int id;
    private final boolean transparent;
    private final boolean liquid;
    private final float[] texCoords; // 6个面的纹理坐标 [top, bottom, front, back, left, right]
    
    BlockType(int id, boolean transparent, boolean liquid, float[] texCoords) {
        this.id = id;
        this.transparent = transparent;
        this.liquid = liquid;
        this.texCoords = texCoords;
    }
    
    public int getId() {
        return id;
    }
    
    public boolean isTransparent() {
        return transparent;
    }
    
    public boolean isLiquid() {
        return liquid;
    }
    
    /**
     * 获取指定面的纹理坐标
     */
    public float[] getTextureCoordinates(FaceDirection face) {
        // 简化版：每个面使用相同的纹理坐标
        // 实际应该根据方块类型和面类型返回不同的坐标
        float u, v;
        
        switch (face) {
            case TOP:
                u = texCoords[0];
                v = texCoords[1];
                break;
            case BOTTOM:
                u = texCoords[2];
                v = texCoords[3];
                break;
            default: // FRONT, BACK, LEFT, RIGHT
                u = texCoords[4];
                v = texCoords[5];
                break;
        }
        
        // 返回4个顶点的纹理坐标
        return new float[] {
            u, v,
            u + 0.25f, v,
            u + 0.25f, v + 0.25f,
            u, v + 0.25f
        };
    }
    
    /**
     * 根据ID获取方块类型
     */
    public static BlockType fromId(int id) {
        for (BlockType type : values()) {
            if (type.id == id) {
                return type;
            }
        }
        return AIR;
    }
    
    /**
     * 获取方块颜色（用于调试渲染）
     */
    public float[] getColor() {
        switch (this) {
            case GRASS:
                return new float[]{0.0f, 0.8f, 0.0f, 1.0f};
            case DIRT:
                return new float[]{0.6f, 0.4f, 0.2f, 1.0f};
            case STONE:
                return new float[]{0.5f, 0.5f, 0.5f, 1.0f};
            case WOOD:
                return new float[]{0.6f, 0.4f, 0.2f, 1.0f};
            case LEAVES:
                return new float[]{0.0f, 0.6f, 0.0f, 1.0f};
            case WATER:
                return new float[]{0.0f, 0.4f, 0.8f, 0.7f};
            case BEDROCK:
                return new float[]{0.2f, 0.2f, 0.2f, 1.0f};
            default:
                return new float[]{0.0f, 0.0f, 0.0f, 0.0f};
        }
    }
}