package entities;

import core.GameEngine;
import maps.CollisionCheck;

/**
 * 敌人基类：网格坐标 + 平滑像素移动。
 * 逻辑格子在移动动画结束后才更新，避免寻路互相卡死。
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

    public int getTargetCol() {
        return isMoving ? targetX / TILE_SIZE : col;
    }

    public int getTargetRow() {
        return isMoving ? targetY / TILE_SIZE : row;
    }

    /** 占用当前格或正在移动到的目标格 */
    public boolean occupiesOrHeadingTo(int c, int r) {
        if (col == c && row == r) return true;
        return isMoving && getTargetCol() == c && getTargetRow() == r;
    }

    /** 该格是否被其他敌人占据或即将占据 */
    public static boolean isBlockedByPeer(Enemy self, Enemy[] peers, int c, int r) {
        if (peers == null) return false;
        for (Enemy peer : peers) {
            if (peer == null || peer == self) continue;
            if (peer.occupiesOrHeadingTo(c, r)) return true;
        }
        return false;
    }

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

    protected boolean tryMoveTo(int nextCol, int nextRow) {
        if (collisionCheck.isSolid(nextCol, nextRow)) return false;
        // 移动中：仅当目标格变化时才重定向（供追踪怪连贯换格）
        if (isMoving) {
            if (targetX / TILE_SIZE == nextCol && targetY / TILE_SIZE == nextRow) {
                return false;
            }
        } else if (nextCol == col && nextRow == row) {
            return false;
        }

        targetX = nextCol * TILE_SIZE;
        targetY = nextRow * TILE_SIZE;
        isMoving = true;
        return true;
    }

    protected void finishMove() {
        col = targetX / TILE_SIZE;
        row = targetY / TILE_SIZE;
        x = targetX;
        y = targetY;
        isMoving = false;
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

        if (Math.abs(x - targetX) < 1 && Math.abs(y - targetY) < 1) {
            finishMove();
        }
    }
}
