package entities;

import core.GameEngine;
import maps.CollisionCheck;

/**
 * 敌人基类：网格坐标 + 平滑像素移动。
 */
public abstract class Enemy {
    public int col;
    public int row;

    public int x;
    public int y;

    protected int targetX;
    protected int targetY;

    protected final int TILE_SIZE = 40;
    protected final int MOVE_SPEED = 150;

    protected boolean isMoving = false;
    protected CollisionCheck collisionCheck;

    protected Enemy(CollisionCheck collisionCheck) {
        this.collisionCheck = collisionCheck;
    }

    public void reset(int startCol, int startRow) {
        this.col = startCol;
        this.row = startRow;
        this.x = startCol * TILE_SIZE;
        this.y = startRow * TILE_SIZE;
        this.targetX = x;
        this.targetY = y;
        this.isMoving = false;
        onReset();
    }

    protected void onReset() {}

    public abstract void update(double dt);

    public abstract void draw(GameEngine engine);

    protected void tryMoveTo(int nextCol, int nextRow) {
        if (collisionCheck.isSolid(nextCol, nextRow)) return;

        col = nextCol;
        row = nextRow;
        targetX = col * TILE_SIZE;
        targetY = row * TILE_SIZE;
        isMoving = true;
    }

    protected void updateSmoothMovement(double dt) {
        if (!isMoving) return;

        if (x < targetX) {
            x += MOVE_SPEED * dt;
            if (x > targetX) x = targetX;
        } else if (x > targetX) {
            x -= MOVE_SPEED * dt;
            if (x < targetX) x = targetX;
        }

        if (y < targetY) {
            y += MOVE_SPEED * dt;
            if (y > targetY) y = targetY;
        } else if (y > targetY) {
            y -= MOVE_SPEED * dt;
            if (y < targetY) y = targetY;
        }

        if (x == targetX && y == targetY) {
            isMoving = false;
        }
    }
}
