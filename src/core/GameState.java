package core;

public enum GameState {
    START_MENU,
    PLAYING,
    PAUSED,
    LEVEL_SELECT,
    VICTOR,
    GAME_OVER,
    HELP;


    public static GameState fromInt(int stateInt) {
        switch (stateInt) {
            case 0:
                return START_MENU;
            case 1:
                return PLAYING;
            case 2:
                return PAUSED;
            case 3:
                return LEVEL_SELECT;
            case 4:
                return VICTOR;
            case 5:
                return GAME_OVER;
            case 6:
                return HELP;
            default:
                return START_MENU;
        }
    }
    public int toInt() {
        switch (this) {
            case START_MENU:   return 0;
            case PLAYING:      return 1;
            case PAUSED:       return 2;
            case LEVEL_SELECT: return 3;
            case VICTOR:       return 4;
            case GAME_OVER:    return 5;
            case HELP:         return 6;
            default:           return 0;
        }
    }


    }