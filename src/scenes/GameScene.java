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
 **/package scenes;
// Drawing and click-handling interface
import core.GameEngine;

public interface GameScene {
    // Each scene draws itself
    void draw(GameEngine engine);
    // Each scene handles its own clicks and returns the next state
    // Return -1 or current state to stay on the same screen
    int handleMouseClick(int mx, int my);


}
