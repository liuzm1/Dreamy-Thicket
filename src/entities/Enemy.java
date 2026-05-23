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
    protected int moveSpeed = 150;

    protected boolean isMoving = false;
    protected CollisionCheck collisionCheck;

    /** 受伤后倒计时显示（9→0），大于 0 时敌人静止 */
    protected int cooldownDisplay = 0;
    protected double cooldownTimer = 0;
    private static final double COOLDOWN_TICK = 1.0;

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

    protected void onReset() {
        cooldownDisplay = 0;
        cooldownTimer = 0;
    }

    public boolean isOnCooldown() {
        return cooldownDisplay > 0;
    }

    public int getCooldownDisplay() {
        return cooldownDisplay;
    }

    /** 玩家受伤后：停在原地并显示 9→0 倒计时 */
    public void startCooldown() {
        cooldownDisplay = 9;
        cooldownTimer = 0;
        isMoving = false;
        x = col * TILE_SIZE;
        y = row * TILE_SIZE;
        targetX = x;
        targetY = y;
    }

    protected void updateCooldown(double dt) {
        if (cooldownDisplay <= 0) return;
        cooldownTimer += dt;
        if (cooldownTimer >= COOLDOWN_TICK) {
            cooldownTimer = 0;
            cooldownDisplay--;
        }
    }

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
            x += moveSpeed * dt;
            if (x > targetX) x = targetX;
        } else if (x > targetX) {
            x -= moveSpeed * dt;
            if (x < targetX) x = targetX;
        }

        if (y < targetY) {
            y += moveSpeed * dt;
            if (y > targetY) y = targetY;
        } else if (y > targetY) {
            y -= moveSpeed * dt;
            if (y < targetY) y = targetY;
        }

        if (x == targetX && y == targetY) {
            isMoving = false;
        }
    }
}
