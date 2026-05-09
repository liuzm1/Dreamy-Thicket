package scenes;
import core.GameEngine;
import scenes.*;

public class MenuManager {
    public StartMenu startMenu;
    public LevelSelectMenu levelSelectMenu;
    public VictoryMenu victoryMenu;
    public PauseMenu pauseMenu;
    public HelpMenu helpMenu;
    public GameOverMenu gameOverMenu;


    private final int STATE_START_MENU = 0;
    private final int STATE_PLAYING = 1;
    private final int STATE_PAUSED = 2;
    private final int STATE_LEVEL_SELECT = 3;
    private final int STATE_VICTOR = 4;
    private final int STATE_GAME_OVER = 5;
    private final int STATE_HELP = 6;

    public MenuManager(GameEngine engine) {
        startMenu = new StartMenu(engine);
        levelSelectMenu = new LevelSelectMenu(engine);
        victoryMenu = new VictoryMenu(engine);
        pauseMenu = new PauseMenu(engine);
        helpMenu = new HelpMenu(engine);
        gameOverMenu = new GameOverMenu(engine);
    }

    public void drawActiveMenu(GameEngine engine, int currentState, MapManager mapManager) {

        // --- 第一层：地基 ---
        if (currentState == STATE_PLAYING || currentState == STATE_PAUSED) {
            mapManager.draw(engine);
            // 这里以后可以画：player.draw(engine);
        }

        switch (currentState) {
            case STATE_START_MENU: // STATE_START_MENU
                startMenu.draw(engine);
                break;
            case STATE_LEVEL_SELECT: // STATE_LEVEL_SELECT
                levelSelectMenu.draw(engine);
                break;
            case STATE_PAUSED: // STATE_PAUSED
                pauseMenu.draw(engine);
                break;
            case STATE_VICTOR: // STATE_VICTORY
                victoryMenu.draw(engine);
                break;
            case STATE_GAME_OVER: // STATE_GAMEOVER
                gameOverMenu.draw(engine);
                break;
            case STATE_HELP: // STATE_HELP
                helpMenu.draw(engine);
                break;
        }
    }

}
