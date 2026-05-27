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

        // 绑定不同关卡目标分数
        if (levelNum == 1)      game.setTargetScore(80);
        else if (levelNum == 2) game.setTargetScore(120);
        else if (levelNum == 3) game.setTargetScore(160);

        enemyManager.setupEnemiesForLevel(levelNum, collisionCheck);
    }

    public void nextLevel(MapManager mapManager, EnemyManager enemyManager, CollisionCheck collisionCheck) {
        int levelNum = getLevelNum() + 1;
        if (levelNum > 3) levelNum = 1; // 超过关卡上限循环回第1关
        loadLevel(100 + levelNum, mapManager, enemyManager, collisionCheck);
    }
}