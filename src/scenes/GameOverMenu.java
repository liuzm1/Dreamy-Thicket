//修改人： 刘子墨
//功能：游戏结束菜单，展示，按钮区域，鼠标悬浮在按钮上出现小刺猬图标
package scenes;

import core.GameEngine;

import javax.swing.*;
import java.awt.*;

public class GameOverMenu {
    public final Image gameOverMunu_Image;

    private int[][] btnAreas = {{200,340,240,92},};

    public GameOverMenu(GameEngine engine) {
        gameOverMunu_Image = engine.loadImage("resource/sprites/menus/GameOver_bg.png");
    }
    public void draw(GameEngine engine) {

        engine.drawImage(gameOverMunu_Image, 160, 180,320,280);
        //定义按钮区域
        DrawSelector ds = new DrawSelector(engine);
        ds.draw(engine,btnAreas);
    }
}
