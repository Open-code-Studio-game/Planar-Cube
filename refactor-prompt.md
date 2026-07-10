# Planar-Cube 重构提示词

## 项目背景

Planar-Cube 是一个 Java 17 + LWJGL 3 + OpenGL 3.3 的 3D 体素游戏项目。使用 Maven 构建。

**当前状态**：项目源码骨架完整，但实际渲染管线还没跑通（shader 未加载、缺少 MVP 矩阵、皮肤 3D 绘制为空壳）。物理/碰撞检测未实现。

## 需求：从"立方体面翻转"改为"垂直切片 2D 平台游戏"

### 原设计（需要去掉）

当前代码的 `Player.java` 有一个 `currentSpace`（0-5 对应立方体的 6 个面），W/S 在面之间切换，位置在 `x=-5/+5`、`y=-5/+5`、`z=-5/+5` 之间瞬移。这被废弃。

### 新设计

把整个 100×50×100 的体素世界沿 Z 轴切成一系列 **2D 竖直截面（切片）**，每一片是一个 **2D 平台关卡画面**。玩家在层之间穿行。

```
平面视角（看向 Y-up, X-right）:

   Z=0        Z=1        Z=2        Z=3        ...
┌──────┐  ┌──────┐  ┌──────┐  ┌──────┐
│ 草🌲  │  │ 草🌲  │  │ 🌲草  │  │ 草土  │
│ 土土  │  │ 土土  │  │ 土土  │  │ 土土  │
│ 石石  │  │ 石石  │  │ 石石  │  │ 石石  │  ← 每一片都是 2D 侧视图 (x-y 平面)
│ 💧💧  │  │ 💧💧  │  │ 石石  │  │ 💧💧  │
└──────┘  └──────┘  └──────┘  └──────┘
   ↑           ↑           ↑
 当前层     W=下一层    W=下下層    ...
```

### 按键映射

| 按键 | 功能 |
|------|------|
| **W** | 向前移动到下一层（Z+1），渲染那一层的 2D 截面 |
| **S** | 向后移动到上一层（Z-1） |
| **A** | 在当前层内向左移动（X-1） |
| **D** | 在当前层内向右移动（X+1） |
| **空格** | 跳跃（Y+，带重力下落） |
| **ESC** | 退出 |

### 渲染方式

- **相机固定为正交 2D 侧视角**：看向 Z 轴正方向，视线平行于 Z 轴，看到 X（水平）-Y（垂直）平面
- **每次只渲染玩家当前所在 Z 层的 2D 截面**：遍历该层的所有 `(x, y)` 方块，非 AIR 的方块渲染为 2D 方块精灵
- 方块大小固定（如 16×16 像素或 32×32 像素）
- 用纹理图集（TextureAtlas）给不同 BlockType 渲染不同颜色/纹理
- **不渲染Z轴深度**——2D 切片不显示其他层的内容（最多在切换层时做一个淡入淡出过渡）

### 玩家逻辑

- 玩家位置：`(x, y, currentLayer)`，其中：
  - `x`：水平位置（方块单位）
  - `y`：垂直位置（方块单位，受重力影响）
  - `currentLayer`：当前所在的 Z 层索引（0~worldDepth-1）
- **碰撞检测**：玩家不能走进固体方块（非 AIR 方块）
- **重力**：玩家如果没有站在方块上，会自然下落
- **边界**：x 和 y 不能超出世界范围，currentLayer 不能超出 0~worldDepth-1

### 需要修改的文件

#### 1. `src/main/java/org/opencodestudiogame/planarcube/entity/Player.java`

- 去掉 `currentSpace`、6 个面的相关逻辑
- 改为 `currentLayer`（int，Z 坐标），`x` 和 `y`（float）
- W 增加 `currentLayer`，S 减少 `currentLayer`
- A/D 在 X 方向左右移动
- 增加 `velocityY`（垂直速度）和 `isOnGround`（是否在地面）用于跳跃和重力
- `update()` 中实现重力逻辑：`velocityY -= gravity * deltaTime`，检测碰撞后落地
- `moveBetweenSpaces(direction)` 改为 `moveToLayer(direction)`，direction=+1 向前，-1 向后
- `moveInSpace(dx, dy)` 改为 `moveHorizontal(dx)`，只在 X 方向移动

