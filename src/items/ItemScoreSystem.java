package items;

import core.GameInstance;

public class ItemScoreSystem {
    public static void apply(int itemType, GameInstance game) {
        switch (itemType) {
            case Item.FIRE -> game.addScore(5);
            case Item.HEART -> game.addScore(10);
            case Item.POISON -> game.addScore(-10);
        }
    }
}