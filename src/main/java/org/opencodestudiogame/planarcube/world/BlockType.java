package org.opencodestudiogame.planarcube.world;

/**
 * 方块类型枚举，支持2D渲染颜色
 */
public enum BlockType {
    AIR(0, false, true),
    GRASS(1, false, false),
    DIRT(2, false, false),
    STONE(3, false, false),
    WOOD(4, false, false),
    LEAVES(5, true, false),
    WATER(6, true, true),
    BEDROCK(7, false, false);
    
    private final int id;
    private final boolean transparent;
    private final boolean liquid;
    
    BlockType(int id, boolean transparent, boolean liquid) {
        this.id = id;
        this.transparent = transparent;
        this.liquid = liquid;
    }
    
    public int getId() { return id; }
    public boolean isTransparent() { return transparent; }
    public boolean isLiquid() { return liquid; }
    
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
     * 获取方块2D渲染颜色 RGBA
     */
    public float[] getColor() {
        switch (this) {
            case GRASS:  return new float[]{0.357f, 0.541f, 0.165f, 1.0f};  // #5B8A2A
            case DIRT:   return new float[]{0.545f, 0.369f, 0.243f, 1.0f};  // #8B5E3C
            case STONE:  return new float[]{0.502f, 0.502f, 0.502f, 1.0f};  // #808080
            case WOOD:   return new float[]{0.420f, 0.259f, 0.149f, 1.0f};  // #6B4226
            case LEAVES: return new float[]{0.227f, 0.490f, 0.196f, 1.0f};  // #3A7D32
            case WATER:  return new float[]{0.290f, 0.565f, 0.851f, 0.7f};  // #4A90D9
            case BEDROCK: return new float[]{0.200f, 0.200f, 0.200f, 1.0f}; // #333333
            default:     return new float[]{0.0f, 0.0f, 0.0f, 0.0f};
        }
    }
}