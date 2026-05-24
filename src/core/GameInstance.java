package core;
//游戏主类
import entities.*;
import maps.CollisionCheck;
import scenes.GameScene;
import maps.MapManager;
import scenes.MenuManager;
import ui.GameUI;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class GameInstance extends GameEngine {
    public int animFrame = 0;
    //定义窗口常量
    private final int WINDOW_SIZE = 640;

    //----UI----
    private GameUI gameUI = new GameUI(this);

    // ====================== 分数 & 倒计时 ======================
    private int score = 0;
    private float countdownTime = 60.0f; // 每局60秒
    private int targetScore = 80;// 默认第一关目标80分
    private boolean gameEnded = false;
    private float winDelayTimer = 0;//胜利延时计时器
    // ===========================================================

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
    // Music and sfx
    //-------------------------------------------------------
    private AudioClip bgm_GamePlaying ;
    private AudioClip bgm_Menu;
    private AudioClip bgm_GameOver;
    private AudioClip bgm_GameWin;
    private AudioClip currentMusic = null;

    private AudioClip sfx_UseSkill;

    private void initMusic(){
        bgm_Menu = loadAudio("resource/music/bgm_menu.wav");
        bgm_GameOver = loadAudio("resource/music/sfx_game_over.wav");
        bgm_GameWin = loadAudio("resource/music/sfx_game_win.wav");
        bgm_GamePlaying = loadAudio("resource/music/bgm_gameplay.wav");
        sfx_UseSkill = loadAudio("resource/sfx/magic.wav");
    }

    private void switchBGM(AudioClip newBGM){
        if (currentMusic == newBGM) return; // 如果已经是这首歌了，别重复播

        if (currentMusic != null) {
            stopAudioLoop(currentMusic); // 停止上一首
        }

        if (newBGM != null) {
            startAudioLoop(newBGM,-10f); // 循环播放新 BGM
        }
        currentMusic = newBGM;
    }

    public void playSkillSFX(AudioClip clip, float volumeDb){
        if(clip != null){
            playAudio(clip, volumeDb);
        }
    }

    //-------------------------------------------------------
    // Players
    //-------------------------------------------------------
    private SoloPlayer player1;
    private GeneratePlayer generatePlayer;
    private DestroyPlayer destroyPlayer;
    //双人游戏状态
    public boolean isTwoPlayer = false;
    /** 双人模式共用生命（总共 3 条） */
    private static final int MAX_LIVES = 3;
    private int sharedLives = MAX_LIVES;

    //玩家初始化
    private void initPlayers(){
        player1 = new SoloPlayer(this,mapManager, 5, 5); // 从 (5,5) 开始
        destroyPlayer = new DestroyPlayer(this,mapManager, 5, 5);
        generatePlayer = new GeneratePlayer(this,mapManager, 10, 10);
        destroyPlayer.setOpponent(generatePlayer);
        generatePlayer.setOpponent(destroyPlayer);
    }
    //-------------------------------------------------------
    // Enemies
    //-------------------------------------------------------
    private PatrolEnemy[] patrolEnemies;
    private ChaseEnemy chaseEnemy;
    private VineDestroyerEnemy vineDestroyer;
    private static final int MIDDLE_PATROL_ROW = 7;
    private static final int MIDDLE_PATROL_COL = 7;

    private PatrolEnemy createMiddlePatrolEnemy() {
        return new PatrolEnemy(this, collisionCheck, MIDDLE_PATROL_COL, MIDDLE_PATROL_ROW, 1);
    }

    private void setupEnemiesForLevel(int levelNum) {
        patrolEnemies = null;
        chaseEnemy = null;
        vineDestroyer = null;

        if (levelNum == 1) {
            patrolEnemies = new PatrolEnemy[]{
                    new PatrolEnemy(this, collisionCheck, 3, 3, -1),
                    new PatrolEnemy(this, collisionCheck, 12, 12, 1)
            };
        } else if (levelNum == 2) {
            patrolEnemies = new PatrolEnemy[]{
                    new PatrolEnemy(this,collisionCheck,13,2,-1),
                    new PatrolEnemy(this,collisionCheck,2,13,1)};
            chaseEnemy = new ChaseEnemy(this, collisionCheck, 13, 7);
        } else if (levelNum == 3) {
            chaseEnemy = new ChaseEnemy(this, collisionCheck, 3, 10);
            vineDestroyer = new VineDestroyerEnemy(this, collisionCheck, mapManager, 12, 5);
        }
    }

    private void resetSharedLives() {
        sharedLives = MAX_LIVES;
    }

    private Player[] getActivePlayers() {
        if (!isTwoPlayer) {
            return new Player[]{player1};
        }
        return new Player[]{destroyPlayer, generatePlayer};
    }

    private Enemy[] getAllEnemies() {
        java.util.ArrayList<Enemy> list = new java.util.ArrayList<>();
        if (patrolEnemies != null) {
            for (PatrolEnemy enemy : patrolEnemies) {
                if (enemy != null) list.add(enemy);
            }
        }
        if (chaseEnemy != null) list.add(chaseEnemy);
        if (vineDestroyer != null) list.add(vineDestroyer);
        return list.toArray(new Enemy[0]);
    }

    private void syncEnemyPeers() {
        Enemy[] all = getAllEnemies();
        if (patrolEnemies != null) {
            for (PatrolEnemy enemy : patrolEnemies) {
                if (enemy != null) enemy.setPeerEnemies(all);
            }
        }
        if (chaseEnemy != null) {
            chaseEnemy.setPeerEnemies(all);
        }
        if (vineDestroyer != null) {
            vineDestroyer.setPeerEnemies(all);
        }
    }

    private void updateEnemies(double dt) {
        syncEnemyPeers();
        if (patrolEnemies != null) {
            for (PatrolEnemy enemy : patrolEnemies) {
                enemy.update(dt);
            }
        }
        if (chaseEnemy != null) {
            chaseEnemy.update(dt, getActivePlayers());
        }
        if (vineDestroyer != null) {
            vineDestroyer.update(dt);
        }
    }

    private void drawEnemies() {
        if (patrolEnemies != null) {
            for (PatrolEnemy enemy : patrolEnemies) {
                enemy.draw(this);
            }
        }
        if (chaseEnemy != null) {
            chaseEnemy.draw(this);
        }
        if (vineDestroyer != null) {
            vineDestroyer.draw(this);
        }
    }

    private void checkEnemyPlayerCollisions() {
        for (Player player : getActivePlayers()) {
            if (player == null) continue;
            if (!isTwoPlayer && !player.isAlive()) continue;
            if (isTwoPlayer && sharedLives <= 0) continue;

            if (patrolEnemies != null) {
                for (PatrolEnemy enemy : patrolEnemies) {
                    if (enemy != null && !enemy.isOnCooldown() && isSameGrid(enemy, player)) {
                        onPlayerHitEnemy(player, enemy);
                        return;
                    }
                }
            }
            if (chaseEnemy != null && !chaseEnemy.isOnCooldown() && isSameGrid(chaseEnemy, player)) {
                onPlayerHitEnemy(player, chaseEnemy);
                return;
            }
            if (vineDestroyer != null && !vineDestroyer.isOnCooldown() && isSameGrid(vineDestroyer, player)) {
                onPlayerHitEnemy(player, vineDestroyer);
                return;
            }
        }
    }

    private boolean isSameGrid(Enemy enemy, Player player) {
        return enemy.col == player.col && enemy.row == player.row;
    }

    private void onPlayerHitEnemy(Player player, Enemy hitter) {
        if (isTwoPlayer) {
            if (sharedLives > 0) sharedLives--;
        } else {
            player.takeDamage();
        }
        hitter.startCooldown();

        if (!hasLivesRemaining()) {
            triggerGameOver(); // 抽取出来统一处理，防止逻辑混乱
        }
    }

    private boolean hasLivesRemaining() {
        if (isTwoPlayer) {
            return sharedLives > 0;
        }
        return player1 != null && player1.isAlive();
    }


    //-------------------------------------------------------
    // keys
    //-------------------------------------------------------
    private boolean left, right, up, down;
    private boolean left_P2, right_P2, up_P2, down_P2;

    private void initKeys(){
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
        initPlayers();
        initKeys();
        initMusic();
        switchBGM(bgm_Menu);
        resetSharedLives();
        setupEnemiesForLevel(1);
    }

    @Override
    public void update(double dt) {
        if (currentState == STATE_PLAYING) {
            // ---------------- 倒计时 ----------------
            countdownTime -= dt;
            if (countdownTime <= 0) {
                countdownTime = 0;
                triggerGameOver();
                return; // 结束更新，防止跑后面的逻辑
            }

            // ---------------- 玩家移动 ----------------
            if(!isTwoPlayer) {
                if(player1 != null) {
                    player1.setEnemies(getAllEnemies());
                    player1.update(dt);
                    if (up) player1.move(0, -1, collisionCheck,null);
                    if (down) player1.move(0, 1, collisionCheck,null);
                    if (left) player1.move(-1, 0, collisionCheck,null);
                    if (right) player1.move(1, 0, collisionCheck,null);
                }
            } else {
                if(destroyPlayer != null) {
                    destroyPlayer.update(dt);
                    if (up) destroyPlayer.move(0, -1, collisionCheck,generatePlayer);
                    if (down) destroyPlayer.move(0, 1, collisionCheck,generatePlayer);
                    if (left) destroyPlayer.move(-1, 0, collisionCheck,generatePlayer);
                    if (right) destroyPlayer.move(1, 0, collisionCheck,generatePlayer);
                }
                if(generatePlayer != null) {
                    generatePlayer.setEnemies(getAllEnemies());
                    generatePlayer.update(dt);
                    if (up_P2) generatePlayer.move(0, -1, collisionCheck,destroyPlayer);
                    if (down_P2) generatePlayer.move(0, 1, collisionCheck,destroyPlayer);
                    if (left_P2) generatePlayer.move(-1, 0, collisionCheck,destroyPlayer);
                    if (right_P2) generatePlayer.move(1, 0, collisionCheck,destroyPlayer);
                }
            }
            updateEnemies(dt);
            checkEnemyPlayerCollisions();
            animFrame++;

            // ======================== 胜利延迟检测 ========================
            if (score >= targetScore) {
                winDelayTimer += dt;
                if (winDelayTimer >= 0.1f) {  // 0.4秒后跳转
                    currentState = STATE_VICTOR;
                    menuManager.switchScene(STATE_VICTOR);
                    switchBGM(bgm_GameWin); // 触发胜利状态时立刻切歌
                    winDelayTimer = 0;
                }
            }
        }
    }

    // 统一的 Game Over 触发入口
    private void triggerGameOver() {
        gameEnded = true;
        currentState = STATE_GAME_OVER;
        menuManager.switchScene(STATE_GAME_OVER);
        switchBGM(bgm_GameOver);
    }

    public void addScore(int value) {
        score += value;
    }

    public void minusScore(int value) {
        score -= value;
    }

    public int getScore() {
        return score;
    }

    public void resetScoreAndTime() {
        score = 0;
        countdownTime = 60.0f;
        gameEnded = false;
        winDelayTimer = 0;
    }

    @Override
    public void paintComponent() {
        changeColor(Color.BLACK);
        drawSolidRectangle(0, 0, 640, 640);

        menuManager.drawActiveMenu(this, currentState, mapManager);

        if (currentState == STATE_PLAYING) {
            if(!isTwoPlayer) {
                if(player1 != null) player1.draw(this);
            }else{
                if(destroyPlayer != null) destroyPlayer.draw(this);
                if(generatePlayer != null) generatePlayer.draw(this);
            }
            drawEnemies();
            //drawDebugGrid();

//            int mx = getMouseX();
//            int my = getMouseY();
//            changeColor(Color.YELLOW);
//            drawBoldText(10, 625, "(" + (mx/40) + " , " + (my/40 ) + ")");
        }
        if (currentState == STATE_PLAYING || currentState == STATE_PAUSED || currentState == STATE_GAME_OVER) {
            double seconds = (int) countdownTime;
            if(!isTwoPlayer) {
            gameUI.draw(this, player1.getHp(), score, seconds, targetScore, isTwoPlayer);
            }else{
                gameUI.draw(this, sharedLives, score, seconds, targetScore, true);
            }
        }
    }

//    private void drawDebugGrid() {
//        changeColor(Color.white);
//        for (int i = 0; i <= WINDOW_SIZE; i += 40) {
//            drawLine(i, 0, i, WINDOW_SIZE);
//            drawLine(0, i, WINDOW_SIZE, i);
//        }
//    }

    private int mouseX, mouseY;
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

    private final int STATE_START_MENU = 0;
    private final int STATE_PLAYING = 1;
    private final int STATE_PAUSED = 2;
    private final int STATE_LEVEL_SELECT = 3;
    private final int STATE_VICTOR = 4;
    private final int STATE_GAME_OVER = 5;
    private final int STATE_HELP = 6;
    private int currentState = STATE_START_MENU;

    @Override
    public void mousePressed(MouseEvent e) {
        int mx = e.getX();
        int my = e.getY();

        GameScene currentScene = (currentState == STATE_PLAYING) ? menuManager.inGameUIButton : getActiveMenu();
        if (currentScene == null) return;

        int nextState = currentScene.handleMouseClick(mx, my);
        if(currentState == STATE_START_MENU) {
            isTwoPlayer = menuManager.startMenu.getIsTwoPlayer();
        }
        if (nextState == -1) return;


        // ==============================================
        // 处理胜利界面按钮（修复：切歌逻辑缺失）
        // ==============================================
        if (currentState == STATE_VICTOR) {
            // 1 = 重新玩当前关卡
            if (nextState == 1) {
                resetScoreAndTime();
                int levelNum = currentLevel - 100;
                mapManager.loadLevel("resource/map" + levelNum + ".txt");
                if(!isTwoPlayer) {
                    if (player1 != null) player1.reset(5, 5);
                }else{
                    if(destroyPlayer != null) destroyPlayer.reset(5, 5);
                    if(generatePlayer != null) generatePlayer.reset(10, 10);
                    resetSharedLives();
                }
                setupEnemiesForLevel(levelNum);
                currentState = STATE_PLAYING;
                menuManager.switchScene(STATE_PLAYING);
                switchBGM(bgm_GamePlaying); // 修复：重开当前关卡切回游戏音乐
            }
            // 2 = 下一关
            else if (nextState == 2) {
                resetScoreAndTime();
                int levelNum = currentLevel - 100;
                levelNum++;
                if (levelNum > 3) levelNum = 1;
                currentLevel = 100 + levelNum;

                mapManager.loadLevel("resource/map" + levelNum + ".txt");
                if (levelNum == 1) targetScore = 80;
                else if (levelNum == 2) targetScore = 120;
                else if (levelNum == 3) targetScore = 160;

                if(!isTwoPlayer) {
                    if (player1 != null) player1.reset(5, 5);
                }else{
                    if(destroyPlayer != null) destroyPlayer.reset(5, 5);
                    if(generatePlayer != null) generatePlayer.reset(10, 10);
                    resetSharedLives();
                }
                setupEnemiesForLevel(levelNum);
                currentState = STATE_PLAYING;
                menuManager.switchScene(STATE_PLAYING);
                switchBGM(bgm_GamePlaying); // 修复：进入下一关切回游戏音乐
            }
            // 0 = 返回主菜单
            else if (nextState == 0) {
                resetScoreAndTime();
                if (isTwoPlayer) resetSharedLives();
                currentState = STATE_START_MENU;
                menuManager.switchScene(STATE_START_MENU);
                switchBGM(bgm_Menu); // 修复：返回主菜单切回菜单音乐
            }
            return;
        }

        // --- A. 进入新关卡 (从关卡选择界面/或者GameOver点进来) ---
        if (nextState >= 100) {
            resetScoreAndTime();
            currentLevel = nextState;
            int levelNum = nextState - 100;

            // 修复：无论从哪个状态进新关卡，只要是双人模式就应该无条件重置生命
            if (isTwoPlayer) {
                resetSharedLives();
            }

            mapManager.loadLevel("resource/map" + levelNum + ".txt");
            if (levelNum == 1) targetScore = 80;
            else if (levelNum == 2) targetScore = 120;
            else if (levelNum == 3) targetScore = 160;

            if(!isTwoPlayer) {
                if (player1 != null) player1.reset(5, 5);
            }else{
                if(destroyPlayer != null) destroyPlayer.reset(5, 5);
                if(generatePlayer != null) generatePlayer.reset(10, 10);
            }
            setupEnemiesForLevel(levelNum);

            currentState = STATE_PLAYING;
            menuManager.switchScene(STATE_PLAYING);
            switchBGM(bgm_GamePlaying); // 确保切歌
        }

        // --- B. 重新开始当前关卡 (在游戏内Pause或特定界面点击重置) ---
        else if (nextState == STATE_PLAYING && currentState == STATE_PLAYING) {
            resetScoreAndTime();
            int levelNum = currentLevel - 100;

            mapManager.loadLevel("resource/map" + levelNum + ".txt");
            if(!isTwoPlayer) {
                if (player1 != null) player1.reset(5, 5);
            }else{
                if(destroyPlayer != null) destroyPlayer.reset(5, 5);
                if(generatePlayer != null) generatePlayer.reset(10, 10);
                resetSharedLives();
            }
            setupEnemiesForLevel(levelNum);
            switchBGM(bgm_GamePlaying);
        }

        // --- C. 从菜单/暂停返回游戏 ---
        else if (nextState == STATE_PLAYING && currentState != STATE_PLAYING) {
            switchBGM(bgm_GamePlaying);
            currentState = STATE_PLAYING;
            menuManager.switchScene(STATE_PLAYING);
            return;
        }

        // --- D. 普通页面切换 (主菜单、帮助、暂停等) ---
        else if (nextState != currentState) {
            if (nextState == STATE_START_MENU) {
                resetScoreAndTime();
                if (player1 != null) player1.reset(5, 5);
                if (destroyPlayer != null) destroyPlayer.reset(5, 5);
                if (generatePlayer != null) generatePlayer.reset(10, 10);
                resetSharedLives();
            }

            if (nextState == STATE_START_MENU || nextState == STATE_LEVEL_SELECT || nextState == STATE_HELP || nextState == STATE_PAUSED) {
                switchBGM(bgm_Menu);
            }

            currentState = nextState;
            menuManager.switchScene(nextState);
        }
    }

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

    public int getMouseX() { return mouseX; }
    public int getMouseY() { return mouseY; }

    @Override
    public void keyPressed(java.awt.event.KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_W) up = true;
        if (key == KeyEvent.VK_S) down = true;
        if (key == KeyEvent.VK_A) left = true;
        if (key == KeyEvent.VK_D) right = true;

        if(!isTwoPlayer) {
            if (key == KeyEvent.VK_Q) {
                if (currentState == STATE_PLAYING && player1 != null) {
                    player1.useSkill(mapManager);
                    playSkillSFX(sfx_UseSkill,-10f);
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
                    playSkillSFX(sfx_UseSkill,-10f);
                }
            }
            if(key == KeyEvent.VK_SPACE) {
                if (currentState == STATE_PLAYING && generatePlayer != null) {
                    generatePlayer.useSkill(mapManager);
                    playSkillSFX(sfx_UseSkill,-10f);
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_W) up = false;
        if (key == KeyEvent.VK_S) down = false;
        if (key == KeyEvent.VK_A) left = false;
        if (key == KeyEvent.VK_D) right = false;

        if (key == KeyEvent.VK_UP)    up_P2 = false;
        if (key == KeyEvent.VK_DOWN)  down_P2 = false;
        if (key == KeyEvent.VK_LEFT)  left_P2 = false;
        if (key == KeyEvent.VK_RIGHT) right_P2 = false;
    }
}