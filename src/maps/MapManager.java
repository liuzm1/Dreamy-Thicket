package maps;

import java.awt.Image;
import java.io.File;
import java.util.Scanner;
import core.GameEngine;
import core.GameInstance;

public class MapManager {
    // 静态常量
    public final int GRID_COUNT = 16;
    public final int TILE_SIZE = 40;

    // 数据与资源
    protected int[][] mapData;
    protected int[][] undergroundMap;

    private final Image grassTileImg;
    private final Image stoneImg;  //石头障碍物 == 1
    private final Image vine; //藤蔓，不能走，但可以消除： 5
    private final Image text;
    private final Image fire;
    private final Image heart_add;
    private final Image heart_poision;
    //private final Image TEST;

    public MapManager(GameEngine engine) {
        mapData = new int[GRID_COUNT][GRID_COUNT];
        undergroundMap = new int[40][40];
        // 在这里加载资源
        //0:草地 1-5:障碍物，5是藤蔓 6-9：装饰物，可通行
        grassTileImg = engine.loadImage("resource/sprites/maps/map1.png");//0
        stoneImg = engine.loadImage("resource/sprites/maps/stone.png"); //1
        vine = engine.loadImage("resource/sprites/maps/vine.png");//5
        text = engine.loadImage("resource/sprites/heart_poision.png");
        fire = engine.loadImage("resource/sprites/fire.png");
        heart_add = engine.loadImage("resource/sprites/heart_add.png");
        heart_poision = engine.loadImage("resource/sprites/heart_poision.png");
    }

    private void generateItems() {
        int spawned = 0;
        int maxItems = 10; // 固定生成10个道具

        // 防止死循环，最多循环100次
        int attempts = 0;
        while (spawned < maxItems && attempts < 100) {
            attempts++;

            // 随机生成在第2~13行、第2~13列，避开外圈边界
            int r = 2 + (int)(Math.random() * 12);
            int c = 2 + (int)(Math.random() * 12);

            // 只在空地生成（0），不覆盖墙、藤蔓和已有的道具
            if (mapData[r][c] == 0) {
                // 随机三种道具：6=火+10、7=心+5、8=毒-5
                double rand = Math.random();
                if (rand < 0.82) {
                    mapData[r][c] = 6;
                } else if (rand < 0.97) {
                    mapData[r][c] = 7;
                } else {
                    mapData[r][c] = 8;
                }
                spawned++;
            }
        }
    }


    // 封装读取逻辑
    public void loadLevel(String path) {
        try {
            Scanner sc = new Scanner(new File(path));
            for (int r = 0; r < GRID_COUNT; r++) {
                if (sc.hasNextLine()) {
                    String[] vals = sc.nextLine().split(",");
                    for (int c = 0; c < GRID_COUNT; c++) {
                        mapData[r][c] = Integer.parseInt(vals[c].trim());
                    }
                }
            }
            sc.close();
            generateItems();
        } catch (Exception e) {
            System.err.println("Map Load Error: " + e.getMessage());
        }
    }

    // 封装渲染逻辑
    public void draw(GameEngine engine) {
        engine.drawImage(grassTileImg, 0, 0, 640, 640);

        for (int r = 2; r < GRID_COUNT-2; r++) {
            for (int c = 2; c < GRID_COUNT-2; c++) {
                int px = c * TILE_SIZE;
                int py = r * TILE_SIZE;
                // 绘制层级内容
                int type = mapData[r][c];

                // 根据道具类型区分参数
                float offset;
                double frame = ((GameInstance)engine).animFrame * 0.1f;


                //障碍物
                if (type == 1) engine.drawImage(stoneImg, px+4, py+4, TILE_SIZE-8, TILE_SIZE-8);
                else if(type == 5) engine.drawImage(vine, px+2, py+2, 35, 35);
                else if(type == 5) engine.drawImage(vine, px-5, py-8, 43, 40);
                // 道具：加分道具
                else if (type == 6) {
                    offset = (float)Math.sin(frame) * 3f;
                    engine.drawImage(fire, px + 4, py + 4 + offset, 39, 39);
                }
                else if (type == 7) {
                    offset = (float)Math.sin(frame + 1.2) * 2.5f;
                    engine.drawImage(heart_add, px + 4, py + 4 + offset, 35, 35);
                }
                // 道具：减分道具（仅开局显示）
                else if (type == 8) {
                    offset = (float)Math.sin(frame + 2.4) * 2f;
                    engine.drawImage(heart_poision, px + 4, py + 4 + offset, 32, 32);
                }
            }
        }
    }



    public int[][] getMapData() {
        return mapData;
    }

    public int getTile(int x, int y) {
        if(x < 0 || y < 0 || x >= 16 || y >= 16) return -1;
        return mapData[y][x];
    }

    public void setTile(int x, int y, int value) {
        mapData[y][x] = value;
    }

}