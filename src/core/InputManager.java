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
package core;

import java.awt.event.KeyEvent;

public class InputManager {
    // 1P movement flags
    public boolean up, down, left, right;
    // 2P movement flags
    public boolean up_P2, down_P2, left_P2, right_P2;

    public void initKeys() {
        up = down = left = right = false;
        up_P2 = down_P2 = left_P2 = right_P2 = false;
    }

    public void handleKeyPressed(KeyEvent e, GameInstance game, AudioManager audio) {
        int key = e.getKeyCode();

        // ----------------- 1. Update movement key state (two independent sets) -----------------
        if (key == KeyEvent.VK_W) up = true;
        if (key == KeyEvent.VK_S) down = true;
        if (key == KeyEvent.VK_A) left = true;
        if (key == KeyEvent.VK_D) right = true;

        if (key == KeyEvent.VK_UP)    up_P2 = true;
        if (key == KeyEvent.VK_DOWN)  down_P2 = true;
        if (key == KeyEvent.VK_LEFT)  left_P2 = true;
        if (key == KeyEvent.VK_RIGHT) right_P2 = true;

        // ----------------- 2. Skills (one-shot; only while PLAYING) -----------------
        if (game.getCurrentState() == GameState.PLAYING) {

            // Q key: skill
            if (key == KeyEvent.VK_Q) {
                if (!game.isTwoPlayer) {
                    // Solo: 1P normal skill
                    if (game.getPlayer1() != null) {
                        game.getPlayer1().useSkill(game.getMapManager());
                        audio.playSkillSFX();
                    }
                } else {
                    // Co-op: 1P (DestroyPlayer) skill
                    if (game.getDestroyPlayer() != null) {
                        game.getDestroyPlayer().useSkill(game.getMapManager());
                        audio.playSkillSFX();
                    }
                }
            }

            // Space: skill (co-op 2P only)
            if (key == KeyEvent.VK_SPACE && game.isTwoPlayer) {
                if (game.getGeneratePlayer() != null) {
                    game.getGeneratePlayer().useSkill(game.getMapManager());
                    audio.playSkillSFX();
                }
            }
        }
    }

    public void handleKeyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        // On key release, clear matching movement flags
        if (key == KeyEvent.VK_W) up = false;
        if (key == KeyEvent.VK_S) down = false;
        if (key == KeyEvent.VK_A) left = false;
        if (key == KeyEvent.VK_D) right = false;

        if (key == KeyEvent.VK_UP)    up_P2 = false;
        if (key == KeyEvent.VK_DOWN)  down_P2 = false;
        if (key == KeyEvent.VK_LEFT)  left_P2 = false;
        if (key == KeyEvent.VK_RIGHT) right_P2 = false;
    }
}