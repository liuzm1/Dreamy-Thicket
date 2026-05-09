package scenes;
//修改人： 刘子墨
//功能：游戏结束菜单，展示，按钮区域，鼠标悬浮在按钮上出现小刺猬图标
import core.GameEngine;

import java.awt.*;
import java.awt.event.ActionListener;

public class HelpMenu implements GameScene{
    public final Image helpMenu_Img;
    public final Image helpTitle_broad;
    public final Image helpContent_broad;
    private int [][] btnAreas = {
            {195, 543,250,110}
    };
    public HelpMenu(GameEngine engine) {
        helpMenu_Img = engine.loadImage("resource/sprites/menus/help_bg.png");
        helpTitle_broad = engine.loadImage("resource/sprites/menus/help_board.png");
        helpContent_broad = engine.loadImage("resource/sprites/menus/help_content.png");
    }

    @Override
    public void draw(GameEngine engine) {
        engine.drawImage(helpMenu_Img, 0, 0,640,640);
        engine.changeColor(255, 0, 0);
        engine.drawImage(helpTitle_broad, 195, 58,250,110);
        engine.drawImage(helpTitle_broad, 195, 543,250,110);
        engine.changeColor(60, 40, 20);
        engine.drawBoldText(270, 610, "BACK", "Monospaced", 40);
        engine.drawBoldText(270, 125, "HELP", "Monospaced", 40);
        engine.drawImage(helpContent_broad, 125, 140,390,358);


        //定义按钮区域
        DrawSelector ds = new DrawSelector(engine);
        ds.draw(engine,btnAreas);
    }
    @Override
    public int handleMouseClick(int mx, int my) {
        if (checkInside(mx, my, btnAreas[0])) return 0;
        return -1;
    }


    public boolean checkInside(int mx, int my,int[] area){
        return mx >= area[0] && mx <= area[0] + area[2] &&
                my >= area[1] && my <= area[1] + area[3];
    }

}
