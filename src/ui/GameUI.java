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
package ui;

import core.GameEngine;
import java.awt.Color;
import java.awt.Image;

/**
 * 游戏界面 UI 组件
 * 显示生命值、得分、90秒倒计时、技能按键紧贴头像聚合，以及【+-生成消除】符号引导
 */
public class GameUI {
    private Image heartImage;
    private final Image SocreBoardImage;
    private final Image Board;
    private final Image cup_Icon;
    private final Image star_Icon;
    private final Image clock_Icon;
    private final Image P1_headshot;
    private final Image P2_headshot;
    private final Image[] Numbers;

    public GameUI(GameEngine engine) {
        heartImage = engine.loadImage("resource/sprites/menus/ui_heart_UI.png");
        SocreBoardImage = engine.loadImage("resource/sprites/menus/ui_board.png");
        Board = engine.loadImage("resource/sprites/menus/ui_board_1.png");
        cup_Icon = engine.loadImage("resource/sprites/menus/icon_cup.png");
        star_Icon = engine.loadImage("resource/sprites/menus/icon_star.png");
        clock_Icon = engine.loadImage("resource/sprites/menus/icon_clock.png");

        Image player1 = engine.loadImage("resource/sprites/entities/P1.png");
        P1_headshot = engine.subImage(player1, 40, 2, 16, 19);

        Image player2 = engine.loadImage("resource/sprites/entities/P2.png");
        P2_headshot = engine.subImage(player2, 40, 2, 16, 19);

        Numbers = new Image[10];
        for (int i = 0; i < 10; i++) {
            Numbers[i] = engine.loadImage("resource/sprites/menus/numbers/" + i + ".png");
        }

        if (heartImage == null) {
            heartImage = engine.loadImage("ui_heart_UI.png");
        }
    }

    public void draw(GameEngine engine, int hp, int score, double timeLeft, int target, boolean isTwoPlayer) {
        // --- 顶部资源数据栏 ---
        engine.drawImage(SocreBoardImage, 10, 5, 120, 40);
        engine.drawImage(SocreBoardImage, 140, 5, 120, 40);
        engine.drawImage(cup_Icon, 6, -6, 57, 57);
        engine.drawImage(star_Icon, 135, -7, 58, 58);

        for (int i = 3; i >= 0; i--) {
            int divisor = (int) Math.pow(10, i);
            int digit = target / divisor % 10;
            engine.drawImage(Numbers[digit], 40 + (3 - i) * 18, 4, 45, 45);
        }

        for (int i = 3; i >= 0; i--) {
            int divisor = (int) Math.pow(10, i);
            int digit = score / divisor % 10;
            engine.drawImage(Numbers[digit], 170 + (3 - i) * 18, 4, 45, 45);
        }

        // --- 中间计时器 ---
        engine.drawImage(Board, 260, 580, 120, 45);
        engine.drawImage(clock_Icon, 253, 571, 57, 57);
        for (int i = 2; i >= 0; i--) {
            int divisor = (int) Math.pow(10, i);
            int digit = (int) timeLeft / divisor % 10;
            engine.drawImage(Numbers[digit], 288 + (2 - i) * 25, 575, 52, 52);
        }

        // ====================== 1P 底部状态牌 (左侧：奥恩 - 消除) ======================
        engine.drawImage(Board, 30, 580, 180, 45);
        engine.drawImage(P1_headshot, 34, 573, 45, 50);

        // 1P (Q键)：双人模式下显现，绘制代表【消除】的红减号 "-" (isPlus = false)
        drawPixelKeyPrompt(engine, 85, 592, "Q", false, isTwoPlayer);

        // 1P 血量
        for (int i = 0; i < hp && i < 3; i++) {
            engine.drawImage(heartImage, 118 + i * 24, 587, 32, 32);
        }

        // ====================== 2P 底部状态牌 (右侧：艾露 - 生成) ======================
        if (isTwoPlayer) {
            engine.drawImage(Board, 430, 580, 180, 45);
            engine.drawImage(P2_headshot, 561, 573, 45, 50);

            // 2P (SPC键)：双人模式下显现，绘制代表【生成】的绿加号 "+" (isPlus = true)
            drawPixelKeyPrompt(engine, 523, 592, "SPC", true, true);

            // 2P 血量
            for (int i = 0; i < hp && i < 3; i++) {
                engine.drawImage(heartImage, 487 - i * 24, 587, 32, 32);
            }
        }
    }

    /**
     * 辅助美化方法：绘制带立体阴影的像素按键，并根据模式在头顶附加功能指示符 (+/-)
     * @param isPlus true 画绿加号，false 画红减号
     * @param showSymbol 是否显示符号（单人模式不显示）
     */
    private void drawPixelKeyPrompt(GameEngine engine, int x, int y, String keyName, boolean isPlus, boolean showSymbol) {
        if (engine == null) return;

        int keyWidth = keyName.equals("SPC") ? 32 : 22;
        int keyHeight = 22;

        // ----------------- 1. 绘制键盘按键本体 -----------------
        // 外圈黑边
        engine.changeColor(Color.BLACK);
        engine.drawRectangle(x - 1, y - 1, keyWidth + 2, keyHeight + 2);

        // 填充复古灰底色
        engine.changeColor(new Color(225, 220, 215));
        engine.drawSolidRectangle(x, y, keyWidth, keyHeight);

        // 底部凸起按键阴影
        engine.changeColor(new Color(175, 170, 165));
        engine.drawSolidRectangle(x, y + keyHeight - 3, keyWidth, 3);

        // 按键文字
        engine.changeColor(new Color(60, 55, 50));
        if (keyName.equals("SPC")) {
            engine.drawBoldText(x + 4, y + 16, "SPC", "Monospaced", 11);
        } else {
            engine.drawBoldText(x + 6, y + 17, "Q", "Monospaced", 14);
        }

        // ----------------- 2. 头顶的【功能符号指示器】 -----------------
        if (showSymbol) {
            int symbolX = x + (keyWidth / 2) - 3; // 符号居中在按键头顶
            int symbolY = y - 9;                  // 漂浮在按键上方

            if (isPlus) {
                // 【生成】：画生机绿的【加号 +】
                engine.changeColor(Color.BLACK); // 符号黑边
                engine.drawSolidRectangle(symbolX - 1, symbolY + 2, 9, 3);
                engine.drawSolidRectangle(symbolX + 2, symbolY - 1, 3, 9);

                engine.changeColor(new Color(50, 215, 100)); // 亮丽的自然生机绿
                engine.drawSolidRectangle(symbolX, symbolY + 3, 7, 1);
                engine.drawSolidRectangle(symbolX + 3, symbolY, 1, 7);
            } else {
                // 【消除】：画警示红的【减号 -】
                engine.changeColor(Color.BLACK); // 符号黑边
                engine.drawSolidRectangle(symbolX - 1, symbolY + 2, 9, 3);

                engine.changeColor(new Color(220, 90, 90)); // 火焰红
                engine.drawSolidRectangle(symbolX, symbolY + 3, 7, 1);
            }
        }
    }
}