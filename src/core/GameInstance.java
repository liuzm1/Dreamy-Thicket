package core;
//游戏主类
import scenes.MapManager;
import scenes.MenuManager;
import scenes.VictoryMenu;

import java.awt.*;
import java.awt.event.MouseEvent;

public class GameInstance extends GameEngine {
    //定义窗口常量
    private final int WINDOW_SIZE = 640;

    //-------------------------------------------------------
    // 定义游戏状态常量
    //-------------------------------------------------------
    private final int STATE_START_MENU = 0;
    private final int STATE_PLAYING = 1;
    private final int STATE_PAUSED = 2;
    private final int STATE_LEVEL_SELECT = 3;
    private final int STATE_VICTOR = 4;
    private final int STATE_GAME_OVER = 5;
    private final int STATE_HELP = 6;

    private int currentState = STATE_START_MENU; // 默认在主菜单

    //-------------------------------------------------------
    //游戏关卡地图管理
    //-------------------------------------------------------
    private final MapManager mapManager = new MapManager(this);

    //-------------------------------------------------------
    //游戏菜单管理
    //-------------------------------------------------------
    private final MenuManager menuManager = new MenuManager(this);


    public GameInstance() {
    }

    //-------------------------------------------------------
    // Game
    //-------------------------------------------------------
    @Override
    public void init() {
        //测试地图是否能正常加载
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
       drawDebugGrid();
       //为了测试
       // menuManager.pauseMenu.draw(this);
        // 把当前状态告诉menuManager，MenuManager 你看着画
        menuManager.drawActiveMenu(this, STATE_HELP, mapManager);

        changeColor(Color.YELLOW);
        // 使用引擎自带的 drawText，坐标设为 (10, 30) 避免被标题栏遮挡
        drawText(10, 30, "X: " + mouseX + "  Y: " + mouseY, "Arial", 18);

    }

    private void drawDebugGrid() {
        changeColor(Color.white);
        for (int i = 0; i <= WINDOW_SIZE; i += 40) {
            drawLine(i, 0, i, WINDOW_SIZE); // 画竖线
            drawLine(0, i, WINDOW_SIZE, i); //画横线
        }
    }


    // 在 GameInstance 类内部添加
    private int mouseX, mouseY;

    // 覆写鼠标移动方法，实时更新坐标变量
    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    // 补充两个获取方法，供菜单判断使用
    public int getMouseX() {
        return mouseX;
    }

    public int getMouseY() {
        return mouseY;
    }


}