package org.opencodestudiogame.planarcube.entity;

import org.opencodestudiogame.planarcube.engine.Renderer;
import org.opencodestudiogame.planarcube.world.World;

/**
 * 玩家实体类
 * 支持在垂直切片之间移动（W/S键）和在当前层内左右移动（A/D键）
 */
public class Player {
    // 玩家位置
    private float x, y;
    private int currentLayer = 0;
    
    // 物理属性
    private float velocityY = 0.0f;
    private boolean isOnGround = false;
    private float width = 1.0f;
    private float height = 2.0f;
    
    // 移动速度
    private float moveSpeed = 8.0f; // 方块/秒
    private float jumpForce = 12.0f; // 跳跃力度
    private float gravity = 15.0f; // 重力加速度（方块/秒²）
    
    // 动画状态
    private boolean isMoving = false;
    private boolean isJumping = false;
    private float animationTime = 0.0f;
    
    // 皮肤相关
    private Skin skin;
    
    // 世界引用（用于碰撞检测）
    private World world;
    
    public Player(World world) {
        this.world = world;
        this.x = world.getWorldWidth() / 2.0f; // 初始在中间
        this.y = world.getGroundHeight((int)x, currentLayer) + 1.0f; // 在地面高度+1
        this.skin = new Skin();
    }
    
    public Player(World world, float x, float y, int layer) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.currentLayer = layer;
        this.skin = new Skin();
    }
    
    /**
     * 在垂直切片之间移动
     * @param direction 方向：1表示向前（Z+1），-1表示向后（Z-1）
     */
    public void moveToLayer(int direction) {
        if (direction != 0 && world != null) {
            int targetLayer = currentLayer + direction;
            
            // 确保层索引在有效范围内
            if (targetLayer >= 0 && targetLayer < world.getWorldDepth()) {
                // 检查目标层当前位置是否有固体方块
                int playerX = (int) x;
                int playerY = (int) y;
                
                if (world.isSolid(playerX, playerY, targetLayer)) {
                    // 如果有固体方块，寻找最近的空位
                    int newX = findNearestEmptySpace(playerX, playerY, targetLayer);
                    if (newX != -1) {
                        x = newX + 0.5f; // 移动到方块中心
                    } else {
                        return; // 找不到空位，不移动
                    }
                }
                
                currentLayer = targetLayer;
                System.out.println("切换到层: " + currentLayer);
            }
        }
    }
    
    /**
     * 在当前层内水平移动
     * @param direction 方向：1表示向右，-1表示向左
     */
    public void moveHorizontal(int direction) {
        if (direction != 0 && world != null) {
            float targetX = x + direction * moveSpeed * 0.016f; // 假设60FPS
            
            // 检查碰撞
            if (!checkCollision(targetX, y, currentLayer)) {
                x = targetX;
                isMoving = true;
                animationTime = 0.0f;
                
                // 更新皮肤动画状态
                if (skin != null) {
                    skin.setWalking(true);
                }
                
                System.out.println(String.format("玩家水平移动到: X=%.2f, Y=%.2f, 层=%d", x, y, currentLayer));
            }
        }
    }
    
    /**
     * 执行跳跃
     */
    public void jump() {
        if (isOnGround && !isJumping) {
            velocityY = jumpForce;
            isOnGround = false;
            isJumping = true;
            
            // 更新皮肤动画状态
            if (skin != null) {
                skin.setJumping(true);
            }
            
            System.out.println("玩家跳跃");
        }
    }
    
    /**
     * 检查碰撞
     */
    private boolean checkCollision(float targetX, float targetY, int layer) {
        if (world == null) return false;
        
        // 检查玩家边界（简化：检查底部中心点）
        int checkX = (int) targetX;
        int checkY = (int) targetY;
        
        // 检查玩家是否碰到固体方块
        return world.isSolid(checkX, checkY, layer) || 
               world.isSolid(checkX, (int)(targetY + height - 0.1f), layer) ||
               world.isSolid((int)(targetX + width - 0.1f), checkY, layer) ||
               world.isSolid((int)(targetX + width - 0.1f), (int)(targetY + height - 0.1f), layer);
    }
    
    /**
     * 寻找最近的空位
     */
    private int findNearestEmptySpace(int startX, int startY, int layer) {
        if (world == null) return -1;
        
        int maxSearchRadius = 10;
        
        // 从当前位置向外搜索
        for (int radius = 1; radius <= maxSearchRadius; radius++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dy = -1; dy <= 1; dy++) { // 只检查上下1格
                    int checkX = startX + dx;
                    int checkY = startY + dy;
                    
                    if (checkX >= 0 && checkX < world.getWorldWidth() &&
                        checkY >= 0 && checkY < world.getWorldHeight()) {
                        
                        if (!world.isSolid(checkX, checkY, layer) &&
                            !world.isSolid(checkX, checkY + 1, layer)) {
                            return checkX;
                        }
                    }
                }
            }
        }
        
        return -1; // 未找到空位
    }
    
    /**
     * 更新玩家状态
     */
    public void update(double deltaTime) {
        // 应用重力
        velocityY -= gravity * deltaTime;
        
        // 垂直移动
        float targetY = y + velocityY * deltaTime;
        
        // 垂直碰撞检测
        if (velocityY < 0) { // 下落
            if (!checkCollision(x, targetY, currentLayer)) {
                y = targetY;
                isOnGround = false;
            } else {
                // 落地
                y = (int)targetY + 1.0f; // 站在方块上
                velocityY = 0.0f;
                isOnGround = true;
                isJumping = false;
                
                if (skin != null) {
                    skin.setJumping(false);
                }
            }
        } else { // 上升
            if (!checkCollision(x, targetY + height - 0.1f, currentLayer)) {
                y = targetY;
                isOnGround = false;
            } else {
                // 碰到天花板
                velocityY = 0.0f;
            }
        }
        
        // 边界检查
        if (x < 0) x = 0;
        if (x > world.getWorldWidth() - width) x = world.getWorldWidth() - width;
        if (y < 0) {
            y = 0;
            velocityY = 0.0f;
            isOnGround = true;
            isJumping = false;
        }
        if (y > world.getWorldHeight() - height) {
            y = world.getWorldHeight() - height;
            velocityY = 0.0f;
        }
        
        // 更新动画状态
        if (isMoving) {
            animationTime += deltaTime;
            if (animationTime > 0.5f) { // 动画持续时间
                isMoving = false;
                if (skin != null) {
                    skin.setWalking(false);
                }
            }
        }
        
        // 更新皮肤动画
        if (skin != null) {
            skin.update(deltaTime);
        }
    }
    
    /**
     * 渲染玩家（2D渲染）
     */
    public void render(Renderer renderer) {
        if (skin != null) {
            // 在2D正交投影下渲染玩家
            // 注意：这里需要将3D坐标转换为2D屏幕坐标
            // 玩家的Z坐标固定为当前层的深度，渲染在2D平面上
            float screenX = x; // 直接使用X坐标（会在渲染器中转换为屏幕坐标）
            float screenY = y; // 直接使用Y坐标
            
            // 渲染玩家精灵
            skin.render(renderer, screenX, screenY, currentLayer, width, height);
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