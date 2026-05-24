package items;

import core.GameEngine;
import java.awt.Image;

public class Item {
    public static final int FIRE = 6;
    public static final int HEART = 7;
    public static final int POISON = 8;

    private final int type;
    private final int col, row;

    public Item(int type, int col, int row) {
        this.type = type;
        this.col = col;
        this.row = row;
    }

    public void draw(GameEngine engine, Image fireImg, Image heartImg, Image poisonImg, int tileSize, int animFrame) {
        int px = col * tileSize;
        int py = row * tileSize;
        float offset = (float) Math.sin(animFrame * 0.1f + type) * 3f;

        switch (type) {
            case FIRE -> engine.drawImage(fireImg, px + 4, py + 4 + offset, 39, 39);
            case HEART -> engine.drawImage(heartImg, px + 4, py + 4 + offset, 35, 35);
            case POISON -> engine.drawImage(poisonImg, px + 4, py + 4 + offset, 32, 32);
        }
    }

    public int getType() { return type; }
    public int getCol() { return col; }
    public int getRow() { return row; }
}