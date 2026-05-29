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
package entities;

import core.GameEngine;
import core.GameInstance;
import maps.CollisionCheck;
import maps.MapManager;

import java.awt.*;
import java.awt.Image;

public abstract class Player {
    // Grid position (logic)
    public int col;
    public int row;

    protected MapManager mapManager;
    protected GameEngine game; // Set here

    // Screen pixel position (rendering)
    public int x;
    public int y;

    // Target pixel position (smooth move destination)
    protected int targetX;
    protected int targetY;

    protected final int TILE_SIZE = 40;
    protected int hp = 3;

    // Facing direction
    public static final int DIR_UP = 3;
    public static final int DIR_DOWN = 0;
    public static final int DIR_LEFT = 1;
    public static final int DIR_RIGHT = 2;
    protected int direction = DIR_DOWN;

    // Sprite frames
    protected final int FRAME_SIZE = 32;
    public Image spriteSheet;

    protected int animationFrame = 0;
    protected double animationTimer = 0;
    protected final double ANIMATION_SPEED = 0.15; // Slightly faster for smoother walk

    // Vines
    protected boolean isCasting;
    protected boolean isClearing; // Clearing mode flag

    protected double castTimer = 0;
    protected int remainingVineGrids;
    protected int currentCastCol, currentCastRow;
    protected final double GROW_INTERVAL = 0.1; // One tile every 0.1s

    // Movement state
    protected boolean isMoving = false;

    // Move speed (pixels per second)
    protected final int MOVE_SPEED = 200;

    // Opponent player
    protected Player opponent;

    /** Enemies for vine growth occupancy checks (injected each frame by GameInstance). */
    protected Enemy[] enemies = new Enemy[0];

    public Player(GameEngine engine, MapManager mapManager, int startCol, int startRow) {
        // Required field assignments
        this.game = engine;
        this.mapManager = mapManager;
        this.col = startCol;
        this.row = startRow;

        // Initialize coordinates
        this.x = col * TILE_SIZE;
        this.y = row * TILE_SIZE;
        this.targetX = x;
        this.targetY = y;

    }

    public void setOpponent(Player opponent) {
        this.opponent = opponent;
    }

    public void setEnemies(Enemy[] enemies) {
        this.enemies = enemies != null ? enemies : new Enemy[0];
    }

    // Skill
    public abstract void useSkill(MapManager mapManager);

    // Damage
    public void takeDamage() {
        if (hp > 0) {
            this.hp--;
        }
    }

    public int getHp() {
        return hp;
    }

    public boolean isAlive() {
        return hp > 0;
    }

    // ========================== Movement =========================
    public void move(int dx, int dy, CollisionCheck collisionCheck, Player otherPlayer) {
        // Locked while casting/clearing
        if (isCasting || isClearing) return;
        // Always update facing even if move fails
        updateDirection(dx, dy);
        // Ignore new input while mid-move (prevents tap-spam teleport)
        if (isMoving) return;

        int nextCol = this.col + dx;
        int nextRow = this.row + dy;

        // 1. Map collision
        if (!canEnterTile(collisionCheck, nextCol, nextRow)) return;

        if (otherPlayer != null && otherPlayer.col == nextCol && otherPlayer.row == nextRow) {
            // Blocked by another player on that cell
            return;
        }
        // Update logical grid cell
        this.col = nextCol;
        this.row = nextRow;

        // Set smooth-move target
        targetX = col * TILE_SIZE;
        targetY = row * TILE_SIZE;

        isMoving = true; // Start moving

    }

    // ====================== Smooth movement + animation ======================
    public void update(double dt) {
        // Smooth movement
        if (isMoving) {
            // Horizontal
            if (x < targetX) {
                x += MOVE_SPEED * dt;
                if (x > targetX) x = targetX;
            } else if (x > targetX) {
                x -= MOVE_SPEED * dt;
                if (x < targetX) x = targetX;
            }

            // Vertical
            if (y < targetY) {
                y += MOVE_SPEED * dt;
                if (y > targetY) y = targetY;
            } else if (y > targetY) {
                y -= MOVE_SPEED * dt;
                if (y < targetY) y = targetY;
            }

            // Arrived at target (distance check avoids float stuck state)
            if (Math.abs(x - targetX) < 1 && Math.abs(y - targetY) < 1) {
                x = targetX;
                y = targetY;
                isMoving = false;
            }

            // Walk animation
            animationTimer += dt;
            if (animationTimer >= ANIMATION_SPEED) {
                animationFrame = (animationFrame + 1) % 3;
                animationTimer = 0;
            }
        } else {
            // Idle: hold first frame
            animationFrame = 0;
            animationTimer = 0;
        }
        checkPickup();
    }

