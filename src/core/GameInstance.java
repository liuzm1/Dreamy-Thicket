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

        // 1. 根据当前状态找到对应的菜单界面
        GameScene currentScene = (currentState == STATE_PLAYING) ?
                menuManager.inGameUI : getActiveMenu();

        if (currentScene == null) return;

        // 2. 获取点击后的下一个状态
        int nextState = currentScene.handleMouseClick(mx, my);
        if (nextState == -1) return; // 无效点击直接返回

        // 3. 分支处理

        // --- A. 进入新关卡 (从关卡选择界面点进来) ---
        if (nextState >= 100) {
            int levelNum = nextState - 100;
            mapManager.loadLevel("resource/map" + levelNum + ".txt");

            // 关键：重置玩家
            if (player1 != null) player1.reset(2, 2);

            currentState = STATE_PLAYING;
            menuManager.switchScene(STATE_PLAYING);
        }

        // --- B. 重新开始当前关卡 (在游戏中点击了重置) ---
        else if (nextState == STATE_PLAYING && currentState == STATE_PLAYING) {
            // 这里可以直接重新 reset 玩家，地图可以根据需要重载或不载
            if (player1 != null) player1.reset(2, 2);
            System.out.println("关卡已重置");
        }

        // --- C. 从菜单返回游戏 (真正的 Resume) ---
        else if (nextState == STATE_PLAYING && currentState != STATE_PLAYING) {
            // 这种情况下通常不 reset 玩家，让他接着玩
            currentState = STATE_PLAYING;
            menuManager.switchScene(STATE_PLAYING);
        }

        // --- D. 普通页面切换 (主菜单、帮助、暂停等) ---
        else if (nextState != currentState) {
            // 如果是从游戏切回主菜单，也可以考虑在这里顺便 reset 一下
            if (nextState == STATE_START_MENU && player1 != null) {
                player1.reset(2, 2);
            }

            currentState = nextState;
            menuManager.switchScene(nextState);
        }
    }

    // 辅助方法：把原来的 switch 提出来，让主方法变干净
    private GameScene getActiveMenu() {
        switch (currentState) {
            case STATE_START_MENU:   return menuManager.startMenu;
            case STATE_PAUSED:       return menuManager.pauseMenu;
            case STATE_LEVEL_SELECT: return menuManager.levelSelectMenu;
            case STATE_VICTOR:       return menuManager.victoryMenu;
            case STATE_GAME_OVER:    return menuManager.gameOverMenu;
            case STATE_HELP:         return menuManager.helpMenu;
            default: return null;
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