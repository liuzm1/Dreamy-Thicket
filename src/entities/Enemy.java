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
import maps.CollisionCheck;

/**
 * Base enemy: grid coords + smooth pixel movement.
 * Logical cell updates after move animation to avoid pathfinding deadlocks.
 */
public abstract class Enemy {
    public int col;
    public int row;

    public int x;
    public int y;

    protected int targetX;
    protected int targetY;

    protected final int TILE_SIZE = 40;
    protected int moveSpeed = 105;

    protected boolean isMoving = false;
    protected double moveStartX;
    protected double moveStartY;
    protected double moveProgress;
    protected CollisionCheck collisionCheck;

    protected int cooldownDisplay = 0;
    protected double cooldownTimer = 0;
    private static final double COOLDOWN_TICK = 1.0;
    /** Seconds to pause in place after collision (countdown 5→1 above head). */
    private static final int COOLDOWN_SECONDS = 2;

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
        this.moveProgress = 0;
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

    /** Occupies current cell or target cell while moving. */
    public boolean occupiesOrHeadingTo(int c, int r) {
        if (col == c && row == r) return true;
        return isMoving && getTargetCol() == c && getTargetRow() == r;
    }

    /** Whether another enemy occupies or is heading to this cell. */
    public static boolean isBlockedByPeer(Enemy self, Enemy[] peers, int c, int r) {
        if (peers == null) return false;
        for (Enemy peer : peers) {
            if (peer == null || peer == self) continue;
            if (peer.occupiesOrHeadingTo(c, r)) return true;
        }
        return false;
    }

    public void startCooldown() {
        cooldownDisplay = COOLDOWN_SECONDS;
        cooldownTimer = 0;
        isMoving = false;
        moveProgress = 0;
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
        // While moving: retarget only when destination changes (smooth chase transitions)
        if (isMoving) {
            if (targetX / TILE_SIZE == nextCol && targetY / TILE_SIZE == nextRow) {
                return false;
            }
        } else if (nextCol == col && nextRow == row) {
            return false;
        }

        moveStartX = x;
        moveStartY = y;
        targetX = nextCol * TILE_SIZE;
        targetY = nextRow * TILE_SIZE;
        moveProgress = 0;
        isMoving = true;
        return true;
    }

    protected void finishMove() {
        col = targetX / TILE_SIZE;
        row = targetY / TILE_SIZE;
        x = targetX;
        y = targetY;
        isMoving = false;
        moveProgress = 0;
    }

    /** Interpolate by time-per-tile to avoid frame-rate jitter at low speed. */
    protected void updateSmoothMovement(double dt) {
        if (!isMoving) return;

        double secondsPerTile = TILE_SIZE / (double) moveSpeed;
        if (secondsPerTile < 0.05) secondsPerTile = 0.05;

        moveProgress += dt / secondsPerTile;
        if (moveProgress >= 1.0) {
            finishMove();
            return;
        }

        x = (int) Math.round(moveStartX + (targetX - moveStartX) * moveProgress);
        y = (int) Math.round(moveStartY + (targetY - moveStartY) * moveProgress);
    }
}
