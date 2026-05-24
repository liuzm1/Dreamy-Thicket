package maps;

import java.awt.Image;
import java.io.File;
import java.util.Scanner;
import items.ItemManager;
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
    //private final Image TEST;
    // 在你的图片变量最后添加
    private final Image fire;
    private final Image heart_add;
    private final Image heart_poision;

    // 添加物品管理器
    private final ItemManager itemManager;

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

        itemManager = new ItemManager(this); // 初始化
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
            itemManager.generateAllItems(); // 生成物品
        } catch (Exception e) {
            System.err.println("Map Load Error: " + e.getMessage());
        }
    }

    //封装渲染逻辑
    public void draw(GameEngine engine) {
        engine.drawImage(grassTileImg, 0, 0, 640, 640);

        for (int r = 2; r < GRID_COUNT-2; r++) {
            for (int c = 2; c < GRID_COUNT-2; c++) {
                int px = c * TILE_SIZE;
                int py = r * TILE_SIZE;
                // 绘制层级内容
                int type = mapData[r][c];
                //障碍物
                if (type == 1) engine.drawImage(stoneImg, px+4, py+4, TILE_SIZE-8, TILE_SIZE-8);
                else if(type == 5) engine.drawImage(vine, px+2, py+2, 35, 35);
            }
        }
        // 物品绘制必须在循环【外面】！！！！
        itemManager.drawAll(engine, fire, heart_add, heart_poision, TILE_SIZE, ((GameInstance) engine).animFrame);

    }

    public void checkItemPickup(int col, int row, GameInstance game) {
        itemManager.checkPickup(col, row, game);
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