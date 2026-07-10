package org.opencodestudiogame.planarcube;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * 游戏配置类
 * 管理游戏设置和配置文件
 */
public class Config {
    private static final String CONFIG_FILE = "config.json";
    
    // 图形设置
    public int windowWidth = 1280;
    public int windowHeight = 720;
    public boolean fullscreen = false;
    public boolean vsync = true;
    public int targetFps = 60;
    
    // 游戏设置
    public float mouseSensitivity = 1.0f;
    public float movementSpeed = 5.0f;
    public float jumpStrength = 8.0f;
    public float gravity = 20.0f;
    
    // 声音设置
    public float masterVolume = 1.0f;
    public float musicVolume = 0.8f;
    public float soundVolume = 1.0f;
    
    // 控制设置
    public int keyForward = 87;    // W
    public int keyBackward = 83;   // S
    public int keyLeft = 65;       // A
    public int keyRight = 68;      // D
    public int keyJump = 32;       // Space
    public int keyToggleSpace = 69; // E (空间切换)
    
    // 其他设置
    public String skinPath = "assets/skins/steve.png";
    public String lastGameMode = "open";
    
    private static Config instance;
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    
    private Config() {
        // 私有构造函数
    }
    
    /**
     * 获取配置单例
     */
    public static Config getInstance() {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }
    
    /**
     * 加载配置
     */
    public boolean load() {
        try {
            if (!Files.exists(Paths.get(CONFIG_FILE))) {
                System.out.println("配置文件不存在，使用默认配置");
                return false;
            }
            
            FileReader reader = new FileReader(CONFIG_FILE);
            Config loadedConfig = gson.fromJson(reader, Config.class);
            reader.close();
            
            // 复制加载的配置到当前实例
            copyFrom(loadedConfig);
            System.out.println("配置加载成功");
            return true;
            
        } catch (IOException e) {
            System.err.println("加载配置失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 保存配置
     */
    public boolean save() {
        try {
            FileWriter writer = new FileWriter(CONFIG_FILE);
            gson.toJson(this, writer);
            writer.close();
            System.out.println("配置保存成功");
            return true;
        } catch (IOException e) {
            System.err.println("保存配置失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 从另一个配置对象复制值
     */
    private void copyFrom(Config other) {
        this.windowWidth = other.windowWidth;
        this.windowHeight = other.windowHeight;
        this.fullscreen = other.fullscreen;
        this.vsync = other.vsync;
        this.targetFps = other.targetFps;
        
        this.mouseSensitivity = other.mouseSensitivity;
        this.movementSpeed = other.movementSpeed;
        this.jumpStrength = other.jumpStrength;
        this.gravity = other.gravity;
        
        this.masterVolume = other.masterVolume;
        this.musicVolume = other.musicVolume;
        this.soundVolume = other.soundVolume;
        
        this.keyForward = other.keyForward;
        this.keyBackward = other.keyBackward;
        this.keyLeft = other.keyLeft;
        this.keyRight = other.keyRight;
        this.keyJump = other.keyJump;
        this.keyToggleSpace = other.keyToggleSpace;
        
        this.skinPath = other.skinPath;
        this.lastGameMode = other.lastGameMode;
    }
    
    /**
     * 重置为默认配置
     */
    public void resetToDefaults() {
        windowWidth = 1280;
        windowHeight = 720;
        fullscreen = false;
        vsync = true;
        targetFps = 60;
        
        mouseSensitivity = 1.0f;
        movementSpeed = 5.0f;
        jumpStrength = 8.0f;
        gravity = 20.0f;
        
        masterVolume = 1.0f;
        musicVolume = 0.8f;
        soundVolume = 1.0f;
        
        keyForward = 87;    // W
        keyBackward = 83;   // S
        keyLeft = 65;       // A
        keyRight = 68;      // D
        keyJump = 32;       // Space
        keyToggleSpace = 69; // E
        
        skinPath = "assets/skins/steve.png";
        lastGameMode = "open";
        
        System.out.println("配置已重置为默认值");
    }
    
    /**
     * 验证配置值
     */
    public boolean validate() {
        boolean valid = true;
        
        if (windowWidth < 640 || windowWidth > 3840) {
            System.err.println("窗口宽度无效: " + windowWidth);
            valid = false;
        }
        
        if (windowHeight < 480 || windowHeight > 2160) {
            System.err.println("窗口高度无效: " + windowHeight);
            valid = false;
        }
        
        if (targetFps < 30 || targetFps > 240) {
            System.err.println("目标FPS无效: " + targetFps);
            valid = false;
        }
        
        if (movementSpeed <= 0 || movementSpeed > 50) {
            System.err.println("移动速度无效: " + movementSpeed);
            valid = false;
        }
        
        if (masterVolume < 0 || masterVolume > 1) {
            System.err.println("主音量无效: " + masterVolume);
            valid = false;
        }
        
        return valid;
    }
    
    /**
     * 打印当前配置
     */
    public void print() {
        System.out.println("=== 当前配置 ===");
        System.out.println("窗口: " + windowWidth + "x" + windowHeight + 
                         " (全屏: " + fullscreen + ", V-Sync: " + vsync + ", FPS: " + targetFps + ")");
        System.out.println("游戏: 灵敏度=" + mouseSensitivity + 
                         ", 移动速度=" + movementSpeed + 
                         ", 跳跃强度=" + jumpStrength + 
                         ", 重力=" + gravity);
        System.out.println("音量: 主音量=" + masterVolume + 
                         ", 音乐=" + musicVolume + 
                         ", 音效=" + soundVolume);
        System.out.println("控制: W=" + keyForward + 
                         ", S=" + keyBackward + 
                         ", A=" + keyLeft + 
                         ", D=" + keyRight + 
                         ", 空格=" + keyJump + 
                         ", E=" + keyToggleSpace);
        System.out.println("其他: 皮肤路径=" + skinPath + 
                         ", 最后游戏模式=" + lastGameMode);
        System.out.println("================");
    }
}