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
import java.awt.Image;
import java.awt.event.ActionListener;

public class VictoryMenu implements GameScene{
    private final Image victoryMenu_Img;
    private final Image home_btn;

    private int[][] btnAreas = {
            {138, 582, 176, 36}, // Replay button
            {336, 582, 176, 36}, // Next level button
            {534, 570, 90, 75} // Home button
    };

    public VictoryMenu(GameEngine engine) {
        victoryMenu_Img =  engine.loadImage("resource/sprites/menus/bg_Victory_bg.png");
        home_btn = engine.loadImage("resource/sprites/menus/btn_inVictoryMenuhome_btn.png");
    }

    @Override
    public void draw(GameEngine engine){
        engine.changeColor(255,204,255);
        engine.drawImage(victoryMenu_Img, 0, 0,640,640);
        engine.drawImage(home_btn,534,570,90,75);
        // Define button areas
        DrawSelector ds = new DrawSelector(engine);
        ds.draw(engine,btnAreas);
    }
    @Override
    public int handleMouseClick(int mx, int my) {
        // Replay: return 1 (reset current level)
        if(checkInside(mx, my, btnAreas[0])) return 1;
        // Next level: return 2 (advance to next level)
        if(checkInside(mx, my, btnAreas[1])) return 2;
        // Home button: return 0 (back to main menu)
        if(checkInside(mx, my, btnAreas[2])) return 0;
        // Invalid click, keep current state
        return 4;
    }

    private boolean checkInside(int mx, int my, int[] area) {
        return mx >= area[0] && mx <= area[0] + area[2] &&
                my >= area[1] && my <= area[1] + area[3];
    }
}
