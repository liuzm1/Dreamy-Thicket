package core;
//游戏主类
import entities.SoloPlayer;
import maps.CollisionCheck;
import scenes.GameScene;
import maps.MapManager;
import scenes.MenuManager;

import java.awt.*;
import java.awt.event.MouseEvent;

public class GameInstance extends GameEngine {
    //定义窗口常量
    private final int WINDOW_SIZE = 640;
    //-------------------------------------------------------
    //游戏关卡地图管理
    private final MapManager mapManager = new MapManager(this);

    //-------------------------------------------------------
    //游戏菜单管理
    private final MenuManager menuManager = new MenuManager(this);

    private SoloPlayer player1;
    private CollisionCheck collisionCheck;
    // 仿照飞船案例：记录按键状态
    private boolean left, right, up, down;


    public GameInstance() {
        mapManager.loadLevel("resource/map1.txt");
    }

    //-------------------------------------------------------
    // Game
    //-------------------------------------------------------
    @Override
    public void init() {
        collisionCheck = new maps.CollisionCheck(mapManager);
        player1 = new SoloPlayer(this, 2, 2); // 假设从 (2,2) 开始

        // 初始状态下按键都是 false
        left = right = up = down = false;

    }

    // 修改 update 逻辑，让按键直接生效
    @Override
    public void update(double dt) {
        if (currentState == STATE_PLAYING && player1 != null) {
            player1.update(dt);
            // 直接根据按键状态调用 move
            if (up)    { player1.move(0, -1, collisionCheck); up = false; }
            if (down)  { player1.move(0, 1, collisionCheck);  down = false; }
            if (left)  { player1.move(-1, 0, collisionCheck); left = false; }
            if (right) { player1.move(1, 0, collisionCheck);  right = false; }
        }
    }



    @Override
    public void paintComponent() {
        changeColor(Color.BLACK);
        drawSolidRectangle(0, 0, 640, 640);

        // 2. 先画菜单和地图
        // 这里的 drawActiveMenu 内部肯定有清屏或者画大背景的逻辑
        menuManager.drawActiveMenu(this, currentState, mapManager);

        // 3. 【重点】在菜单画完之后，再画玩家
        // 只有在游戏中才画
        if (currentState == STATE_PLAYING && player1 != null) {
            // 这里先用你那个“绝对看得见”的蓝色方块试试
            // 如果蓝色方块动了，再换成 player1.draw(this);
            player1.draw(this);
        }

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
                    int levelNum = nextState - 100;
                    mapManager.loadLevel("resource/map" + levelNum + ".txt");
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


    @Override
    public void keyPressed(java.awt.event.KeyEvent e) {
        int key = e.getKeyCode();
        if (key == java.awt.event.KeyEvent.VK_W) up = true;
        if (key == java.awt.event.KeyEvent.VK_S) down = true;
        if (key == java.awt.event.KeyEvent.VK_A) left = true;
        if (key == java.awt.event.KeyEvent.VK_D) right = true;
    }

    @Override
    public void keyReleased(java.awt.event.KeyEvent e) {
        int key = e.getKeyCode();
        if (key == java.awt.event.KeyEvent.VK_W) up = false;
        if (key == java.awt.event.KeyEvent.VK_S) down = false;
        if (key == java.awt.event.KeyEvent.VK_A) left = false;
        if (key == java.awt.event.KeyEvent.VK_D) right = false;
    }


}