#### 2. `src/main/java/org/opencodestudiogame/planarcube/world/World.java`

- 保持 3D 体素数据结构不变（100×50×100）
- 新增方法 `renderLayer(Renderer renderer, int layerZ)`：只渲染指定 Z 层的 2D 截面
- 新增方法 `isSolid(int x, int y, int z)`：检查某个方块是否固体（碰撞用）
- 新增方法 `getGroundHeight(int x, int z)`：获取某列的地面高度（玩家生成时用）

#### 3. `src/main/java/org/opencodestudiogame/planarcube/world/Terrain.java`

- 保留 3D 方块数据存储和 `setBlock`/`getBlock`
- **去掉原来的 3D mesh 生成逻辑**（`generateMesh()`、VAO/VBO/EBO、`FaceDirection` 枚举、`addFaceVertices()`、`uploadMeshData()`）
- 新增 `renderLayer(Renderer renderer, int layerZ)`：遍历 `(x, y)`，对每个非 AIR 方块绘制一个 **2D 矩形**
- 每个方块用一个简单的 2D 四边形（两个三角形），加上对应 BlockType 的颜色或纹理坐标
- 可以生成一个 **动态的 2D VAO/VBO**，每层切换时重建

#### 4. `src/main/java/org/opencodestudiogame/planarcube/engine/Renderer.java`

- 改为**正交投影**代替透视投影
- 添加 `setOrthographicProjection(float left, float right, float bottom, float top, float near, float far)` 方法
- 设置相机的 Model-View 矩阵为固定 2D 视角

#### 5. `src/main/java/org/opencodestudiogame/planarcube/engine/GameEngine.java`

- 初始化时设置正交投影
- 游戏循环中：更新玩家物理（重力、跳跃、碰撞检测）→ 渲染当前层的 2D 截面 → 渲染玩家
- 输入处理：W/S 改调用 `player.moveToLayer()`、A/D 改调用 `player.moveHorizontal()`、空格触发 `player.jump()`

#### 6. `src/main/java/org/opencodestudiogame/planarcube/engine/GameConfig.java`

- 新增参数：`tileSize`（方块像素大小，默认 32）、`viewWidth`（视口可见方块数，默认 40）、`viewHeight`（默认 22.5）

#### 7. `src/main/java/org/opencodestudiogame/planarcube/entity/Skin.java`

- 保持 MC 皮肤加载能力
- `render()` 改为 **2D 精灵渲染**：在 2D 正交投影下，在玩家 `(x, y)` 位置绘制皮肤的一个 2D 站立/行走帧
- 行走动画简化为左右腿交替帧

#### 8. `src/main/java/org/opencodestudiogame/planarcube/world/BlockType.java`

- 保持当前的方块类型枚举
- 为每种方块增加 2D 渲染颜色属性（如 GRASS→绿色，DIRT→棕色，STONE→灰色，WATER→蓝色等）

#### 9. `src/main/java/org/opencodestudiogame/planarcube/engine/TextureAtlas.java`

- 改为管理 **2D 方块纹理**，每个 BlockType 对应一个纹理区域

#### 10. `src/main/resources/shaders/vertex.glsl` 和 `fragment.glsl`

- 改为简单的 2D 着色器
- Vertex：接受位置 + 纹理坐标，乘以正交 MVP 矩阵
- Fragment：采样纹理或使用 uniform 颜色

### 图形细节

- 窗口大小：1280×720
- 正交投影范围：left=0, right=worldWidth (100), bottom=-5, top=worldHeight (55)
- 或者根据 `tileSize` 计算：left=0, right=1280/tileSize (40), bottom=0, top=720/tileSize (22.5)
- 方块颜色区分：草→#5B8A2A、土→#8B5E3C、石→#808080、木头→#6B4226、树叶→#3A7D32、水→#4A90D9、基岩→#333333
- 玩家用一个简单的人形 2D 精灵表示（可以先用一个矩形占位）

### 边缘情况

- 切换层时如果目标层在玩家当前位置有固体方块，玩家应被"挤"到最近的空位
- 初次生成玩家：放在 Z=0 层，X 在中间，Y 在地面高度
- 跳跃高度 ≈ 3 个方块，重力加速度 ≈ 15 方块/秒²
- 移动速度 ≈ 8 方块/秒
- 层切换有平滑感（非瞬移，但过渡可以很简短）
