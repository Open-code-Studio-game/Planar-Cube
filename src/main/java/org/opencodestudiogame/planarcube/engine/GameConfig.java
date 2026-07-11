package org.opencodestudiogame.planarcube.engine;

/**
 * 游戏配置类
 */
public class GameConfig {
    private String title = "Planar Cube";
    private int width = 1280;
    private int height = 720;
    private boolean fullscreen = false;
    private boolean vsync = true;
    private int targetFps = 60;
    private String gameMode = "open"; // open 或 adventure

    // 2D切片设置
    private int tileSize = 32;          // 每个方块的像素大小（默认32）
    private int viewWidthTiles = 40;    // 视口水平可见方块数
    private double viewHeightTiles = 22.5; // 视口垂直可见方块数
    
    public GameConfig() {
    }
    
    public String getTitle() {
        return title;
    }
    
    public GameConfig setTitle(String title) {
        this.title = title;
        return this;
    }
    
    public int getWidth() {
        return width;
    }
    
    public GameConfig setWidth(int width) {
        this.width = width;
        return this;
    }
    
    public int getHeight() {
        return height;
    }
    
    public GameConfig setHeight(int height) {
        this.height = height;
        return this;
    }
    
    public boolean isFullscreen() {
        return fullscreen;
    }
    
    public GameConfig setFullscreen(boolean fullscreen) {
        this.fullscreen = fullscreen;
        return this;
    }
    
    public boolean isVsync() {
        return vsync;
    }
    
    public GameConfig setVsync(boolean vsync) {
        this.vsync = vsync;
        return this;
    }
    
    public int getTargetFps() {
        return targetFps;
    }
    
    public GameConfig setTargetFps(int targetFps) {
        this.targetFps = targetFps;
        return this;
    }
    
    public String getGameMode() {
        return gameMode;
    }
    
    public GameConfig setGameMode(String gameMode) {
        this.gameMode = gameMode;
        return this;
    }
}