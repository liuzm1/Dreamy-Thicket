package scenes;
import core.GameEngine;
import maps.MapManager;
import ui.InGameUI_Button;

public class MenuManager {
    public StartMenu startMenu;
    public LevelSelectMenu levelSelectMenu;
    public VictoryMenu victoryMenu;
    public PauseMenu pauseMenu;
    public HelpMenu helpMenu;
    public GameOverMenu gameOverMenu;
    public InGameUI_Button inGameUIButton;


    public static final int STATE_START_MENU = 0;
    public static int STATE_PLAYING = 1;
    public static int STATE_PAUSED = 2;
    public static int STATE_LEVEL_SELECT = 3;
    public static int STATE_VICTOR = 4;
    public static int STATE_GAME_OVER = 5;
    public static int STATE_HELP = 6;
    public static int STATE_INGAMEUI = 7;

    private GameScene activeScene;

    public MenuManager(GameEngine engine) {
        startMenu = new StartMenu(engine);
        levelSelectMenu = new LevelSelectMenu(engine);
        victoryMenu = new VictoryMenu(engine);
        pauseMenu = new PauseMenu(engine);
        helpMenu = new HelpMenu(engine);
        gameOverMenu = new GameOverMenu(engine);
        inGameUIButton = new InGameUI_Button(engine);

        //初始状态指向主菜单
        activeScene = startMenu;
    }

    public void switchScene(int state) {
        switch(state) {
            case 0: activeScene = startMenu; break;
            case 1: activeScene = inGameUIButton; break;
            case 2: activeScene = pauseMenu; break;
            case 3: activeScene = levelSelectMenu; break;
            case 4: activeScene = victoryMenu; break;
            case 5: activeScene = gameOverMenu; break;
            case 6: activeScene = helpMenu; break;
        }
    }

    public void drawActiveMenu(GameEngine engine, int currentState, MapManager mapManager) {
        // 1. 画地基
        if (currentState == STATE_PLAYING || currentState == STATE_PAUSED ||
                currentState == STATE_VICTOR || currentState == STATE_GAME_OVER) {
            mapManager.draw(engine);
        }

        // 2. 特殊处理：如果是游戏中，额外画一层 InGameUI
        if (currentState == STATE_PLAYING) {
            inGameUIButton.draw(engine); // 这样它就不用非得挤在 activeScene 变量里了
        }


        // 2. 再画菜单。因为 activeScene.draw 在后面，它会覆盖在地图上
        if (activeScene != null) {
            activeScene.draw(engine);
        }
    }

}
