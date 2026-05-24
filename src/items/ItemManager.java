package items;

import core.GameEngine;
import core.GameInstance;
import maps.MapManager;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ItemManager {
    private final List<Item> items = new ArrayList<>();
    private final MapManager map;

    public ItemManager(MapManager map) {
        this.map = map;
    }

    // 开局生成道具
    public void generateAllItems() {
        items.clear();
        int count = 0;
        while (count < 10) {
            int r = 2 + (int) (Math.random() * 12);
            int c = 2 + (int) (Math.random() * 12);
            if (map.getTile(c, r) == 0) {
                double rand = Math.random();
                int type;
                if (rand < 0.82) type = Item.FIRE;
                else if (rand < 0.97) type = Item.HEART;
                else type = Item.POISON;

                map.setTile(c, r, type);
                items.add(new Item(type, c, r));
                count++;
            }
        }
    }

    // 玩家踩到物品
    public void checkPickup(int playerCol, int playerRow, GameInstance game) {
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            if (item.getCol() == playerCol && item.getRow() == playerRow) {
                // 加分/减分
                ItemScoreSystem.apply(item.getType(), game);
                // 移除
                map.setTile(item.getCol(), item.getRow(), 0);
                items.remove(i);
                // 立刻生成新物品
                spawnNewRandomItem();
                return;
            }
        }
    }

    // 永远保持地图有物品
    private void spawnNewRandomItem() {
        while (true) {
            int r = 2 + (int) (Math.random() * 12);
            int c = 2 + (int) (Math.random() * 12);
            if (map.getTile(c, r) == 0) {
                double rand = Math.random();
                int type;
                if (rand < 0.82) type = Item.FIRE;
                else if (rand < 0.97) type = Item.HEART;
                else type = Item.POISON;

                map.setTile(c, r, type);
                items.add(new Item(type, c, r));
                break;
            }
        }
    }

    public void drawAll(GameEngine engine, Image fire, Image heart, Image poison, int tileSize, int anim) {
        for (Item item : items) {
            item.draw(engine, fire, heart, poison, tileSize, anim);
        }
    }

    public void clear() {
        items.clear();
    }
}