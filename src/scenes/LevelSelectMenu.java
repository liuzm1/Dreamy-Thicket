//修改人： 刘子墨
//功能：游戏结束菜单，展示，按钮区域，鼠标悬浮在按钮上出现小刺猬图标
package scenes;
import core.GameEngine;
import java.awt.Image;

public class LevelSelectMenu {
    private final Image levelSelectMenu_Img;
    public LevelSelectMenu(GameEngine engine) {
        levelSelectMenu_Img = engine.loadImage("resource/sprites/menus/LevelSelect_bg.png");
    }

    private int[][] btnAreas = {
            {64,246,84,84},  //关卡1
            {210,246,84,84}, //关卡2
            {350,246,84,84}, //关卡3
            {492,246,84,84}, //关卡4
            {210,415,84,84}, //关卡5
            {492,415,84,84}, //关卡6
            {14,576,154,54}, //返回菜单
            {490,576,134,64}  //开始游戏
    };
    public void draw(GameEngine engine){
        engine.changeColor(255,204,255);
        engine.drawImage(levelSelectMenu_Img,0,0,640,640);
        //定义按钮区域
        DrawSelector ds = new DrawSelector(engine);
        ds.draw(engine,btnAreas);
    }
}
