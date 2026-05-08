package core;
//游戏主类
import scenes.MapManager;
import scenes.StartMenu;

import java.awt.*;
import java.util.Map;

public class GameInstance extends GameEngine {
    //定义地图常量
    private final int WINDOW_SIZE = 640;
    private MapManager mapManager;
    private StartMenu startMenu;
    //3: 草地 + 蘑菇
    @Override
    public void init() {
        //设置正方形窗口
        setupWindow(650, 650);
        mapManager = new MapManager(this);
        startMenu = new StartMenu(this);
        mapManager.loadLevel("resource/map1.txt");
    }

    @Override
    public void update(double dt) {
        //
    }

    @Override
    public void paintComponent() {
        //把背景涂黑，防止闪烁
        //mapManager.draw(this);
        //先绘制一个测试网格
       //drawDebugGrid();

        startMenu.draw(this);
    }

    private void drawDebugGrid() {
        changeColor(Color.white);
        for (int i = 0; i <= WINDOW_SIZE; i += 40) {
            drawLine(i, 0, i, WINDOW_SIZE); // 画竖线
            drawLine(0, i, WINDOW_SIZE, i); //画横线
        }
    }


}