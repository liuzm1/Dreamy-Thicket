package core;
//游戏主类
import entities.DestroyPlayer;
import entities.GeneratePlayer;
import entities.SoloPlayer;
import maps.CollisionCheck;
import scenes.GameScene;
import maps.MapManager;
import scenes.MenuManager;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class GameInstance extends GameEngine {
    //定义窗口常量
    private final int WINDOW_SIZE = 640;
    //-------------------------------------------------------
    //Maps
    //-------------------------------------------------------
    private final MapManager mapManager = new MapManager(this);
    private CollisionCheck collisionCheck;
    //-------------------------------------------------------
    // Menus
    //-------------------------------------------------------
    private final MenuManager menuManager = new MenuManager(this);
    int currentLevel;


    //-------------------------------------------------------
    // Players
    //-------------------------------------------------------
    private SoloPlayer player1;
    private GeneratePlayer generatePlayer;
    private DestroyPlayer destroyPlayer;
    //双人游戏状态
    public boolean isTwoPlayer = false;
    //玩家初始化
    private void initPlayers(){
        player1 = new SoloPlayer(this,mapManager, 5, 5); // 从 (5,5) 开始
        destroyPlayer = new DestroyPlayer(this,mapManager, 5, 5);
        generatePlayer = new GeneratePlayer(this,mapManager, 11, 11);
        destroyPlayer.setOpponent(generatePlayer);
        generatePlayer.setOpponent(destroyPlayer);
    }
    //-------------------------------------------------------
    // Enemies
    //-------------------------------------------------------






    //-------------------------------------------------------
    // Items
    //-------------------------------------------------------




    //-------------------------------------------------------
    // keys
    //-------------------------------------------------------
    //记录按键状态
    private boolean left, right, up, down;
    private boolean left_P2, right_P2, up_P2, down_P2;
    //按键初始化
    private void initKeys(){
        // 初始状态下按键都是 false
        left = right = up = down = false;
        left_P2 = right_P2 = up_P2 = down_P2 = false;
    }




    //-------------------------------------------------------
    // Game
    //-------------------------------------------------------
    @Override
    public void init() {
        currentLevel = 101;
        collisionCheck = new maps.CollisionCheck(mapManager);
        //====【玩家初始化加载中...】====
        initPlayers();

        initKeys();

    }

    // 修改 update 逻辑，让按键直接生效
    @Override
    public void update(double dt) {
        if (currentState == STATE_PLAYING) {
            if(!isTwoPlayer) {
                if(player1 != null) {
                    player1.update(dt);
                    // 直接根据按键状态调用 move
                    if (up) player1.move(0, -1, collisionCheck,null);
                    if (down) player1.move(0, 1, collisionCheck,null);
                    if (left) player1.move(-1, 0, collisionCheck,null);
                    if (right) player1.move(1, 0, collisionCheck,null);
                }
            }else{
                if(destroyPlayer != null) {
                    destroyPlayer.update(dt);
                    // 直接根据按键状态调用 move
                    if (up) destroyPlayer.move(0, -1, collisionCheck,generatePlayer);
                    if (down) destroyPlayer.move(0, 1, collisionCheck,generatePlayer);
                    if (left) destroyPlayer.move(-1, 0, collisionCheck,generatePlayer);
                    if (right) destroyPlayer.move(1, 0, collisionCheck,generatePlayer);
                }
                if(generatePlayer != null) {
                    generatePlayer.update(dt);
                    if (up_P2) generatePlayer.move(0, -1, collisionCheck,destroyPlayer);
                    if (down_P2) generatePlayer.move(0, 1, collisionCheck,destroyPlayer);
                    if (left_P2) generatePlayer.move(-1, 0, collisionCheck,destroyPlayer);
                    if (right_P2) generatePlayer.move(1, 0, collisionCheck,destroyPlayer);
                }
            }
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
        if (currentState == STATE_PLAYING) {
            if(!isTwoPlayer) {
                if(player1 != null) player1.draw(this);
            }else{
                if(destroyPlayer != null) destroyPlayer.draw(this);
                if(generatePlayer != null) generatePlayer.draw(this);
            }

            //辅助网格 最后要删除
            drawDebugGrid();
            int mx = getMouseX();
            int my = getMouseY();
            changeColor(Color.YELLOW);
            drawBoldText(10, 40, "(" + (mx/40) + " , " + (my/40 ) + ")");
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
        GameScene currentScene = (currentState == STATE_PLAYING) ? menuManager.inGameUI : getActiveMenu();

        if (currentScene == null) return;

        // 2. 获取点击后的下一个状态
        int nextState = currentScene.handleMouseClick(mx, my);
        if(currentState == STATE_START_MENU) {
            isTwoPlayer = menuManager.startMenu.getIsTwoPlayer();
        }
        if (nextState == -1) return; // 无效点击直接返回

        // 3. 分支处理

        // --- A. 进入新关卡 (从关卡选择界面点进来) ---
        if (nextState >= 100) {
            currentLevel = nextState; // 重点：把这个 101 记下来
            int levelNum = nextState - 100;
            mapManager.loadLevel("resource/map" + levelNum + ".txt");

            // 关键：重置玩家
            if(!isTwoPlayer) {
            if (player1 != null) player1.reset(5, 5);
            }else{
                if(destroyPlayer != null) destroyPlayer.reset(5, 5);
                if(generatePlayer != null) generatePlayer.reset(11, 11);
            }


            currentState = STATE_PLAYING;
            menuManager.switchScene(STATE_PLAYING);
        }

        // --- B. 重新开始当前关卡 (在游戏中点击了重置) ---
        else if (nextState == STATE_PLAYING && currentState == STATE_PLAYING) {
            // 这里可以直接重新 reset 玩家和地图
            // 这里的 currentLevel 可能是 101, 102 或 103
            int levelNum = currentLevel - 100;

            // 这样加载的路径就是正确的 resource/map1.txt 了！
            mapManager.loadLevel("resource/map" + levelNum + ".txt");
            if(!isTwoPlayer) {
                if (player1 != null) player1.reset(5, 5);

            }else{
                if(destroyPlayer != null) destroyPlayer.reset(5, 5);
                if(generatePlayer != null) generatePlayer.reset(11, 11);
            }
        }

        // --- C. 从菜单返回游戏 ---
        else if (nextState == STATE_PLAYING && currentState != STATE_PLAYING) {
            // 这种情况下通常不 reset 玩家，让他接着玩
            currentState = STATE_PLAYING;
            menuManager.switchScene(STATE_PLAYING);
        }

        // --- D. 普通页面切换 (主菜单、帮助、暂停等) ---
        else if (nextState != currentState) {
            // 如果是从游戏切回主菜单，也可以考虑在这里顺便 reset 一下
            if (nextState == STATE_START_MENU && player1 != null) {
                player1.reset(5, 5);
                destroyPlayer.reset(5, 5);
                generatePlayer.reset(11, 11);
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
        if (key == KeyEvent.VK_W) up = true;
        if (key == KeyEvent.VK_S) down = true;
        if (key == KeyEvent.VK_A) left = true;
        if (key == KeyEvent.VK_D) right = true;

        // 技能按键：空格
        if(!isTwoPlayer) {
            if (key == KeyEvent.VK_Q) {
                if (currentState == STATE_PLAYING && player1 != null) {
                    player1.useSkill(mapManager);
                }
            }
        }else{
            if (key == KeyEvent.VK_UP) up_P2 = true;
            if (key == KeyEvent.VK_DOWN) down_P2 = true;
            if (key == KeyEvent.VK_LEFT) left_P2 = true;
            if (key == KeyEvent.VK_RIGHT) right_P2 = true;

            if (key == KeyEvent.VK_Q) {
                if (currentState == STATE_PLAYING && destroyPlayer != null) {
                    destroyPlayer.useSkill(mapManager);
                }
            }
            if(key == KeyEvent.VK_SPACE) {
                if (currentState == STATE_PLAYING && generatePlayer != null) {
                    generatePlayer.useSkill(mapManager);
                }
            }
        }


    }
    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        // P1 松开
        if (key == KeyEvent.VK_W) up = false;
        if (key == KeyEvent.VK_S) down = false;
        if (key == KeyEvent.VK_A) left = false;
        if (key == KeyEvent.VK_D) right = false;

        // P2 松开
        if (key == KeyEvent.VK_UP)    up_P2 = false;
        if (key == KeyEvent.VK_DOWN)  down_P2 = false;
        if (key == KeyEvent.VK_LEFT)  left_P2 = false;
        if (key == KeyEvent.VK_RIGHT) right_P2 = false;
    }




}