package org.opencodestudiogame.planarcube.entity;

import org.opencodestudiogame.planarcube.engine.Renderer;

/**
 * 玩家实体类
 * 支持在不同空间移动（W/S键）和在当前空间前后移动（A/D键）
 */
public class Player {
    // 玩家位置
    private float x, y, z;
    
    // 当前所在空间（面）
    private int currentSpace = 0;
    
    // 玩家尺寸
    private float width = 1.0f;
    private float height = 2.0f;
    
    // 移动速度
    private float moveSpeed = 5.0f;
    private float spaceTransitionSpeed = 2.0f;
    
    // 动画状态
    private boolean isMoving = false;
    private float animationTime = 0.0f;
    
    // 皮肤相关
    private Skin skin;
    
    public Player() {
        this.x = 0.0f;
        this.y = 0.0f;
        this.z = 0.0f;
        this.skin = new Skin();
    }
    
    public Player(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.skin = new Skin();
    }
    
    /**
     * 在不同空间之间移动
     * @param direction 方向：1表示向上一个空间，-1表示向下一个空间
     */
    public void moveBetweenSpaces(int direction) {
        if (direction != 0) {
            // 在不同空间之间过渡
            // 这里可以实现空间过渡的动画效果
            currentSpace += direction;
            
            // 确保空间索引在有效范围内
            if (currentSpace < 0) currentSpace = 0;
            if (currentSpace > 5) currentSpace = 5; // 假设有6个面
            
            System.out.println("切换到空间: " + currentSpace);
            
            // 根据当前空间更新玩家位置
            updatePositionForSpace();
        }
    }
    
    /**
     * 在当前空间内移动
     * @param dx X方向移动量
     * @param dy Y方向移动量
     */
    public void moveInSpace(float dx, float dy) {
        // 根据当前空间调整移动方向
        float[] adjustedMove = adjustMovementForSpace(dx, dy);
        
        this.x += adjustedMove[0] * moveSpeed;
        this.y += adjustedMove[1] * moveSpeed;
        
        isMoving = true;
        animationTime = 0.0f;
        
        System.out.println(String.format("玩家移动到: (%.2f, %.2f, %.2f)", x, y, z));
    }
    
    /**
     * 根据当前空间调整移动方向
     */
    private float[] adjustMovementForSpace(float dx, float dy) {
        // 根据当前空间（立方体的面）调整移动方向
        // 这里简化处理，实际应根据3D坐标变换
        float[] result = new float[2];
        
        switch (currentSpace) {
            case 0: // 前面
                result[0] = dx;  // 左右移动
                result[1] = dy;  // 上下移动
                break;
            case 1: // 后面
                result[0] = -dx; // 反转左右
                result[1] = dy;
                break;
            case 2: // 左面
                result[0] = -dy; // 上下变为左右
                result[1] = dx;  // 左右变为上下
                break;
            case 3: // 右面
                result[0] = dy;
                result[1] = -dx;
                break;
            case 4: // 上面
                result[0] = dx;
                result[1] = -dy; // 反转上下
                break;
            case 5: // 下面
                result[0] = dx;
                result[1] = dy;
                break;
            default:
                result[0] = dx;
                result[1] = dy;
        }
        
        return result;
    }
    
    /**
     * 根据当前空间更新玩家位置
     */
    private void updatePositionForSpace() {
        // 根据空间索引计算3D位置
        // 这里简化处理，实际应根据立方体面的坐标计算
        switch (currentSpace) {
            case 0: // 前面
                z = -5.0f;
                break;
            case 1: // 后面
                z = 5.0f;
                break;
            case 2: // 左面
                x = -5.0f;
                break;
            case 3: // 右面
                x = 5.0f;
                break;
            case 4: // 上面
                y = 5.0f;
                break;
            case 5: // 下面
                y = -5.0f;
                break;
        }
    }
    
    /**
     * 更新玩家状态
     */
    public void update(double deltaTime) {
        if (isMoving) {
            animationTime += deltaTime;
            if (animationTime > 0.5f) { // 动画持续时间
                isMoving = false;
            }
        }
        
        // 更新皮肤动画
        if (skin != null) {
            skin.update(deltaTime);
        }
    }
    
    /**
     * 渲染玩家
     */
    public void render(Renderer renderer) {
        // 这里应该实现玩家的渲染逻辑
        // 根据皮肤和动画状态渲染玩家模型
        
        if (skin != null) {
            skin.render(renderer, x, y, z, width, height);
        }
    }
    
    /**
     * 加载MC皮肤
     */
    public boolean loadSkin(String skinPath) {
        if (skin == null) {
            skin = new Skin();
        }
        return skin.loadFromFile(skinPath);
    }
    
    // Getter 方法
    public float getX() { return x; }
    public float getY() { return y; }
    public float getZ() { return z; }
    public int getCurrentSpace() { return currentSpace; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public Skin getSkin() { return skin; }
    
    // Setter 方法
    public void setPosition(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public void setCurrentSpace(int space) {
        this.currentSpace = space;
        updatePositionForSpace();
    }
    
    public void setSkin(Skin skin) {
        this.skin = skin;
    }
}