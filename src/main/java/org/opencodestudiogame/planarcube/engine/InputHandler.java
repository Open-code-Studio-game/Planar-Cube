package org.opencodestudiogame.planarcube.engine;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;

/**
 * 输入处理器类
 */
public class InputHandler {
    private final long window;
    private final Map<Integer, Boolean> keyStates = new HashMap<>();
    
    public InputHandler(long window) {
        this.window = window;
    }
    
    public void handleKey(int key, int action) {
        if (action == GLFW_PRESS) {
            keyStates.put(key, true);
        } else if (action == GLFW_RELEASE) {
            keyStates.put(key, false);
        }
    }
    
    public boolean isKeyPressed(int key) {
        return keyStates.getOrDefault(key, false);
    }
    
    public boolean isKeyDown(int key) {
        return glfwGetKey(window, key) == GLFW_PRESS;
    }
    
    public void update() {
        // 可以在这里处理连续按键的逻辑
    }
    
    public void cleanup() {
        keyStates.clear();
    }
}