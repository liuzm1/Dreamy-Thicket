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

    private final Image grassImg;
    private final Image stoneImg;  //石头障碍物 == 1
    private final Image flowerImg;
    private final Image mushroomImg;
    private final Image vine; //藤蔓，不能走，但可以消除： 5

    public MapManager(GameEngine engine) {
        mapData = new int[GRID_COUNT][GRID_COUNT];
        // 在这里加载资源
        grassImg = engine.loadImage("resource/sprites/maps/grass1.png");
        stoneImg = engine.loadImage("resource/sprites/maps/stone.png");
        flowerImg = engine.loadImage("resource/sprites/maps/flowers_bg.png");
        mushroomImg = engine.loadImage("resource/sprites/maps/mushroon_bg.png");
        vine = engine.loadImage("resource/sprites/maps/vine.png");
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
                if (type == 1) engine.drawImage(stoneImg, px, py, TILE_SIZE, TILE_SIZE);
                else if (type == 2) engine.drawImage(flowerImg, px, py, TILE_SIZE, TILE_SIZE);
                else if (type == 3) engine.drawImage(mushroomImg, px, py, TILE_SIZE, TILE_SIZE);
            }
        }
    }

    public int[][] getMapData() {
        return mapData;
    }

}