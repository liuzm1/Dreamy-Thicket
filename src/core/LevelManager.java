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
package core;

import maps.MapManager;
import maps.CollisionCheck;

public class LevelManager {
    private final GameInstance game;
    private int currentLevel = 101;

    public LevelManager(GameInstance game) {
        this.game = game;
    }

    public int getCurrentLevel() { return currentLevel; }
    public void setCurrentLevel(int currentLevel) { this.currentLevel = currentLevel; }

    public int getLevelNum() {
        return this.currentLevel - 100;
    }

    public void loadLevel(int targetLevelState, MapManager mapManager, EnemyManager enemyManager, CollisionCheck collisionCheck) {
        this.currentLevel = targetLevelState;
        int levelNum = targetLevelState - 100;

        mapManager.loadLevel("resource/map" + levelNum + ".txt");

        // Per-level target score
        if (levelNum == 1)      game.setTargetScore(150);
        else if (levelNum == 2) game.setTargetScore(200);
        else if (levelNum == 3) game.setTargetScore(230);

        enemyManager.setupEnemiesForLevel(levelNum, collisionCheck);
    }

    public void nextLevel(MapManager mapManager, EnemyManager enemyManager, CollisionCheck collisionCheck) {
        int levelNum = getLevelNum() + 1;
        if (levelNum > 3) levelNum = 1; // Wrap to level 1 after max level
        loadLevel(100 + levelNum, mapManager, enemyManager, collisionCheck);
    }
}