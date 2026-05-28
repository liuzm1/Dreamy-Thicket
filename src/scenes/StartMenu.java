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
//功能：游戏结束菜单，展示，按钮区域，鼠标悬浮在按钮上出现小刺猬图标
public class StartMenu implements GameScene{
    private final Image startMenu_Img;

    boolean isTwoPlayer = false;

    //定义按钮区域
    private int[][] btnAreas = {
            {15, 578, 154, 55}, // 0: 单人模式 [x, y, w, h]
            {186, 578, 154, 55}, // 1: 双人模式
            {338, 578, 154, 55}, // 2: 帮助
            {490, 578, 154, 55}  // 3: 退出
    };

    public StartMenu(GameEngine engine) {
        startMenu_Img = engine.loadImage("resource/sprites/menus/bg_start_bg.png");
    }
    //绘制界面和按钮悬浮
    @Override
    public void draw(GameEngine engine){
        engine.changeColor(255,204,255);
        engine.drawImage(startMenu_Img,0,0,640,640);
        //定义按钮区域
        DrawSelector ds = new DrawSelector(engine);
        ds.draw(engine,btnAreas);
    }


    //按钮交互
    @Override
    public int handleMouseClick(int mx, int my){
        if(checkInside(mx, my, btnAreas[0])) {
            isTwoPlayer = false;
            return 3;  //点击单人模式，跳转 选择关卡
        }
        if(checkInside(mx, my, btnAreas[1])) {
            isTwoPlayer = true;
            return 3;
        }
             //点击双人模式，跳转 选择关卡
        if(checkInside(mx, my, btnAreas[2])) return 6;  //点击HELP，跳转 HELP
        if(checkInside(mx, my, btnAreas[3])) System.exit(0); //点击 退出，退出
        return -1;
    }

    public boolean getIsTwoPlayer(){
        return isTwoPlayer;
    }

    //判断鼠标位置
    private boolean checkInside(int mx, int my, int[] area) {
        return mx >= area[0] && mx <= area[0] + area[2] &&
                my >= area[1] && my <= area[1] + area[3];
    }


}
