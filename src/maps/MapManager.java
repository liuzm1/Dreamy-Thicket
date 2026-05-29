/**
 * ---------------------------------------------------------------------------
 * Massey University - 159.261 Games Programming
 * Assignment 2
 * ---------------------------------------------------------------------------
 * * [Dreamy Forest]
 * * Team Members:
 * - LIU ZIMO (ID:24009362)
 * - MIAO CHONG (ID: 24008986)
 * - SUN MINGYI (ID: 24009239)
 * - ZHOU XUAN (ID: 24009035)
 * ---------------------------------------------------------------------------
 **/
package maps;

import java.awt.Image;
import java.io.File;
import java.util.Scanner;
import core.GameEngine;
import core.GameInstance;

public class MapManager {
    // Static constants
    public final int GRID_COUNT = 16;
    public final int TILE_SIZE = 40;

    // Data and assets
    protected int[][] mapData;
    protected int[][] undergroundMap;

    private final Image grassTileImg;
    private final Image stoneImg;  // Stone obstacle == 1
    private final Image vine; // Vine: blocks movement, can be cleared; tile 5
    private final Image ruby;
    private final Image star;
    private final Image oracle;
    //private final Image TEST;

    public MapManager(GameEngine engine) {
        mapData = new int[GRID_COUNT][GRID_COUNT];
        undergroundMap = new int[40][40];
        // Load assets here
        // 0: grass; 1-5: obstacles (5 = vine); 6-9: decorations, passable
        grassTileImg = engine.loadImage("resource/sprites/maps/map1.png");//0
        stoneImg = engine.loadImage("resource/sprites/maps/stone.png"); //1
        vine = engine.loadImage("resource/sprites/maps/vine.png");//5
        ruby = engine.loadImage("resource/sprites/Items/ruby.png");
        star = engine.loadImage("resource/sprites/Items/star.png");
        oracle = engine.loadImage("resource/sprites/Items/oracle.png");
    }

    private void generateItems() {
        int spawned = 0;
        int maxItems = 5; // Fixed count of items to spawn

        // Cap attempts to avoid infinite loop
        int attempts = 0;
        while (spawned < maxItems && attempts < 100) {
            attempts++;

            // Random row/col 2~13, avoiding outer border
            int r = 2 + (int)(Math.random() * 12);
            int c = 2 + (int)(Math.random() * 12);

            // Only on empty tiles (0); do not overwrite walls, vines, or existing items
            if (mapData[r][c] == 0) {
                // Random item: 6=ruby +5, 7=star +10, 8=oracle +/-5
                double rand = Math.random();
                if (rand < 0.94) {
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



    // Encapsulated load logic
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

    // Encapsulated render logic
    public void draw(GameEngine engine) {
        engine.drawImage(grassTileImg, 0, 0, 640, 640);

        for (int r = 2; r < GRID_COUNT-2; r++) {
            for (int c = 2; c < GRID_COUNT-2; c++) {
                int px = c * TILE_SIZE;
                int py = r * TILE_SIZE;
                // Draw tile layer content
                int type = mapData[r][c];

                // Per-item-type draw params
                float offset;
                double frame = ((GameInstance)engine).animFrame * 0.1f;


                // Obstacles
                if (type == 1) engine.drawImage(stoneImg, px+4, py+4, TILE_SIZE-8, TILE_SIZE-8);
                else if(type == 5) engine.drawImage(vine, px+2, py+2, 35, 35);
                // Items: score bonus
                else if (type == 6) {
                    offset = (float)Math.sin(frame) * 3f;
                    engine.drawImage(ruby, px + 4, py + 1 + offset, 39, 39);
                }
                else if (type == 7) {
                    offset = (float)Math.sin(frame + 1.2) * 2.5f;
                    engine.drawImage(star, px + 4, py + 1 + offset, 35, 35);
                }
                // Items: score penalty (shown at level start)
                else if (type == 8) {
                    offset = (float)Math.sin(frame + 2.4) * 2f;
                    engine.drawImage(oracle, px + 4, py + 1 + offset, 32, 32);
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