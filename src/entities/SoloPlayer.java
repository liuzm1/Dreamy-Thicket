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
//            * 虽然现在不设计藤蔓逻辑，但因为父类是 abstract，这里必须实现
//     * 先留个空，保证程序能编译通过
//     */
    @Override
    public void useSkill(maps.MapManager map) {
        // 移动或施法中不可重复施法
        if (isCasting || isClearing || isMoving) return;
        // 1. 确定目标起点（面前第一格）
        currentCastCol = this.col;
        currentCastRow = this.row;
        if (direction == DIR_UP) currentCastRow--;
        else if (direction == DIR_DOWN) currentCastRow++;
        else if (direction == DIR_LEFT) currentCastCol--;
        else if (direction == DIR_RIGHT) currentCastCol++;

        // 2. 判定：是长还是消？
        int tile = mapManager.getTile(currentCastCol, currentCastRow);

        if (tile == 5) {
            // 如果面前已经是藤蔓，进入消除模式
            isClearing = true;
            remainingVineGrids = 11; // 消除距离也可以设长一点
            castTimer = 0;
        } else if (tile == 0) {
            // 如果面前是空地，进入生长模式
            isCasting = true;
            remainingVineGrids = 11;
            castTimer = 0;
        }
    }

    @Override
    public void update(double dt) {
        super.update(dt); // 执行父类的平滑移动逻辑

        // 处理延时逻辑
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
        // 1. 检查边界和地形
        if (currentCastCol < 0 || currentCastCol >= 16
                || currentCastRow < 0 || currentCastRow >= 16) {
            isClearing = false;
            return;
        }
        // 假设 1 是墙，6 是收集物 (Collectibles)
        int tile = mapManager.getTile(currentCastCol, currentCastRow);

        // 如果撞墙、超出地图、或者格子里有收集物，停止生长
        if (tile == 1 || tile == 6 || remainingVineGrids <= 0) {
            isCasting = false;
            return;
        }

        // 如果是空地，生成藤蔓
        if (tile == 0) {
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
