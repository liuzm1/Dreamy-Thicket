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
package scenes;

import core.GameEngine;
import java.awt.*;

public class HelpMenu implements GameScene {
    public final Image helpMenu_Img;
    public final Image helpTitle_broad;


    private int[][] btnAreas = {
            {195, 543, 250, 110}
    };

    public HelpMenu(GameEngine engine) {
        helpMenu_Img = engine.loadImage("resource/sprites/menus/bg_Help_bg.png");
        helpTitle_broad = engine.loadImage("resource/sprites/menus/help_board.png");
    }

    @Override
    public void draw(GameEngine engine) {
        // 1. Draw fantasy forest pixel background
        engine.drawImage(helpMenu_Img, 0, 0, 640, 640);
        engine.changeColor(new Color(25, 20, 15, 215));
        engine.drawSolidRectangle(115, 143, 410, 379);
        engine.drawSolidRectangle(123, 135, 394, 8);
        engine.drawSolidRectangle(123, 522, 394, 8);

        engine.drawImage(helpTitle_broad, 195, 40, 250, 110);
        engine.drawImage(helpTitle_broad, 195, 543, 250, 110);
        String fontName = "Monospaced";
        // 4. Wooden sign text (dark wood tone)
        engine.changeColor(55, 35, 15);
        // Title typography
        drawTextWithOutline(engine, 209,100, "HOW TO PLAY", fontName, 34,Color.white);
        drawTextWithOutline(engine, 253, 610, "RETURN", fontName, 34,Color.white);

        // ====================== 5. Main text layout (1px pixel outline) ======================

        // --- MISSION ---
        drawTextWithOutline(engine, 130, 185, "🌲 MISSION: SAVE THE FOREST 🌲", fontName, 17, Color.YELLOW);
        drawTextWithOutline(engine, 123, 212, "Collect Ruby Berries to reach Target Score in 90s!", fontName, 13, Color.WHITE);

        // --- SKILLS ---
        drawTextWithOutline(engine, 130, 255, "🎮 TWIN SPRITES SKILLS", fontName, 15, new Color(100, 230, 255));

        drawTextWithOutline(engine, 135, 280, "• 1P Aila [Pink] : Press [Q] to CLEAR Vines", fontName, 13, new Color(255, 180, 210));
        drawTextWithOutline(engine, 155, 300, "(Summon thorn walls to block monsters!)", fontName, 11, Color.LIGHT_GRAY);

        drawTextWithOutline(engine, 135, 330, "• 2P Oren [Blue] : Press [Space] to GROW Vines", fontName, 13, new Color(150, 200, 255));
        drawTextWithOutline(engine, 155, 350, "(Break dead branches & open escape paths!)", fontName, 11, Color.LIGHT_GRAY);

        // --- ENEMY ---
        drawTextWithOutline(engine, 130, 390, "✨ ITEMS & MONSTERS", fontName, 15, new Color(255, 200, 100));
        drawTextWithOutline(engine, 135, 415, "Ruby (+5) | Star (+10) | Oracle (?) -> 50/50 Luck!", fontName, 12, Color.WHITE);
        drawTextWithOutline(engine, 135, 440, "Watch out for Slimes, Shrooms and Fallen Sprites!", fontName, 12, new Color(255, 130, 130));

        // ====================== 6. Bottom grass area: control hints ======================
        engine.changeColor(Color.yellow);
        engine.drawBoldText(135, 505, "💡 Tip: Share Lives in Duo Mode! Watch your step!", fontName, 12);

        // 7. Hedgehog hover selector icon
        DrawSelector ds = new DrawSelector(engine);
        ds.draw(engine, btnAreas);
    }

    /** Pixel outline via basic draw calls. */
    private void drawTextWithOutline(GameEngine engine, int x, int y, String text, String font, int size, Color textColor) {
        // Black cross pattern for 1px outline
        engine.changeColor(Color.BLACK);
        engine.drawBoldText(x - 1, y, text, font, size);
        engine.drawBoldText(x + 1, y, text, font, size);
        engine.drawBoldText(x, y - 1, text, font, size);
        engine.drawBoldText(x, y + 1, text, font, size);
        // Center fill
        engine.changeColor(textColor);
        engine.drawBoldText(x, y, text, font, size);
    }

    @Override
    public int handleMouseClick(int mx, int my) {
        if (checkInside(mx, my, btnAreas[0])) return 0;
        return -1;
    }

    public boolean checkInside(int mx, int my, int[] area) {
        return mx >= area[0] && mx <= area[0] + area[2] &&
                my >= area[1] && my <= area[1] + area[3];
    }
}