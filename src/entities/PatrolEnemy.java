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
 * 第一关敌人：固定行上水平巡逻，使用 Enemy1 精灵表动画。
 */
public class PatrolEnemy extends AnimatedSpriteEnemy {

    private final int patrolRow;
    private int moveDir;
    private Enemy[] peerEnemies = new Enemy[0];

    public PatrolEnemy(GameEngine engine, CollisionCheck collisionCheck,
                       int startCol, int patrolRow, int initialMoveDir) {
        super(collisionCheck, engine, "resource/sprites/entities/Enemy1.png");
        this.patrolRow = patrolRow;
        this.moveDir = initialMoveDir;
        reset(startCol, patrolRow);
    }

    public void setPeerEnemies(Enemy[] peerEnemies) {
        this.peerEnemies = peerEnemies != null ? peerEnemies : new Enemy[0];
    }

    @Override
    protected void onReset() {
        spriteRow = moveDir > 0 ? ROW_RIGHT : ROW_LEFT;
        animationFrame = 0;
        animationTimer = 0;
    }

    @Override
    public void update(double dt) {
        if (isOnCooldown()) {
            updateCooldown(dt);
            return;
        }
        if (!isMoving) {
            int nextCol = col + moveDir;
            if (collisionCheck.isSolid(nextCol, patrolRow)) {
                moveDir = -moveDir;
                nextCol = col + moveDir;
            }
            if (canPatrolMove(nextCol)) {
                tryMoveTo(nextCol, patrolRow);
            } else {
                moveDir = -moveDir;
            }
        }

        spriteRow = moveDir > 0 ? ROW_RIGHT : ROW_LEFT;
        updateWalkAnimation(dt);
        updateSmoothMovement(dt);
    }

    private boolean canPatrolMove(int nextCol) {
        if (collisionCheck.isSolid(nextCol, patrolRow)) return false;
        if (nextCol == col) return false;
        if (Enemy.isBlockedByPeer(this, peerEnemies, nextCol, patrolRow)) return false;
        return true;
    }
}
