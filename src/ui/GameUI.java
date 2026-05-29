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
 * In-game UI component.
 * Shows HP, score, 90s countdown, skill keys beside portraits, and +/- grow/clear hints.
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
        // --- Top resource bar ---
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

        // --- Center timer ---
        engine.drawImage(Board, 260, 580, 120, 45);
        engine.drawImage(clock_Icon, 253, 571, 57, 57);
        for (int i = 2; i >= 0; i--) {
            int divisor = (int) Math.pow(10, i);
            int digit = (int) timeLeft / divisor % 10;
            engine.drawImage(Numbers[digit], 288 + (2 - i) * 25, 575, 52, 52);
        }

        // ====================== 1P bottom status panel (left: Aila - clear) ======================
        engine.drawImage(Board, 30, 580, 180, 45);
        engine.drawImage(P1_headshot, 34, 573, 45, 50);

        // 1P (Q key): shown in co-op; red minus "-" for clear (isPlus = false)
        drawPixelKeyPrompt(engine, 85, 592, "Q", false, isTwoPlayer);

        // 1P HP
        for (int i = 0; i < hp && i < 3; i++) {
            engine.drawImage(heartImage, 118 + i * 24, 587, 32, 32);
        }

        // ====================== 2P bottom status panel (right: Oren - grow) ======================
        if (isTwoPlayer) {
            engine.drawImage(Board, 430, 580, 180, 45);
            engine.drawImage(P2_headshot, 561, 573, 45, 50);

            // 2P (SPC key): shown in co-op; green plus "+" for grow (isPlus = true)
            drawPixelKeyPrompt(engine, 523, 592, "SPC", true, true);

            // 2P HP
            for (int i = 0; i < hp && i < 3; i++) {
                engine.drawImage(heartImage, 487 - i * 24, 587, 32, 32);
            }
        }
    }

    /**
     * Draw pixel key prompt with drop shadow and optional +/- function indicator above.
     * @param isPlus true for green plus, false for red minus
     * @param showSymbol whether to show symbol (hidden in solo mode)
     */
    private void drawPixelKeyPrompt(GameEngine engine, int x, int y, String keyName, boolean isPlus, boolean showSymbol) {
        if (engine == null) return;

        int keyWidth = keyName.equals("SPC") ? 32 : 22;
        int keyHeight = 22;

        // ----------------- 1. Draw key cap -----------------
        // Outer black border
        engine.changeColor(Color.BLACK);
        engine.drawRectangle(x - 1, y - 1, keyWidth + 2, keyHeight + 2);

        // Retro gray fill
        engine.changeColor(new Color(225, 220, 215));
        engine.drawSolidRectangle(x, y, keyWidth, keyHeight);

        // Bottom raised key shadow
        engine.changeColor(new Color(175, 170, 165));
        engine.drawSolidRectangle(x, y + keyHeight - 3, keyWidth, 3);

        // Key label
        engine.changeColor(new Color(60, 55, 50));
        if (keyName.equals("SPC")) {
            engine.drawBoldText(x + 4, y + 16, "SPC", "Monospaced", 11);
        } else {
            engine.drawBoldText(x + 6, y + 17, "Q", "Monospaced", 14);
        }

        // ----------------- 2. Function symbol indicator above key -----------------
        if (showSymbol) {
            int symbolX = x + (keyWidth / 2) - 3; // Center symbol above key
            int symbolY = y - 9;                  // Float above key

            if (isPlus) {
                // Grow: draw green plus (+)
                engine.changeColor(Color.BLACK); // Symbol outline
                engine.drawSolidRectangle(symbolX - 1, symbolY + 2, 9, 3);
                engine.drawSolidRectangle(symbolX + 2, symbolY - 1, 3, 9);

                engine.changeColor(new Color(50, 215, 100)); // Bright green
                engine.drawSolidRectangle(symbolX, symbolY + 3, 7, 1);
                engine.drawSolidRectangle(symbolX + 3, symbolY, 1, 7);
            } else {
                // Clear: draw red minus (-)
                engine.changeColor(Color.BLACK); // Symbol outline
                engine.drawSolidRectangle(symbolX - 1, symbolY + 2, 9, 3);

                engine.changeColor(new Color(220, 90, 90)); // Red
                engine.drawSolidRectangle(symbolX, symbolY + 3, 7, 1);
            }
        }
    }
}