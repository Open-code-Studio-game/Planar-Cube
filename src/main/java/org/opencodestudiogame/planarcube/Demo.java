package org.opencodestudiogame.planarcube;

/**
 * 游戏演示类
 * 展示游戏的基本功能和特性
 */
public class Demo {
    
    public static void main(String[] args) {
        System.out.println("=== Planar Cube 游戏演示 ===");
        System.out.println();
        
        // 演示游戏特性
        System.out.println("游戏特色:");
        System.out.println("1. 独特的空间移动机制");
        System.out.println("   - W/S键: 在不同空间（立方体面）之间移动");
        System.out.println("   - A/D键: 在当前空间内前后移动");
        System.out.println("   - 视角固定，玩家始终位于屏幕中心");
        System.out.println();
        
        System.out.println("2. 支持MC皮肤文件");
        System.out.println("   - 可以加载Minecraft皮肤文件");
        System.out.println("   - 支持默认史蒂夫皮肤");
        System.out.println("   - 自定义皮肤支持");
        System.out.println();
        
        System.out.println("3. 两种游戏模式");
        System.out.println("   - 开放模式: 使用Perlin噪声生成随机地形");
        System.out.println("   - 探索模式: 预设的冒险地图（待实现）");
        System.out.println();
        
        System.out.println("4. 技术特性");
        System.out.println("   - 使用Java + LWJGL 3开发");
        System.out.println("   - 3D图形渲染");
        System.out.println("   - 程序化地形生成");
        System.out.println("   - 完整的光照和纹理系统");
        System.out.println();
        
        // 演示配置系统
        System.out.println("配置系统演示:");
        Config config = Config.getInstance();
        config.load();
        config.print();
        System.out.println();
        
        // 演示游戏状态
        System.out.println("游戏状态演示:");
        System.out.println("初始状态: " + GameStateManager.getCurrentState());
        GameStateManager.startNewGame("open");
        System.out.println("开始游戏后: " + GameStateManager.getCurrentState());
        GameStateManager.togglePause();
        System.out.println("暂停后: " + GameStateManager.getCurrentState());
        GameStateManager.togglePause();
        System.out.println("恢复后: " + GameStateManager.getCurrentState());
        GameStateManager.gameOver();
        System.out.println("游戏结束后: " + GameStateManager.getCurrentState());
        System.out.println();
        
        // 演示方块类型
        System.out.println("方块类型演示:");
        System.out.println("草方块: " + org.opencodestudiogame.planarcube.world.BlockType.GRASS);
        System.out.println("泥土: " + org.opencodestudiogame.planarcube.world.BlockType.DIRT);
        System.out.println("石头: " + org.opencodestudiogame.planarcube.world.BlockType.STONE);
        System.out.println("木头: " + org.opencodestudiogame.planarcube.world.BlockType.WOOD);
        System.out.println("树叶: " + org.opencodestudiogame.planarcube.world.BlockType.LEAVES);
        System.out.println("水: " + org.opencodestudiogame.planarcube.world.BlockType.WATER);
        System.out.println("基岩: " + org.opencodestudiogame.planarcube.world.BlockType.BEDROCK);
        System.out.println();
        
        // 演示玩家移动
        System.out.println("玩家移动演示:");
        org.opencodestudiogame.planarcube.entity.Player player = new org.opencodestudiogame.planarcube.entity.Player();
        System.out.println("玩家初始位置: (" + player.getX() + ", " + player.getY() + ", " + player.getZ() + ")");
        System.out.println("玩家初始空间: " + player.getCurrentSpace());
        
        // 演示空间移动
        player.moveBetweenSpaces(1);
        System.out.println("按W键后 - 空间: " + player.getCurrentSpace());
        
        player.moveBetweenSpaces(-1);
        System.out.println("按S键后 - 空间: " + player.getCurrentSpace());
        
        // 演示平面移动
        player.moveInSpace(1, 0);
        System.out.println("按D键后 - 位置: (" + player.getX() + ", " + player.getY() + ", " + player.getZ() + ")");
        
        player.moveInSpace(-1, 0);
        System.out.println("按A键后 - 位置: (" + player.getX() + ", " + player.getY() + ", " + player.getZ() + ")");
        System.out.println();
        
        // 演示世界生成
        System.out.println("世界生成演示:");
        org.opencodestudiogame.planarcube.world.World openWorld = 
            new org.opencodestudiogame.planarcube.world.World("open");
        System.out.println("开放模式世界创建完成");
        System.out.println("世界尺寸: " + openWorld.getWorldWidth() + "x" + 
                         openWorld.getWorldHeight() + "x" + openWorld.getWorldDepth());
        System.out.println("游戏模式: " + openWorld.getGameMode());
        System.out.println();
        
        System.out.println("=== 演示完成 ===");
        System.out.println();
        System.out.println("要运行完整游戏，请执行:");
        System.out.println("1. 安装Maven: https://maven.apache.org/");
        System.out.println("2. 编译项目: mvn clean compile");
        System.out.println("3. 运行游戏: mvn exec:java -Dexec.mainClass=\"org.opencodestudiogame.planarcube.Main\"");
        System.out.println("或直接运行: java -jar target/planar-cube-1.0.0.jar");
        System.out.println();
        System.out.println("游戏控制:");
        System.out.println("- W/S: 在不同空间之间移动");
        System.out.println("- A/D: 在当前空间内前后移动");
        System.out.println("- ESC: 退出游戏");
    }
}