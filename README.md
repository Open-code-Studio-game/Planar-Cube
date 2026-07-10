# Planar-Cube

平面立方 - 一款开源的"开放探索+RPG角色扮演游戏"
Planar Cube - an open-source "open exploration + RPG" game.

## 游戏特色

1. **独特的空间移动机制**
   - 使用 W/S 键在不同空间（立方体面）之间移动
   - 使用 A/D 键在当前空间内前后移动
   - 视角固定，玩家始终位于屏幕中心

2. **支持MC皮肤文件**
   - 可以加载Minecraft皮肤文件
   - 支持默认史蒂夫皮肤
   - 自定义皮肤支持

3. **两种游戏模式**
   - **开放模式**：使用Perlin噪声生成随机地形
   - **探索模式**：预设的冒险地图（待实现）

4. **技术特性**
   - 使用Java + LWJGL 3开发
   - 3D图形渲染
   - 程序化地形生成
   - 完整的光照和纹理系统

## 系统要求

- Java 17或更高版本
- OpenGL 3.3兼容显卡
- 至少2GB可用内存
- 1GB可用存储空间

## 构建和运行

### 使用Maven构建

```bash
# 克隆项目
git clone <repository-url>
cd Planar-Cube

# 使用Maven编译
mvn clean compile

# 运行游戏
mvn exec:java -Dexec.mainClass="org.opencodestudiogame.planarcube.Main"
```

### 直接运行

```bash
# 编译项目
mvn clean package

# 运行JAR文件
java -jar target/planar-cube-1.0.0.jar
```

## 游戏控制

| 按键 | 功能 |
|------|------|
| W | 向上一个空间移动 |
| S | 向下一个空间移动 |
| A | 向左移动 |
| D | 向右移动 |
| E | 切换空间（备用） |
| 空格 | 跳跃（待实现） |
| ESC | 退出游戏 |

## 项目结构

```
src/main/java/org/opencodestudiogame/planarcube/
├── Main.java                    # 游戏主入口
├── Config.java                  # 配置管理
├── GameStateEnum.java           # 游戏状态枚举
├── GameStateManager.java        # 游戏状态管理
├── engine/
│   ├── GameEngine.java          # 游戏引擎主类
│   ├── GameConfig.java          # 游戏配置
│   ├── InputHandler.java        # 输入处理
│   ├── Renderer.java            # 渲染器
│   └── TextureAtlas.java        # 纹理图集
├── entity/
│   ├── Player.java              # 玩家实体
│   └── Skin.java                # 皮肤系统
└── world/
    ├── World.java               # 世界生成和管理
    ├── Terrain.java             # 地形管理
    └── BlockType.java           # 方块类型
```

## 配置说明

游戏配置文件位于 `config.json`，包含以下设置：

- **图形设置**：窗口大小、全屏、V-Sync、FPS限制
- **游戏设置**：鼠标灵敏度、移动速度、跳跃强度、重力
- **声音设置**：主音量、音乐音量、音效音量
- **控制设置**：按键绑定
- **其他设置**：皮肤路径、最后游戏模式

## 开发计划

### 已实现功能
- [x] 基础游戏引擎框架
- [x] 3D渲染系统
- [x] 输入处理系统
- [x] 玩家实体和移动系统
- [x] 空间切换机制（W/S键）
- [x] 皮肤系统（支持MC皮肤）
- [x] 地形生成系统（开放模式）
- [x] 配置管理系统

### 待实现功能
- [ ] 探索模式地图
- [ ] 物理系统（碰撞检测、重力）
- [ ] 敌人AI和战斗系统
- [ ] 物品和装备系统
- [ ] 任务系统
- [ ] 保存/加载系统
- [ ] 声音系统
- [ ] 多人游戏支持

## 贡献指南

1. Fork本仓库
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开Pull Request

## 许可证

本项目采用MIT许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 联系方式

- 项目主页：<repository-url>
- 问题反馈：<issues-page>
- 讨论区：<discussions-page>
