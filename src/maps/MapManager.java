package maps;

import java.awt.Image;
import java.io.File;
import java.util.Scanner;
import core.GameEngine;

public class MapManager {
    // 静态常量
    public final int GRID_COUNT = 16;
    public final int TILE_SIZE = 40;

    // 数据与资源
    protected int[][] mapData;
    protected int[][] undergroundMap;

    private final Image grassImg;
    private final Image stoneImg;  //石头障碍物 == 1
    private final Image flower_6_Img;
    private final Image flower_7_Img;
    private final Image flower_8_Img;
    private final Image grass_9_Img;
    private final Image vine; //藤蔓，不能走，但可以消除： 5

    public MapManager(GameEngine engine) {
        mapData = new int[GRID_COUNT][GRID_COUNT];
        undergroundMap = new int[40][40];
        // 在这里加载资源
        //0:草地 1-5:障碍物，5是藤蔓 6-9：装饰物，可通行
        grassImg = engine.loadImage("resource/sprites/maps/grassTile.png");//0
        stoneImg = engine.loadImage("resource/sprites/maps/stone.png"); //1
        flower_6_Img = engine.loadImage("resource/sprites/maps/flower_bg6.png"); //6
        flower_7_Img = engine.loadImage("resource/sprites/maps/flower_bg7.png"); //7
        flower_8_Img = engine.loadImage("resource/sprites/maps/flower_bg8.png"); //8
        grass_9_Img = engine.loadImage("resource/sprites/maps/grass_bg1.png"); //9
        vine = engine.loadImage("resource/sprites/maps/vine.png");//5
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
        } catch (Exception e) {
            System.err.println("Map Load Error: " + e.getMessage());
        }
    }

    //封装渲染逻辑
    public void draw(GameEngine engine) {

        for (int r = 0; r < GRID_COUNT; r++) {
            for (int c = 0; c < GRID_COUNT; c++) {
                int px = c * TILE_SIZE;
                int py = r * TILE_SIZE;

                // 绘制底色
                engine.drawImage(grassImg, px, py, TILE_SIZE, TILE_SIZE);

                // 绘制层级内容
                int type = mapData[r][c];
                //障碍物
                if (type == 1) engine.drawImage(stoneImg, px, py, TILE_SIZE, TILE_SIZE);
                else if(type == 5) engine.drawImage(vine, px, py, TILE_SIZE, TILE_SIZE);

                //非障碍物
                else if (type == 6) engine.drawImage(flower_6_Img, px, py, TILE_SIZE, TILE_SIZE);
                else if (type == 7) engine.drawImage(flower_7_Img, px, py, TILE_SIZE, TILE_SIZE);
                else if (type == 8) engine.drawImage(flower_8_Img, px, py, TILE_SIZE, TILE_SIZE);
                else if (type == 9) engine.drawImage(grass_9_Img, px, py, TILE_SIZE, TILE_SIZE);

            }
        }
    }

    public int[][] getMapData() {
        return mapData;
    }

}