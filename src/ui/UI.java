package ui;

import core.GameEngine;
import java.awt.Color;
import java.awt.Image;

/**
 * 游戏界面 UI 组件
 * 显示生命值（图片）、得分（右上角）、90秒倒计时（中间）
 */
public class UI {
    private Image heartImage;
    private final int heartWidth = 32;
    private final int heartHeight = 32;

    public UI(GameEngine engine) {
        heartImage = engine.loadImage("resource/sprites/menus/heart_UI.png");
        if (heartImage == null) {
            heartImage = engine.loadImage("heart_UI.png");
        }
    }

    public void draw(GameEngine engine, int hp, int score, double timeLeft) {
        // 1. 生命值
        int startX = 20;
        int startY = 70;
        for (int i = 0; i < hp && i < 3; i++) {
            engine.drawImage(heartImage, startX + i * (heartWidth + 8), startY, heartWidth, heartHeight);
        }

        // 2. 得分
        engine.changeColor(Color.YELLOW);
        engine.drawText(640 - 150, startY + 25, "SCORE: " + score, "Arial", 26);

        // 3. 倒计时
        int minutes = (int) (timeLeft / 60);
        int seconds = (int) (timeLeft % 60);
        String timeStr = String.format("%02d:%02d", minutes, seconds);
        engine.changeColor(Color.CYAN);
        engine.drawText(280, startY + 25, timeStr, "Arial", 32);
    }
}