package core;
//游戏主类
import scenes.GameScene;
import maps.MapManager;
import scenes.MenuManager;

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
        //mapManager.loadLevel("resource/map1.txt");

        //测试
        //currentState = STATE_PLAYING;
        //menuManager.switchScene(STATE_PLAYING);

    }

    @Override
    public void update(double dt) {
        //  只有当状态不是“暂停”时，才更新游戏逻辑
        //    if (currentState != STATE_PAUSED) {
        //        // 执行：角色移动、碰撞检测、计时器等
        //        // player.move();
        //        // enemy.update();
        //    }
        // 如果是暂停状态，update 里的逻辑会被跳过，小人就“定”住了
    }

    @Override
    public void paintComponent() {
        //先绘制一个测试网格
       drawDebugGrid();

        // 把当前状态告诉menuManager，MenuManager看着画
        menuManager.drawActiveMenu(this, currentState, mapManager);

        // 2.画人物—— 让在这里写代码，别去动MenuManager
        // player.draw(this);


        //--------------------
        //鼠标显示坐标
        //---------------------
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

    @Override
    public void mousePressed(MouseEvent e) {
        int mx = e.getX();
        int my = e.getY();

        // 1. 获取当前活跃的场景对象
        GameScene currentScene = null;
        if (currentState == STATE_PLAYING) {
            currentScene = menuManager.inGameUI;
        } else {
            // 根据当前状态从 menuManager 拿到对应的菜单实例
            switch (currentState) {
                case STATE_START_MENU:   currentScene = menuManager.startMenu; break;
                case STATE_PAUSED:       currentScene = menuManager.pauseMenu; break;
                case STATE_LEVEL_SELECT: currentScene = menuManager.levelSelectMenu; break;
                case STATE_VICTOR:       currentScene = menuManager.victoryMenu; break;
                case STATE_GAME_OVER:    currentScene = menuManager.gameOverMenu; break;
                case STATE_HELP:         currentScene = menuManager.helpMenu; break;
            }
        }

        // 2. 统一处理点击逻辑
        if (currentScene != null) {
            int nextState = currentScene.handleMouseClick(mx, my);

            // 如果点击有效（不为 -1）
            if (nextState != -1) {

                // --- A. 处理关卡跳转逻辑 (nextState >= 100) ---
                if (nextState >= 100) {
                    int levelNum = nextState - 100;
                    mapManager.loadLevel("resource/map" + levelNum + ".txt");

                    currentState = STATE_PLAYING; // 强制进入游戏状态
                    menuManager.switchScene(STATE_PLAYING);
                }

                // --- B. 处理游戏内“重新开始” (点完还是当前状态 1) ---
                else if (nextState == STATE_PLAYING && currentState == STATE_PLAYING) {
                    // 假设默认重启第一关，或者你可以定义一个变量记住当前是第几关
                    int levelNum = nextState - 100;
                    mapManager.loadLevel("resource/map" + levelNum + ".txt");
                    // 如果有玩家对象，记得重置位置：player.reset();
                }
                // --- C. 处理普通页面切换 ---
                else if (nextState != currentState) {
                    currentState = nextState;
                    menuManager.switchScene(nextState);
                }
            }
        }
    }

    // 补充两个获取方法，供菜单判断使用
    public int getMouseX() {
        return mouseX;
    }

    public int getMouseY() {
        return mouseY;
    }


}