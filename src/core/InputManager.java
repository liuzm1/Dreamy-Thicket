package core;

import java.awt.event.KeyEvent;

public class InputManager {
    // 1P 移动开关
    public boolean up, down, left, right;
    // 2P 移动开关
    public boolean up_P2, down_P2, left_P2, right_P2;

    public void initKeys() {
        up = down = left = right = false;
        up_P2 = down_P2 = left_P2 = right_P2 = false;
    }

    public void handleKeyPressed(KeyEvent e, GameInstance game, AudioManager audio) {
        int key = e.getKeyCode();

        // ----------------- 1. 基础位移按键状态更新（两套按键独立响应） -----------------
        if (key == KeyEvent.VK_W) up = true;
        if (key == KeyEvent.VK_S) down = true;
        if (key == KeyEvent.VK_A) left = true;
        if (key == KeyEvent.VK_D) right = true;

        if (key == KeyEvent.VK_UP)    up_P2 = true;
        if (key == KeyEvent.VK_DOWN)  down_P2 = true;
        if (key == KeyEvent.VK_LEFT)  left_P2 = true;
        if (key == KeyEvent.VK_RIGHT) right_P2 = true;

        // ----------------- 2. 技能释放（单次触发行为，需限制在 PLAYING 状态） -----------------
        if (game.getCurrentState() == GameState.PLAYING) {

            // 【Q键】释放技能
            if (key == KeyEvent.VK_Q) {
                if (!game.isTwoPlayer) {
                    // 单人模式：1P 释放普通技能
                    if (game.getPlayer1() != null) {
                        game.getPlayer1().useSkill(game.getMapManager());
                        audio.playSkillSFX();
                    }
                } else {
                    // 双人模式：1P (DestroyPlayer) 释放技能
                    if (game.getDestroyPlayer() != null) {
                        game.getDestroyPlayer().useSkill(game.getMapManager());
                        audio.playSkillSFX();
                    }
                }
            }

            // 【空格键】释放技能（仅在双人模式下由 2P 触发）
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

        // 释放按键时，无条件把对应的布尔开关关掉
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