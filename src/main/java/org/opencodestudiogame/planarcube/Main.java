package org.opencodestudiogame.planarcube;

import org.opencodestudiogame.planarcube.engine.GameEngine;
import org.opencodestudiogame.planarcube.engine.GameConfig;

/**
 * 游戏主入口类
 */
public class Main {
    
    public static void main(String[] args) {
        System.out.println("启动 Planar Cube 游戏...");
        
        GameConfig config = new GameConfig()
            .setTitle("Planar Cube")
            .setWidth(1280)
            .setHeight(720)
            .setFullscreen(false)
            .setVsync(true);
        
        try {
            GameEngine engine = new GameEngine(config);
            engine.run();
        } catch (Exception e) {
            System.err.println("游戏启动失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}