package entities;

import core.GameEngine;
import maps.MapManager;

public class GeneratePlayer extends Player{

    public GeneratePlayer(GameEngine engine, MapManager mapManager, int startCol, int startRow) {
        super(engine, mapManager, startCol, startRow);
        isClearing = false;

        spriteSheet = engine.loadImage("resource/sprites/entities/P2.png");
    }


    @Override
    public void useSkill(MapManager mapManager){
        if(isClearing || isMoving) return;
        // 1. 确定目标起点（面前第一格）
        currentCastCol = this.col;
        currentCastRow = this.row;
        if (direction == DIR_UP) currentCastRow--;
        else if (direction == DIR_DOWN) currentCastRow++;
        else if (direction == DIR_LEFT) currentCastCol--;
        else if (direction == DIR_RIGHT) currentCastCol++;

        // 2. 判定：是长还是消？
        int tile = mapManager.getTile(currentCastCol, currentCastRow);

        if(tile == 0){
            isCasting = true;
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
                if (isCasting) growOneStep();
            }
        }
    }

    private void growOneStep() {
        // 1. 检查边界和地形
        // 假设 1 是墙，6 是收集物 (Collectibles)
        int tile = mapManager.getTile(currentCastCol, currentCastRow);
        // 检查对手是否挡路
        boolean isOpponentHere = (opponent != null &&
                opponent.col == currentCastCol &&
                opponent.row == currentCastRow);

        // 如果撞墙、超出地图、或者格子里有收集物，停止生长
        if (tile != 0 || isOpponentHere) {
            isCasting = false;
            return;
        }else {
            mapManager.setTile(currentCastCol, currentCastRow, 5);
        }

        // 计算下一格坐标
        if (direction == DIR_UP) currentCastRow--;
        else if (direction == DIR_DOWN) currentCastRow++;
        else if (direction == DIR_LEFT) currentCastCol--;
        else if (direction == DIR_RIGHT) currentCastCol++;

        remainingVineGrids--;

        // 次数用完则停止
        if (remainingVineGrids <= 0) {
            isCasting = false;
        }
    }




}
