import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class GenerateTextures {
    
    public static void generateBlockTextureAtlas(String outputPath) throws IOException {
        int textureSize = 256;
        int blockSize = 16;
        int blocksPerRow = textureSize / blockSize;
        
        BufferedImage texture = new BufferedImage(textureSize, textureSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = texture.createGraphics();
        
        // 设置抗锯齿
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // 清空背景
        g.setColor(new Color(0, 0, 0, 0));
        g.fillRect(0, 0, textureSize, textureSize);
        
        // 生成不同方块的纹理
        int blockIndex = 0;
        
        // 草方块
        drawBlock(g, blockIndex++, blocksPerRow, blockSize, 
                 new Color(0, 128, 0),   // 顶部（绿色）
                 new Color(139, 69, 19),  // 侧面（棕色）
                 new Color(101, 67, 33)); // 底部（深棕色）
        
        // 泥土
        drawBlock(g, blockIndex++, blocksPerRow, blockSize,
                 new Color(139, 69, 19),  // 顶部
                 new Color(139, 69, 19),  // 侧面
                 new Color(139, 69, 19)); // 底部
        
        // 石头
        drawBlock(g, blockIndex++, blocksPerRow, blockSize,
                 new Color(128, 128, 128), // 顶部
                 new Color(128, 128, 128), // 侧面
                 new Color(128, 128, 128)); // 底部
        
        // 木头
        drawBlock(g, blockIndex++, blocksPerRow, blockSize,
                 new Color(160, 120, 80),  // 顶部（年轮）
                 new Color(139, 90, 43),   // 侧面（树皮）
                 new Color(139, 90, 43));  // 底部
        
        // 树叶
        drawBlock(g, blockIndex++, blocksPerRow, blockSize,
                 new Color(0, 100, 0),     // 顶部（深绿）
                 new Color(0, 120, 0),     // 侧面（绿）
                 new Color(0, 100, 0));    // 底部
        
        // 水
        drawBlock(g, blockIndex++, blocksPerRow, blockSize,
                 new Color(64, 164, 223, 200),   // 半透明蓝色
                 new Color(64, 164, 223, 200),
                 new Color(64, 164, 223, 200));
        
        // 基岩
        drawBlock(g, blockIndex++, blocksPerRow, blockSize,
                 new Color(64, 64, 64),    // 顶部（深灰）
                 new Color(64, 64, 64),    // 侧面
                 new Color(64, 64, 64));   // 底部
        
        g.dispose();
        
        // 保存图像
        File outputFile = new File(outputPath);
        ImageIO.write(texture, "PNG", outputFile);
        System.out.println("纹理图集已生成: " + outputPath);
    }
    
    private static void drawBlock(Graphics2D g, int index, int blocksPerRow, int blockSize,
                                 Color topColor, Color sideColor, Color bottomColor) {
        int x = (index % blocksPerRow) * blockSize;
        int y = (index / blocksPerRow) * blockSize;
        
        // 绘制顶部（完整方块）
        g.setColor(topColor);
        g.fillRect(x, y, blockSize, blockSize);
        
        // 添加一些纹理细节
        g.setColor(topColor.brighter());
        g.fillRect(x, y, blockSize / 4, blockSize / 4);
        g.fillRect(x + blockSize * 3/4, y + blockSize * 3/4, blockSize / 4, blockSize / 4);
        
        g.setColor(topColor.darker());
        g.fillRect(x + blockSize * 3/4, y, blockSize / 4, blockSize / 4);
        g.fillRect(x, y + blockSize * 3/4, blockSize / 4, blockSize / 4);
        
        // 绘制边框
        g.setColor(Color.BLACK);
        g.drawRect(x, y, blockSize - 1, blockSize - 1);
    }
    
    public static void generateSteveSkin(String outputPath) throws IOException {
        int width = 64;
        int height = 64;
        
        BufferedImage skin = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = skin.createGraphics();
        
        // 设置抗锯齿
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // 清空背景
        g.setColor(new Color(0, 0, 0, 0));
        g.fillRect(0, 0, width, height);
        
        // 皮肤基础颜色（浅棕色）
        Color skinColor = new Color(198, 133, 95);
        Color hairColor = new Color(45, 45, 45);
        Color shirtColor = new Color(0, 127, 0);
        Color pantsColor = new Color(0, 0, 139);
        
        // 头部（8x8）
        drawSkinPart(g, 8, 8, 8, 8, skinColor);
        drawSkinPart(g, 8, 8, 8, 8, hairColor); // 头发/帽子层
        
        // 身体（8x12）
        drawSkinPart(g, 20, 20, 8, 12, shirtColor);
        
        // 右臂（4x12）
        drawSkinPart(g, 4, 20, 4, 12, skinColor);
        
        // 左臂（4x12）
        drawSkinPart(g, 56, 20, 4, 12, skinColor);
        
        // 右腿（4x12）
        drawSkinPart(g, 4, 36, 4, 12, pantsColor);
        
        // 左腿（4x12）
        drawSkinPart(g, 56, 36, 4, 12, pantsColor);
        
        // 添加细节
        g.setColor(Color.BLACK);
        g.drawRect(8, 8, 8, 8);   // 头部边框
        g.drawRect(20, 20, 8, 12); // 身体边框
        g.drawRect(4, 20, 4, 12);  // 右臂边框
        g.drawRect(56, 20, 4, 12); // 左臂边框
        g.drawRect(4, 36, 4, 12);  // 右腿边框
        g.drawRect(56, 36, 4, 12); // 左腿边框
        
        g.dispose();
        
        // 保存图像
        File outputFile = new File(outputPath);
        ImageIO.write(skin, "PNG", outputFile);
        System.out.println("史蒂夫皮肤已生成: " + outputPath);
    }
    
    private static void drawSkinPart(Graphics2D g, int x, int y, int w, int h, Color color) {
        g.setColor(color);
        g.fillRect(x, y, w, h);
        
        // 添加一些阴影效果
        g.setColor(color.brighter());
        g.fillRect(x, y, w/2, h/2);
        
        g.setColor(color.darker());
        g.fillRect(x + w/2, y + h/2, w/2, h/2);
    }
    
    public static void main(String[] args) {
        try {
            // 创建资源目录
            new File("assets/textures").mkdirs();
            new File("assets/skins").mkdirs();
            
            // 生成纹理图集
            generateBlockTextureAtlas("assets/textures/blocks.png");
            
            // 生成史蒂夫皮肤
            generateSteveSkin("assets/skins/steve.png");
            
            System.out.println("所有纹理生成完成！");
            
        } catch (IOException e) {
            System.err.println("生成纹理失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}