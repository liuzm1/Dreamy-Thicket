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
import java.awt.event.ActionListener;

public class GameOverMenu implements GameScene{
    public final Image gameOverMunu_Image;

    private int[][] btnAreas = {{200,340,240,92},};  // Back to level select

    public GameOverMenu(GameEngine engine) {
        gameOverMunu_Image = engine.loadImage("resource/sprites/menus/bg_GameOver_bg.png");
    }
    @Override
    public void draw(GameEngine engine) {
        engine.drawImage(gameOverMunu_Image, 160, 180,320,280);
        // Define button areas
        DrawSelector ds = new DrawSelector(engine);
        ds.draw(engine,btnAreas);
    }
    @Override
    public int handleMouseClick(int mx, int my) {
        if (checkInside(mx, my, btnAreas[0])) return 3;
        return -1;
    }

    public boolean checkInside(int mx, int my,int[] area){
        return mx >= area[0] && mx <= area[0] + area[2] &&
                my >= area[1] && my <= area[1] + area[3];
    }
}
