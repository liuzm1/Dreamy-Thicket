package maps;

import java.awt.Image;
import java.io.File;
import java.util.Scanner;
import core.GameEngine;

public class MapManager {
    // 静态常量
    private final int GRID_COUNT = 16;
    private final int TILE_SIZE = 40;

    // 数据与资源
    private final int[][] mapData;
    private final Image grassImg;
    private final Image stoneImg;
    private final Image flowerImg;
    private final Image mushroomImg;

    public MapManager(GameEngine engine) {
        mapData = new int[GRID_COUNT][GRID_COUNT];
        // 在这里加载资源
        grassImg = engine.loadImage("resource/sprites/maps/Grass1.png");
        stoneImg = engine.loadImage("resource/sprites/maps/Stone.png");
        flowerImg = engine.loadImage("resource/sprites/maps/flowers_bg.png");
        mushroomImg = engine.loadImage("resource/sprites/maps/mushroon_bg.png");
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

    // 为后续碰撞检测预留的“窗口”
    public boolean isSolid(int col, int row) {
        if (col < 0 || col >= GRID_COUNT || row < 0 || row >= GRID_COUNT) return true;
        return mapData[row][col] == 1; // 如果是石头，返回 true
    }
}