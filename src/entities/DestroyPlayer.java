package entities;

import core.GameEngine;
import maps.MapManager;

public class DestroyPlayer extends Player{

    public DestroyPlayer(GameEngine engine, MapManager mapManager, int startCol, int startRow) {
        //人物站在的格子位置
        this.mapManager = mapManager;
        this.col = startCol;
        this.row = startRow;
        //人物站在的像素位置
        this.x = startCol * TILE_SIZE;
        this.y = startRow * TILE_SIZE;
        isClearing = false;

        spriteSheet = engine.loadImage("resource/sprites/entities/P1.png");
    }


    @Override
    public void useSkill(MapManager mapManager){
        if(isMoving || isClearing) return ;

        // 1. 确定目标起点（面前第一格）
        currentCastCol = this.col;
        currentCastRow = this.row;
        if (direction == DIR_UP) currentCastRow--;
        else if (direction == DIR_DOWN) currentCastRow++;
        else if (direction == DIR_LEFT) currentCastCol--;
        else if (direction == DIR_RIGHT) currentCastCol++;

        // 2. 判定：是长还是消？
        int tile = mapManager.getTile(currentCastCol, currentCastRow);
        if(tile == 5){
            isClearing = true;
            remainingVineGrids = 11;
            castTimer = 0;
        }
    };

    @Override
    public void update(double dt) {
        super.update(dt); // 执行父类的平滑移动逻辑

        // 处理延时逻辑
        if (isCasting || isClearing) {
            castTimer += dt;
            if (castTimer >= GROW_INTERVAL) {
                castTimer = 0;
                if (isClearing) clearOneStep();
            }
        }
    }

    public void clearOneStep() {
        int tile = mapManager.getTile(currentCastCol, currentCastRow);
        if(tile != 5)
        {   isClearing = false;
            return;
        }
        mapManager.setTile(currentCastCol, currentCastRow, 0);
        // 计算下一格坐标
        if (direction == DIR_UP) currentCastRow--;
        else if (direction == DIR_DOWN) currentCastRow++;
        else if (direction == DIR_LEFT) currentCastCol--;
        else if (direction == DIR_RIGHT) currentCastCol++;

        remainingVineGrids--;

        // 次数用完则停止
        if (remainingVineGrids <= 0) {
            isClearing = false;
        }
    }


}
