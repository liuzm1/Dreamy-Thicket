package ui;
import core.GameEngine;
import scenes.DrawSelector;
import scenes.GameScene;

import java.awt.*;

public class InGameUI implements GameScene {
    private final Image inGameHome_btn;
    private final Image inGamePause_btn;
    private final Image inGameResume_btn;
    private final Image btn_bg;
    private final Image heart_UI;

    private final int[][] btnAreas = {
            {590, 0, 45, 45},  //inGameHome_btn
            {540, 0, 45, 45},  //inGamePause_btn
            {490, 0, 45, 45}  //inGameResume_btn
    };

    public InGameUI(GameEngine engine) {
        inGameHome_btn = engine.loadImage("resource/sprites/menus/inGamehome_btn1.png");
        inGamePause_btn = engine.loadImage("resource/sprites/menus/inGamepause_btn.png");
        inGameResume_btn =  engine.loadImage("resource/sprites/menus/inGameReset_btn.png");
        btn_bg = engine.loadImage("resource/sprites/menus/btn_bg.png");
        heart_UI = engine.loadImage("resource/sprites/menus/heart_UI.png");
    }

    public int getPlayerHealth() {
        return 3;//共三条命
    }
        @Override
    public void draw(GameEngine engine){
        engine.changeColor(0,0,0);
        engine.drawImage(btn_bg,590,0,45,45);
        engine.drawImage(btn_bg,540,0,45,45);
        engine.drawImage(btn_bg,490,0,45,45);
        engine.drawImage(inGameHome_btn,590,0,45,45);
        engine.drawImage(inGamePause_btn,540,0,45,45);
        engine.drawImage(inGameResume_btn,490,0,45,45);
        //共三条命，血量条放置在右上角
        int hp = getPlayerHealth();
        for(int i=0;i<hp;i++){
            engine.drawImage(heart_UI, 15 + (i * 35), 60, 30, 30);
        }
    }

    @Override
    public int handleMouseClick(int mx, int my){
        if(checkInside(mx, my, btnAreas[0])) return 0;  // 第一个按钮点击是 返回主页
        //第二个按钮点击是 游戏暂停
        if(checkInside(mx, my, btnAreas[1])) return 2;
        //第三个按钮点击是 重新开始游戏
        if(checkInside(mx, my, btnAreas[2])) return 1;

        return -1;
    }

    //判断鼠标位置
    private boolean checkInside(int mx, int my, int[] area) {
        return mx >= area[0] && mx <= area[0] + area[2] &&
                my >= area[1] && my <= area[1] + area[3];
    }

}
