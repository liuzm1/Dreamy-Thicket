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

import javax.swing.*;
import java.awt.*;


public class PauseMenu implements GameScene{
    public final Image paeseMenu_Img;
    private int[][] btnAreas = {{200,340,240,92}};// Continue button
    public PauseMenu(GameEngine engine) {
        paeseMenu_Img = engine.loadImage("resource/sprites/menus/bg_Pause_bg.png");
    }
    @Override
    public void draw(GameEngine engine) {
        engine.drawImage(paeseMenu_Img, 160, 180,320,280);
        // Define button areas
        DrawSelector ds = new DrawSelector(engine);
        ds.draw(engine,btnAreas);
    }

    @Override
    public int handleMouseClick(int mx, int my){
        if(checkInside(mx, my, btnAreas[0])) return 1;
        return -1;
    }

    // Check if mouse is inside button area
    private boolean checkInside(int mx, int my, int[] area) {
        return mx >= area[0] && mx <= area[0] + area[2] &&
                my >= area[1] && my <= area[1] + area[3];
    }
}
