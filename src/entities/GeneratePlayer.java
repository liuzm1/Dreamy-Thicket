package entities;

import core.GameEngine;
import maps.CollisionCheck;
import maps.MapManager;

public class GeneratePlayer extends Player {

    public GeneratePlayer(GameEngine engine, MapManager mapManager, int startCol, int startRow) {
        this.mapManager = mapManager;
        this.col = startCol;
        this.row = startRow;
        this.x = startCol * TILE_SIZE;
        this.y = startRow * TILE_SIZE;

        isClearing = false;
        isCasting = false;

        spriteSheet = engine.loadImage("resource/sprites/entities/P2.png");
    }

    /** 玩家2 不能踏入藤蔓格 */
    @Override
    protected boolean canEnterTile(CollisionCheck collisionCheck, int col, int row) {
        if (collisionCheck.isVine(col, row)) return false;
        return !collisionCheck.isSolid(col, row);
    }

    @Override
    public void useSkill(MapManager mapManager) {
        if (isClearing || isMoving) return;

        currentCastCol = this.col;
        currentCastRow = this.row;
        if (direction == DIR_UP) currentCastRow--;
        else if (direction == DIR_DOWN) currentCastRow++;
        else if (direction == DIR_LEFT) currentCastCol--;
        else if (direction == DIR_RIGHT) currentCastCol++;

        if (canPlantVineAt(currentCastCol, currentCastRow)) {
            isCasting = true;
            remainingVineGrids = 11;
            castTimer = 0;
        }
    }

    @Override
    public void update(double dt) {
        super.update(dt);

        if (isCasting || isClearing) {
            castTimer += dt;
            if (castTimer >= GROW_INTERVAL) {
                castTimer = 0;
                if (isCasting) growOneStep();
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
}
