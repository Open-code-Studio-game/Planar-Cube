# Planar Cube 项目总结

## 项目概述

Planar Cube 是一个基于Java开发的2D平台游戏，具有RPG和开放探索元素。游戏的核心特色是玩家可以在立方体的不同面（称为"空间"）之间移动，同时每个面都采用2D平台游戏的形式。

## 核心技术栈

- **编程语言**: Java 17
- **图形库**: LWJGL 3 (OpenGL绑定)
- **构建工具**: Maven
- **依赖管理**: 
  - LWJGL 3.3.3 (图形、窗口、输入)
  - Gson 2.10.1 (JSON配置处理)
  - flowpowered/noise 1.0.0 (地形噪声生成)

## 项目结构

```
org.opencodestudiogame.planarcube/
├── Main.java                    # 游戏主入口
├── Config.java                  # 配置管理
├── GameStateEnum.java           # 游戏状态枚举
├── GameStateManager.java        # 游戏状态管理
├── Demo.java                    # 功能演示
├── engine/                      # 游戏引擎
│   ├── GameEngine.java          # 游戏引擎主类
│   ├── GameConfig.java          # 游戏窗口配置
│   ├── InputHandler.java        # 输入处理
│   ├── Renderer.java            # 渲染器
│   └── TextureAtlas.java        # 纹理图集管理
├── entity/                      # 游戏实体
│   ├── Player.java              # 玩家实体（核心）
│   └── Skin.java                # 皮肤系统（支持MC皮肤）
├── world/                       # 游戏世界
│   ├── World.java               # 世界生成和管理
│   ├── Terrain.java             # 地形管理（方块网格）
│   └── BlockType.java           # 方块类型定义
└── util/                        # 工具类
    └── TextureGenerator.java    # 纹理生成工具
```

## 核心特性实现

### 1. 空间移动机制

**实现文件**: `Player.java`

- **W/S键**: 在不同空间（立方体面）之间移动
- **A/D键**: 在当前空间内前后移动
- **视角固定**: 玩家始终位于屏幕中心，不允许视角移动
- **空间切换**: 通过`moveBetweenSpaces()`方法实现
- **平面移动**: 通过`moveInSpace()`方法实现，根据当前空间自动调整移动方向

### 2. MC皮肤支持

**实现文件**: `Skin.java`

- 支持加载标准Minecraft皮肤文件（PNG格式）
- 内置默认史蒂夫皮肤生成
- OpenGL纹理管理
- 皮肤动画支持（行走、跳跃）

### 3. 地形生成系统

**实现文件**: `World.java`, `Terrain.java`

- **开放模式**: 使用Perlin噪声生成随机地形
- **地形特征**: 
  - 多层地形（基岩、石头、泥土、草）
  - 树木生成
  - 洞穴系统
  - 水体生成
- **方块类型**: 7种基础方块类型
- **网格渲染**: 基于OpenGL的高效渲染

### 4. 游戏模式

- **开放模式**: 程序化生成的无限世界
- **探索模式**: 预设的冒险地图（待实现）

## 关键技术实现

### 渲染系统
- 基于OpenGL 3.3的现代渲染管线
- 顶点着色器 (`vertex.glsl`) 和片段着色器 (`fragment.glsl`)
- 纹理图集管理
- 光照计算（环境光、漫反射、镜面反射）

### 输入系统
- GLFW键盘输入处理
- 自定义按键绑定
- 游戏状态敏感的输入处理

### 配置系统
- JSON格式配置文件
- 图形、游戏、声音、控制设置
- 自动保存/加载配置

### 资源管理
- 纹理图集自动生成
- 皮肤文件加载
- 着色器程序管理

## 使用说明

### 编译和运行

```bash
# 方法1: 使用Maven
mvn clean compile
mvn exec:java -Dexec.mainClass="org.opencodestudiogame.planarcube.Main"

# 方法2: 直接运行JAR
mvn clean package
java -jar target/planar-cube-1.0.0.jar

# 方法3: 使用脚本
./run.sh      # Linux/macOS
run.bat       # Windows
```

### 游戏控制

| 按键 | 功能 | 说明 |
|------|------|------|
| W | 向上一个空间移动 | 在立方体的不同面之间切换 |
| S | 向下一个空间移动 | 在立方体的不同面之间切换 |
| A | 向左移动 | 在当前空间内移动 |
| D | 向右移动 | 在当前空间内移动 |
| E | 切换空间 | 备用空间切换键 |
| 空格 | 跳跃 | 待实现 |
| ESC | 退出游戏 | 关闭游戏窗口 |

### 配置文件

游戏会自动创建 `config.json` 文件，包含以下设置：

```json
{
  "windowWidth": 1280,
  "windowHeight": 720,
  "fullscreen": false,
  "vsync": true,
  "targetFps": 60,
  "mouseSensitivity": 1.0,
  "movementSpeed": 5.0,
  "jumpStrength": 8.0,
  "gravity": 20.0,
  "masterVolume": 1.0,
  "musicVolume": 0.8,
  "soundVolume": 1.0,
  "keyForward": 87,    // W
  "keyBackward": 83,   // S
  "keyLeft": 65,       // A
  "keyRight": 68,      // D
  "keyJump": 32,       // Space
  "keyToggleSpace": 69, // E
  "skinPath": "assets/skins/steve.png",
  "lastGameMode": "open"
}
```

## 扩展功能

### 已实现功能
1. ✅ 基础游戏引擎框架
2. ✅ 3D渲染系统
3. ✅ 输入处理系统
4. ✅ 玩家实体和移动系统
5. ✅ 空间切换机制（W/S键）
6. ✅ 皮肤系统（支持MC皮肤）
7. ✅ 地形生成系统（开放模式）
8. ✅ 配置管理系统
9. ✅ 纹理生成工具
10. ✅ 游戏状态管理

### 待实现功能
1. 🔄 探索模式地图
2. 🔄 物理系统（碰撞检测、重力）
3. 🔄 敌人AI和战斗系统
4. 🔄 物品和装备系统
5. 🔄 任务系统
6. 🔄 保存/加载系统
7. 🔄 声音系统
8. 🔄 多人游戏支持

## 设计模式应用

1. **单例模式**: `Config` 类
2. **状态模式**: `GameStateManager` 管理游戏状态
3. **工厂模式**: `BlockType` 枚举管理方块类型
4. **观察者模式**: 输入事件处理
5. **策略模式**: 不同游戏模式的地形生成策略

## 性能优化

1. **网格批处理**: 地形渲染使用VAO/VBO进行批处理
2. **视锥剔除**: 只渲染可见方块
3. **纹理图集**: 所有纹理合并为单个图集
4. **LOD系统**: 根据距离使用不同细节等级（待实现）
5. **多线程加载**: 背景线程加载资源（待实现）

## 已知问题和限制

1. **内存使用**: 大规模地形可能消耗较多内存
2. **性能**: 复杂场景可能影响帧率
3. **功能完整性**: 部分RPG功能尚未实现
4. **内容**: 需要更多游戏内容和玩法

## 未来改进方向

1. **内容扩展**: 添加更多方块类型、生物、物品
2. **性能优化**: 实现LOD、遮挡剔除等优化
3. **网络支持**: 添加多人游戏功能
4. **模组支持**: 创建模组API
5. **跨平台**: 支持更多操作系统

## 贡献指南

欢迎提交Issue和Pull Request来改进项目。主要开发方向包括：

1. 添加新的游戏功能
2. 优化渲染性能
3. 改进用户界面
4. 修复Bug
5. 添加测试用例

## 许可证

MIT许可证 - 查看 [LICENSE](LICENSE) 文件了解详情