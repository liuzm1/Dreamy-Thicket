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

import java.awt.*;

public class CollisionCheck {
    MapManager mapManager;
    public CollisionCheck(MapManager mapManager) {
        this.mapManager = mapManager;
    }

    /*
      Check if a cell is solid; true = blocked, false = walkable.
     */
    public boolean isSolid(int col, int row){
        // Bounds check
        if(col < 0 || col >= mapManager.GRID_COUNT || row < 0 || row >= mapManager.GRID_COUNT)
            return true;

        // Read map tile
        int tileType = mapManager.getMapData()[row][col];

        int tile = mapManager.getTile(col, row);
        boolean isOuter8 = (tile ==8) && (row==0||row==15||col==0||col==15); // Outer border tile 8
        // Tile 1 = stone, 5 = vine (also blocks movement)
        return tile ==1 || tile ==5 || isOuter8;

    }

    /*
     * Vine-only check (for vine clear/grow logic).
     * true if cell is vine, false otherwise.
     */
    public boolean isVine(int col, int row){
        // Bounds check, then test for tile 5
        if(col < 0 || col >= mapManager.GRID_COUNT || row < 0 || row >= mapManager.GRID_COUNT)
            return false;

        return mapManager.getMapData()[row][col] == 5;
    }

}
