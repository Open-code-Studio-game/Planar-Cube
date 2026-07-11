# Planar Cube 项目长期记忆

## 项目基本信息
- **项目名称**: Planar Cube
- **当前设计**: 垂直切片 2D 平台游戏（2026-07-11 从"立方体面翻转"重构而来）
- **包名**: org.opencodestudiogame.planarcube
- **技术栈**: Java 17 + LWJGL 3 + Maven + JOML

## 核心设计理念（新）
1. **垂直切片机制**: 3D体素世界(100×50×100)沿Z轴切为2D竖直截面，每片一个2D平台关卡
2. **操作**: W/S切换Z层, A/D水平移动, 空格跳跃(带重力), ESC退出
3. **正交2D侧视**: 摄像机看向Z轴正方向，看到X(水平)-Y(垂直)平面
4. **碰撞检测**: 玩家不能走入固体方块(AIR和WATER除外)，有重力下落
5. **颜色渲染**: 每个BlockType有2D颜色属性(草#5B8A2A/土#8B5E3C/石#808080等)
6. **双模式**: 开放模式(Perlin噪声随机地形)和默认模式(平坦地形)
7. **物理参数**: 移动速度8方块/秒, 重力15方块/秒², 跳跃力度12, 跳跃高度≈3方块

## 项目约定
1. **代码结构**: 严格遵循Maven标准目录结构
2. **包组织**: engine/: 游戏引擎 / entity/: 实体 / world/: 世界地形
3. **配置**: GameConfig管理所有参数(tileSize=32, viewWidth=40, viewHeight=22.5)

## 关键技术决策
1. **LWJGL 3 + JOML**: 底层OpenGL渲染 + 矩阵运算
2. **逐层VAO缓存**: Terrain为每个Z层维护独立VAO/VBO，脏标记(dirty)触发重建
3. **2D正交投影**: Renderer使用ortho投影，摄像机跟随玩家居中
4. **Perlin噪声地形**: open模式使用gradientCoherentNoise3D生成地形高度图
5. **BlockType颜色**: 枚举自带getColor()返回2D渲染颜色数组

## 已修改文件清单（2026-07-11重构）
- Player.java: 移除currentSpace, 改为currentLayer + x/y + 物理
- World.java: 新增renderLayer/isSolid/getGroundHeight
- Terrain.java: 3D mesh → 逐层2D VAO/VBO
- Renderer.java: 透视投影 → 2D正交投影
- GameEngine.java: 2D摄像机跟随 + 连续输入 + 层渲染
- BlockType.java: 移除FaceDirection, 添加精确颜色
- GameConfig.java: 新增2D切片参数
- Skin.java: render改为2D签名
- TextureAtlas.java: 添加BlockType纹理映射
- vertex/fragment.glsl: 3D → 2D着色器

## 扩展计划
1. 完善玩家2D精灵动画（行走/跳跃帧）
2. 层切换过渡动画（淡入淡出）
3. 声音系统
4. 敌人AI
5. 物品/道具系统
