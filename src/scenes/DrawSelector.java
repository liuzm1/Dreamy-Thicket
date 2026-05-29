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

public class DrawSelector {
    private final Image selectorIcon;
    public DrawSelector(GameEngine engine) {
        selectorIcon = engine.loadImage("resource/sprites/menus/selector.png");
    }

    public void draw(GameEngine engine,int [][] btnAreas){
        core.GameInstance gi = (core.GameInstance) engine;
        int mx = gi.getMouseX();
        int my = gi.getMouseY();

        for (int[] btnArea : btnAreas) {
            int x = btnArea[0];
            int y = btnArea[1];
            int w = btnArea[2];
            int h = btnArea[3];

            if (mx >= x && mx <= x + w && my >= y && my <= y + h) {
                // Hover effect: draw small icon to the left of button
                engine.drawImage(selectorIcon,(x-32+w/2), y - 64, 64, 64);

                break; // Stop after first hovered button
            }
        }
    }
}
