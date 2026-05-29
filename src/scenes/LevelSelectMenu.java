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

public class LevelSelectMenu implements GameScene{
    private final Image levelSelectMenu_Img;
    public LevelSelectMenu(GameEngine engine) {
        levelSelectMenu_Img = engine.loadImage("resource/sprites/menus/bg_LevelSelect_bg.png");
    }

    private final int[][] btnAreas = {
            {64,246,84,84},  // Level 1
            {210,246,84,84}, // Level 2
            {350,246,84,84}, // Level 3
            {492,246,84,84}, // Level 4
            {210,415,84,84}, // Level 5
            {492,415,84,84}, // Level 6
            {14,576,154,54}, // Back to menu
            {490,576,134,64}  // Start game
    };

    @Override
    public void draw(GameEngine engine){
        engine.changeColor(255,204,255);
        engine.drawImage(levelSelectMenu_Img,0,0,640,640);
        // Define button areas
        DrawSelector ds = new DrawSelector(engine);
        ds.draw(engine,btnAreas);
    }

    @Override
    public int handleMouseClick(int mx, int my){
        if(checkInside(mx, my, btnAreas[0])) return 101;
        if(checkInside(mx, my, btnAreas[1])) return 102;
        if(checkInside(mx, my, btnAreas[2])) return 103;
        if(checkInside(mx, my, btnAreas[6])) return 0;
        // Start game button: begin from level 1
        if(checkInside(mx, my, btnAreas[7])) return 101;
        return -1;
    }

    public boolean checkInside(int mx, int my,int[] area){
        return mx >= area[0] && mx <= area[0] + area[2] &&
                my >= area[1] && my <= area[1] + area[3];
    }
}
