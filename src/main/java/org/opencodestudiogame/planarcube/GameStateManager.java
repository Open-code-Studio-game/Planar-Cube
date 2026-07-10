package org.opencodestudiogame.planarcube;

/**
 * 游戏状态管理类
 */
public class GameStateManager {
    private static GameStateEnum currentState = GameStateEnum.MENU;
    private static String currentGameMode = "open"; // open 或 adventure
    
    // 不允许实例化
    private GameStateManager() {}
    
    /**
     * 获取当前游戏状态
     */
    public static GameStateEnum getCurrentState() {
        return currentState;
    }
    
    /**
     * 设置游戏状态
     */
    public static void setState(GameStateEnum state) {
        currentState = state;
        System.out.println("游戏状态切换为: " + state);
    }
    
    /**
     * 获取当前游戏模式
     */
    public static String getCurrentGameMode() {
        return currentGameMode;
    }
    
    /**
     * 设置游戏模式
     */
    public static void setGameMode(String mode) {
        if ("open".equals(mode) || "adventure".equals(mode)) {
            currentGameMode = mode;
            System.out.println("游戏模式设置为: " + mode);
        } else {
            System.err.println("无效的游戏模式: " + mode);
        }
    }
    
    /**
     * 检查是否在游戏中
     */
    public static boolean isPlaying() {
        return currentState == GameStateEnum.PLAYING;
    }
    
    /**
     * 检查游戏是否暂停
     */
    public static boolean isPaused() {
        return currentState == GameStateEnum.PAUSED;
    }
    
    /**
     * 检查是否在菜单中
     */
    public static boolean isInMenu() {
        return currentState == GameStateEnum.MENU;
    }
    
    /**
     * 检查游戏是否结束
     */
    public static boolean isGameOver() {
        return currentState == GameStateEnum.GAME_OVER;
    }
    
    /**
     * 切换暂停状态
     */
    public static void togglePause() {
        if (currentState == GameStateEnum.PLAYING) {
            currentState = GameStateEnum.PAUSED;
            System.out.println("游戏已暂停");
        } else if (currentState == GameStateEnum.PAUSED) {
            currentState = GameStateEnum.PLAYING;
            System.out.println("游戏已恢复");
        }
    }
    
    /**
     * 开始新游戏
     */
    public static void startNewGame(String mode) {
        setGameMode(mode);
        currentState = GameStateEnum.PLAYING;
        System.out.println("开始新游戏 - 模式: " + mode);
    }
    
    /**
     * 返回主菜单
     */
    public static void returnToMenu() {
        currentState = GameStateEnum.MENU;
        System.out.println("返回主菜单");
    }
    
    /**
     * 游戏结束
     */
    public static void gameOver() {
        currentState = GameStateEnum.GAME_OVER;
        System.out.println("游戏结束");
    }
}