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
                // 悬浮效果：在按钮左侧画个小图标
                engine.drawImage(selectorIcon,(x-32+w/2), y - 64, 64, 64);

                break; // 找到一个悬浮就跳出循环
            }
        }
    }
}
