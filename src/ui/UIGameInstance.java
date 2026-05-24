package ui;

import core.GameEngine;
import core.GameInstance;
import maps.MapManager;
import entities.SoloPlayer;
import entities.Player;

import java.lang.reflect.Field;

public class UIGameInstance extends GameInstance {
    private UI gameUI;
    private int score = 0;
    private double timeLeft = 90.0;

    // 反射字段
    private Field fieldCurrentState;
    private Field fieldPlayer1;
    private Field fieldMapManager;
    private int STATE_PLAYING;

    @Override
    public void init() {
        super.init();
        gameUI = new UI(this);

        try {
            // 获取私有字段
            fieldCurrentState = GameInstance.class.getDeclaredField("currentState");
            fieldCurrentState.setAccessible(true);

            fieldPlayer1 = GameInstance.class.getDeclaredField("player1");
            fieldPlayer1.setAccessible(true);

            fieldMapManager = GameInstance.class.getDeclaredField("mapManager");
            fieldMapManager.setAccessible(true);

            Field fStatePlaying = GameInstance.class.getDeclaredField("STATE_PLAYING");
            fStatePlaying.setAccessible(true);
            STATE_PLAYING = fStatePlaying.getInt(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isPlaying() {
        try {
            return fieldCurrentState.getInt(this) == STATE_PLAYING;
        } catch (Exception e) {
            return false;
        }
    }

    private SoloPlayer getPlayer() {
        try {
            return (SoloPlayer) fieldPlayer1.get(this);
        } catch (Exception e) {
            return null;
        }
    }

    private MapManager getMapManager() {
        try {
            return (MapManager) fieldMapManager.get(this);
        } catch (Exception e) {
            return null;
        }
    }

    private int[][] getMapData() {
        try {
            MapManager mm = getMapManager();
            if (mm == null) return null;
            Field dataField = MapManager.class.getDeclaredField("mapData");
            dataField.setAccessible(true);
            return (int[][]) dataField.get(mm);
        } catch (Exception e) {
            return null;
        }
    }

    private void setTile(int col, int row, int value) {
        int[][] data = getMapData();
        if (data != null && row >= 0 && row < 16 && col >= 0 && col < 16) {
            data[row][col] = value;
        }
    }

    private int getPlayerHp(SoloPlayer p) {
        try {
            // Player 类中的 hp 字段是 protected
            Field hpField = Player.class.getDeclaredField("hp");
            hpField.setAccessible(true);
            return hpField.getInt(p);
        } catch (Exception e) {
            return 3; // 默认生命值
        }
    }

    @Override
    public void update(double dt) {
        super.update(dt);

        if (isPlaying()) {
            // 倒计时更新
            timeLeft -= dt;
            if (timeLeft < 0) timeLeft = 0;

            // 拾取物品逻辑
            SoloPlayer p = getPlayer();
            if (p != null) {
                int col = p.col;
                int row = p.row;
                int[][] mapData = getMapData();
                if (mapData != null) {
                    int tile = mapData[row][col];
                    if (tile == 2) {      // 花朵
                        score += 10;
                        setTile(col, row, 0);
                    } else if (tile == 3) { // 蘑菇
                        score += 20;
                        setTile(col, row, 0);
                    }
                }
            }
        }
    }

    @Override
    public void paintComponent() {
        super.paintComponent();

        if (isPlaying()) {
            SoloPlayer p = getPlayer();
            if (p != null && gameUI != null) {
                int hp = getPlayerHp(p);
                gameUI.draw(this, hp, score, timeLeft);
            }
        }
    }

    public static void main(String[] args) {
        GameEngine.createGame(new UIGameInstance(), 60);
    }
}