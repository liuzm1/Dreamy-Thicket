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
package entities;
import core.GameEngine;
import maps.MapManager;

import java.awt.*;
import java.awt.Image;

public class SoloPlayer extends Player {

    public SoloPlayer(GameEngine engine, MapManager mapManager, int startCol, int startRow) {
        super(engine, mapManager, startCol, startRow);
        isClearing = false;
        isCasting = false;

        spriteSheet = engine.loadImage("resource/sprites/entities/P1.png");
    }


    //    **
//            * Vine logic not fully designed yet; must implement because parent is abstract.
//     * Stub for compilation.
//     */
    @Override
    public void useSkill(maps.MapManager map) {
        // Cannot cast again while moving or already casting
        if (isCasting || isClearing || isMoving) return;
        // 1. Target cell in front of player
        currentCastCol = this.col;
        currentCastRow = this.row;
        if (direction == DIR_UP) currentCastRow--;
        else if (direction == DIR_DOWN) currentCastRow++;
        else if (direction == DIR_LEFT) currentCastCol--;
        else if (direction == DIR_RIGHT) currentCastCol++;

        // 2. Grow or clear?
        int tile = mapManager.getTile(currentCastCol, currentCastRow);

        if (tile == 5) {
            // Vine ahead: enter clear mode
            isClearing = true;
            remainingVineGrids = 11;
            castTimer = 0;
        } else if (canPlantVineAt(currentCastCol, currentCastRow)) {
            isCasting = true;
            remainingVineGrids = 11;
            castTimer = 0;
        }
    }

    @Override
    public void update(double dt) {
        super.update(dt); // Parent smooth movement

        // Timed grow/clear steps
        if (isCasting || isClearing) {
            castTimer += dt;
            if (castTimer >= GROW_INTERVAL) {
                castTimer = 0;
                if (isCasting) growOneStep();
                else if (isClearing) clearOneStep();
            }
        }
    }

    private void growOneStep() {
        if (currentCastCol < 0 || currentCastCol >= 16
                || currentCastRow < 0 || currentCastRow >= 16) {
            isCasting = false;
            return;
        }

        if (!canPlantVineAt(currentCastCol, currentCastRow)) {
            isCasting = false;
            return;
        }

        mapManager.setTile(currentCastCol, currentCastRow, 5);

        if (direction == DIR_UP) currentCastRow--;
        else if (direction == DIR_DOWN) currentCastRow++;
        else if (direction == DIR_LEFT) currentCastCol--;
        else if (direction == DIR_RIGHT) currentCastCol++;

        remainingVineGrids--;

        if (remainingVineGrids <= 0) {
            isCasting = false;
        }
    }

    public void clearOneStep() {
        int tile = mapManager.getTile(currentCastCol, currentCastRow);
        if(tile != 5)
        {   isClearing = false;
            return;
        }
        mapManager.setTile(currentCastCol, currentCastRow, 0);
        // Next cell along facing direction
        if (direction == DIR_UP) currentCastRow--;
        else if (direction == DIR_DOWN) currentCastRow++;
        else if (direction == DIR_LEFT) currentCastCol--;
        else if (direction == DIR_RIGHT) currentCastCol++;

        remainingVineGrids--;

        // Stop when step count exhausted
        if (remainingVineGrids <= 0) {
            isClearing = false;
        }
    }

}
