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
import scenes.DrawSelector;
import java.awt.Image;
// Game over menu: display, button areas, hedgehog icon on hover
public class StartMenu implements GameScene{
    private final Image startMenu_Img;

    boolean isTwoPlayer = false;

    // Define button areas
    private int[][] btnAreas = {
            {15, 578, 154, 55}, // 0: solo mode [x, y, w, h]
            {186, 578, 154, 55}, // 1: co-op mode
            {338, 578, 154, 55}, // 2: help
            {490, 578, 154, 55}  // 3: quit
    };

    public StartMenu(GameEngine engine) {
        startMenu_Img = engine.loadImage("resource/sprites/menus/bg_start_bg.png");
    }
    // Draw UI and button hover
    @Override
    public void draw(GameEngine engine){
        engine.changeColor(255,204,255);
        engine.drawImage(startMenu_Img,0,0,640,640);
        // Define button areas
        DrawSelector ds = new DrawSelector(engine);
        ds.draw(engine,btnAreas);
    }


    // Button interaction
    @Override
    public int handleMouseClick(int mx, int my){
        if(checkInside(mx, my, btnAreas[0])) {
            isTwoPlayer = false;
            return 3;  // Solo mode: go to level select
        }
        if(checkInside(mx, my, btnAreas[1])) {
            isTwoPlayer = true;
            return 3;
        }
             // Co-op mode: go to level select
        if(checkInside(mx, my, btnAreas[2])) return 6;  // Help
        if(checkInside(mx, my, btnAreas[3])) System.exit(0); // Quit
        return -1;
    }

    public boolean getIsTwoPlayer(){
        return isTwoPlayer;
    }

    // Check if mouse is inside button area
    private boolean checkInside(int mx, int my, int[] area) {
        return mx >= area[0] && mx <= area[0] + area[2] &&
                my >= area[1] && my <= area[1] + area[3];
    }


}
