/**
        * ---------------------------------------------------------------------------
        * Massey University - 159.261 Games Programming
        * Assignment 2
        * ---------------------------------------------------------------------------
        * * [Dreamy Forest]
        * * Team Members:
        * - LIU ZIMO (ID:24009362)
        * - MIAO CHONG (ID: 24008986)
        * - SUN MINGYI (ID: 24009239)
        * - ZHOU XUAN (ID: 24009035)
        * ---------------------------------------------------------------------------
 **/
package core;

import entities.*;
import maps.CollisionCheck;
import maps.MapManager;
import scenes.GameScene;
import scenes.MenuManager;
import ui.GameUI;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class GameInstance extends GameEngine {
    public int animFrame = 0;

    // ====================== Manager modules ======================
    private AudioManager audioManager;
    private InputManager inputManager;
    private EnemyManager enemyManager;
    private LevelManager levelManager;

    private final GameUI gameUI = new GameUI(this);
    private final MapManager mapManager = new MapManager(this);
    private final MenuManager menuManager = new MenuManager(this);
    private CollisionCheck collisionCheck;

    // ====================== Game state ======================
    private int score = 0;
    private float countdownTime = 90.0f;
    private int targetScore = 80;
    private boolean gameEnded = false;
    private float winDelayTimer = 0;
    private GameState currentState = GameState.START_MENU;

    // ====================== Characters ======================
    private SoloPlayer player1;
    private GeneratePlayer generatePlayer;
    private DestroyPlayer destroyPlayer;

    public boolean isTwoPlayer = false;
    private static final int MAX_LIVES = 3;
    private int sharedLives = MAX_LIVES;

    @Override
    public void init() {
        // Initialize component managers
        this.audioManager = new AudioManager(this);
        this.inputManager = new InputManager();
        this.enemyManager = new EnemyManager(this);
        this.levelManager = new LevelManager(this);

        this.collisionCheck = new CollisionCheck(mapManager);

        initPlayers();
        inputManager.initKeys();
        resetSharedLives();

        // Load first level
        levelManager.loadLevel(101, mapManager, enemyManager, collisionCheck);
        audioManager.playMenuBGM();
    }

    private void initPlayers() {
        player1 = new SoloPlayer(this, mapManager, 3, 3);
        destroyPlayer = new DestroyPlayer(this, mapManager, 3, 3);
        generatePlayer = new GeneratePlayer(this, mapManager, 12, 12);
        destroyPlayer.setOpponent(generatePlayer);
        generatePlayer.setOpponent(destroyPlayer);
    }

    public void resetSharedLives() { this.sharedLives = MAX_LIVES; }
    public int getSharedLives() { return sharedLives; }
    public MapManager getMapManager() { return mapManager; }
    public SoloPlayer getPlayer1() { return player1; }
    public DestroyPlayer getDestroyPlayer() { return destroyPlayer; }
    public GeneratePlayer getGeneratePlayer() { return generatePlayer; }
    public GameState getCurrentState() { return currentState; }
    public void setTargetScore(int targetScore) { this.targetScore = targetScore; }
    public AudioManager getAudioManager() {
        return audioManager;
    }
    public Player[] getActivePlayers() {
        if (!isTwoPlayer) return new Player[]{player1};
        return new Player[]{destroyPlayer, generatePlayer};
    }

    @Override
    public void update(double dt) {
        if (currentState == GameState.PLAYING) {
            // 1. Countdown
            countdownTime -= dt;
            if (countdownTime <= 0) {
                countdownTime = 0;
                triggerGameOver();
                return;
            }

            // 2. Player movement
            if (!isTwoPlayer) {
                if (player1 != null) {
                    player1.setEnemies(enemyManager.getAllEnemies());
                    player1.update(dt);
                    if (inputManager.up)    player1.move(0, -1, collisionCheck, null);
                    if (inputManager.down)  player1.move(0, 1, collisionCheck, null);
                    if (inputManager.left)  player1.move(-1, 0, collisionCheck, null);
                    if (inputManager.right) player1.move(1, 0, collisionCheck, null);
                }
            } else {
                if (destroyPlayer != null) {
                    destroyPlayer.update(dt);
                    if (inputManager.up)    destroyPlayer.move(0, -1, collisionCheck, generatePlayer);
                    if (inputManager.down)  destroyPlayer.move(0, 1, collisionCheck, generatePlayer);
                    if (inputManager.left)  destroyPlayer.move(-1, 0, collisionCheck, generatePlayer);
                    if (inputManager.right) destroyPlayer.move(1, 0, collisionCheck, generatePlayer);
                }
                if (generatePlayer != null) {
                    generatePlayer.setEnemies(enemyManager.getAllEnemies());
                    generatePlayer.update(dt);
                    if (inputManager.up_P2)    generatePlayer.move(0, -1, collisionCheck, destroyPlayer);
                    if (inputManager.down_P2)  generatePlayer.move(0, 1, collisionCheck, destroyPlayer);
                    if (inputManager.left_P2)  generatePlayer.move(-1, 0, collisionCheck, destroyPlayer);
                    if (inputManager.right_P2) generatePlayer.move(1, 0, collisionCheck, destroyPlayer);
                }
            }

            // 3. Enemies and collisions
            enemyManager.update(dt, getActivePlayers());
            enemyManager.checkCollisions(getActivePlayers());
            animFrame++;

            // 4. Win condition
            if (score >= targetScore) {
                winDelayTimer += dt;
                if (winDelayTimer >= 0.1f) {
                    currentState = GameState.VICTOR;
                    menuManager.switchScene(currentState.toInt());
                    audioManager.playGameWinBGM(); // Switch to victory BGM
                    winDelayTimer = 0;
                }
            }
        }
    }

    public void onPlayerHitEnemy(Player player, Enemy hitter) {
        if (audioManager != null) {
            audioManager.playHitEnemySFX();
        }
        if (isTwoPlayer) {
            if (sharedLives > 0) sharedLives--;
        } else {
            player.takeDamage();
        }
        hitter.startCooldown();

        if (!hasLivesRemaining()) {
            triggerGameOver();
        }
    }

    private boolean hasLivesRemaining() {
        return isTwoPlayer ? (sharedLives > 0) : (player1 != null && player1.isAlive());
    }

    private void triggerGameOver() {
        gameEnded = true;
        currentState = GameState.GAME_OVER;
        menuManager.switchScene(currentState.toInt());
        audioManager.playGameOverBGM();
    }

    public void addScore(int value) { score += value; }
    public void minusScore(int value) {
        score -= value; if (score < 0) {
        score = 0;
    }}
    public int getScore() { return score; }

    public void resetScoreAndTime() {
        score = 0;
        countdownTime = 90.0f;
        gameEnded = false;
        winDelayTimer = 0;
        resetSharedLives();
        if (player1 != null) {
            // Ensure solo HP is full (resetPlayersToSpawn handles this; extra guard)
        }
    }

    @Override
    public void paintComponent() {
        changeColor(Color.BLACK);
        int WINDOW_SIZE = 640;
        drawSolidRectangle(0, 0, WINDOW_SIZE, WINDOW_SIZE);

        menuManager.drawActiveMenu(this, currentState.toInt(), mapManager);

        if (currentState == GameState.PLAYING) {
            if (!isTwoPlayer) {
                if (player1 != null) player1.draw(this);
            } else {
                if (destroyPlayer != null) destroyPlayer.draw(this);
                if (generatePlayer != null) generatePlayer.draw(this);
            }
            enemyManager.draw();
        }

        if (currentState == GameState.PLAYING || currentState == GameState.PAUSED || currentState == GameState.GAME_OVER) {
            double seconds = (int) countdownTime;
            if (!isTwoPlayer) {
                gameUI.draw(this, player1.getHp(), score, seconds, targetScore, false);
            } else {
                gameUI.draw(this, sharedLives, score, seconds, targetScore, true);
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int mx = e.getX();
        int my = e.getY();

        GameScene currentScene = (currentState == GameState.PLAYING) ? menuManager.inGameUIButton : getActiveMenu();
        if (currentScene == null) return;

        int nextStateInt = currentScene.handleMouseClick(mx, my);
        if (currentState == GameState.START_MENU) {
            isTwoPlayer = menuManager.startMenu.getIsTwoPlayer();
        }
        if (nextStateInt == -1) return;

        // Victory screen buttons
        if (currentState == GameState.VICTOR) {
            if (nextStateInt == 1) { // Replay level
                resetScoreAndTime();
                levelManager.loadLevel(levelManager.getCurrentLevel(), mapManager, enemyManager, collisionCheck);
                resetPlayersToSpawn();
                enterPlayingState();
            } else if (nextStateInt == 2) { // Next level
                resetScoreAndTime();
                levelManager.nextLevel(mapManager, enemyManager, collisionCheck);
                resetPlayersToSpawn();
                enterPlayingState();
            } else if (nextStateInt == 0) { // Main menu
                backToMainMenu();
            }
            return;
        }

        // Load level from level select or game over
        if (nextStateInt >= 100) {
            resetScoreAndTime();
            if (isTwoPlayer) resetSharedLives();
            levelManager.loadLevel(nextStateInt, mapManager, enemyManager, collisionCheck);
            resetPlayersToSpawn();
            enterPlayingState();
        }
        // In-game pause: restart level
        else if (nextStateInt == GameState.PLAYING.toInt() && currentState == GameState.PLAYING) {
            resetScoreAndTime();
            levelManager.loadLevel(levelManager.getCurrentLevel(), mapManager, enemyManager, collisionCheck);
            resetPlayersToSpawn();
            audioManager.playGameplayBGM();
        }
        // Resume from pause
        else if (nextStateInt == GameState.PLAYING.toInt() && currentState != GameState.PLAYING) {
            enterPlayingState();
        }
        // Normal menu navigation
        else if (nextStateInt != currentState.toInt()) {
            GameState targetState = GameState.fromInt(nextStateInt);
            if (targetState == GameState.START_MENU) {
                backToMainMenu();
            } else {
                if (targetState == GameState.LEVEL_SELECT || targetState == GameState.HELP || targetState == GameState.PAUSED) {
                    audioManager.playMenuBGM();
                }
                currentState = targetState;
                menuManager.switchScene(nextStateInt);
            }
        }
    }

    private void enterPlayingState() {
        currentState = GameState.PLAYING;
        menuManager.switchScene(currentState.toInt());
        audioManager.playGameplayBGM();
    }

    private void backToMainMenu() {
        resetScoreAndTime();
        resetPlayersToSpawn();
        resetSharedLives();
        currentState = GameState.START_MENU;
        menuManager.switchScene(currentState.toInt());
        audioManager.playMenuBGM();
    }

    private void resetPlayersToSpawn() {
        if (player1 != null) player1.reset(3, 3);
        if (destroyPlayer != null) destroyPlayer.reset(3, 3);
        if (generatePlayer != null) generatePlayer.reset(12, 12);
    }

    private GameScene getActiveMenu() {
        return switch (currentState) {
            case START_MENU -> menuManager.startMenu;
            case PAUSED -> menuManager.pauseMenu;
            case LEVEL_SELECT -> menuManager.levelSelectMenu;
            case VICTOR -> menuManager.victoryMenu;
            case GAME_OVER -> menuManager.gameOverMenu;
            case HELP -> menuManager.helpMenu;
            default -> null;
        };
    }

    private int mouseX, mouseY;
    @Override
    public void mouseMoved(MouseEvent e) { mouseX = e.getX(); mouseY = e.getY(); }
    @Override
    public void mouseDragged(MouseEvent e) { mouseX = e.getX(); mouseY = e.getY(); }
    public int getMouseX() { return mouseX; }
    public int getMouseY() { return mouseY; }

    @Override
    public void keyPressed(KeyEvent e) { inputManager.handleKeyPressed(e, this, audioManager); }
    @Override
    public void keyReleased(KeyEvent e) { inputManager.handleKeyReleased(e); }
}