    public void checkPickup() {
        if (game == null) return;
        int tile = mapManager.getTile(col, row);

        if (tile == 6) {
            ((GameInstance) game).getAudioManager().playItemPickSFX();
            // Ruby +5
            ((GameInstance) game).addScore(5);
            mapManager.setTile(col, row, 0);
            spawnNewRandomItem();
        } else if (tile == 7) {
            ((GameInstance) game).getAudioManager().playItemPickAddSFX();
            // Star +10
            ((GameInstance) game).addScore(10);
            mapManager.setTile(col, row, 0);
            spawnNewRandomItem();
        } else if (tile == 8) {
            ((GameInstance) game).getAudioManager().playItemPickPoison();
            double rand = Math.random();
            if(rand < 0.55){
                // Oracle -30
                if(((GameInstance) game).getScore() >= 0) {
                    ((GameInstance) game).minusScore(30);
                }
            }else{
                ((GameInstance) game).addScore(30);
            }
                mapManager.setTile(col, row, 0);
                spawnNewRandomItem();
        }
    }

    private void spawnNewRandomItem() {
        int r = 0, c = 0;
        boolean found = false;

        // Find empty tile (max 50 tries to avoid infinite loop)
        for(int i=0; i<50; i++){
            r = 2 + (int)(Math.random()*12);
            c = 2 + (int)(Math.random()*12);

            if(mapManager.getTile(r, c) == 0) {
                found = true;
                break;
            }
        }

        if(!found) return;

        // ==============================
        // Spawn weights (low poison chance)
        // ==============================
        double rand = Math.random();
        int newItem;

        if(rand < 0.94) {
            newItem = 6;
        }
        else if(rand < 0.97) {
            newItem = 7;
        }
        else {
            newItem = 8;
        }

        mapManager.setTile(r, c, newItem);


    }



    // Draw
    public void draw(GameEngine engine) {
        if (spriteSheet == null) return;

        int sx = animationFrame * FRAME_SIZE;
        int sy = direction * FRAME_SIZE;

        Image currentFrame = engine.subImage(spriteSheet, sx, sy, FRAME_SIZE, FRAME_SIZE);
        if (currentFrame != null) {
            engine.drawImage(currentFrame, x-25, y-25, 90, 90);
        }
    }

    // Reset
    public void reset(int startCol, int startRow) {
        this.col = startCol;
        this.row = startRow;
        this.x = startCol * TILE_SIZE;
        this.y = startRow * TILE_SIZE;
        this.targetX = x;
        this.targetY = y;
        this.direction = DIR_DOWN;
        this.hp = 3;
        this.isMoving = false;
        this.isCasting = false;
        this.isClearing = false;
        this.castTimer = 0;
        this.remainingVineGrids = 0;
        this.animationFrame = 0;
    }

    /** Subclasses may override: whether tile is enterable (default: walls/vines block). */
    protected boolean canEnterTile(CollisionCheck collisionCheck, int col, int row) {
        return !collisionCheck.isSolid(col, row);
    }

    /** Vines only on grass; no opponent or enemy on cell. */
    protected boolean canPlantVineAt(int c, int r) {
        if (mapManager.getTile(c, r) != 0) return false;
        if (opponent != null && opponent.col == c && opponent.row == r) return false;
        return !isOccupiedByEnemy(c, r);
    }

    protected boolean isOccupiedByEnemy(int c, int r) {
        for (Enemy enemy : enemies) {
            if (enemy == null) continue;
            if (enemy.col == c && enemy.row == r) return true;
            if (enemy.isMoving && enemy.getTargetCol() == c && enemy.getTargetRow() == r) {
                return true;
            }
        }
        return false;
    }

    // Update facing
    protected void updateDirection(int dx, int dy) {
        // Locked while casting/clearing
        if (isCasting || isClearing) return;
        if (dx > 0) direction = DIR_RIGHT;
        else if (dx < 0) direction = DIR_LEFT;
        else if (dy > 0) direction = DIR_DOWN;
        else if (dy < 0) direction = DIR_UP;
    }


}