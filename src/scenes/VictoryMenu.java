//修改人： 刘子墨
//功能：游戏结束菜单，展示，按钮区域，鼠标悬浮在按钮上出现小刺猬图标
package scenes;
import core.GameEngine;
import java.awt.Image;
import java.awt.event.ActionListener;

public class VictoryMenu implements GameScene{
    private final Image victoryMenu_Img;
    private final Image home_btn;

    private int[][] btnAreas = {
            {138, 582, 176, 36}, //重新玩 按钮
            {336, 582, 176, 36}, //下一关 按钮
            {534, 570, 90, 75} //主页按钮
    };

    public VictoryMenu(GameEngine engine) {
        victoryMenu_Img =  engine.loadImage("resource/sprites/menus/Victory_bg.png");
        home_btn = engine.loadImage("resource/sprites/menus/inVictoryMenuhome_btn.png");
    }

    @Override
    public void draw(GameEngine engine){
        engine.changeColor(255,204,255);
        engine.drawImage(victoryMenu_Img, 0, 0,640,640);
        engine.drawImage(home_btn,534,570,90,75);
        //定义按钮区域
        DrawSelector ds = new DrawSelector(engine);
        ds.draw(engine,btnAreas);
    }
    @Override
    public int handleMouseClick(int mx, int my) {
        // 重新玩：返回 1（重置当前关卡）
        if(checkInside(mx, my, btnAreas[0])) return 1;
        // 下一关：返回 2（进入下一关）
        if(checkInside(mx, my, btnAreas[1])) return 2;
        // 主页按钮：返回 0（回到主菜单）
        if(checkInside(mx, my, btnAreas[2])) return 0;
        // 无效点击，返回当前状态
        return 4;
    }

    private boolean checkInside(int mx, int my, int[] area) {
        return mx >= area[0] && mx <= area[0] + area[2] &&
                my >= area[1] && my <= area[1] + area[3];
    }
}
