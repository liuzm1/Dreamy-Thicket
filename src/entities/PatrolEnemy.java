package entities;

import core.GameEngine;
import maps.CollisionCheck;

/**
 * 第一关敌人：固定行上水平巡逻，使用 Enemy1 精灵表动画。
 */
public class PatrolEnemy extends AnimatedSpriteEnemy {

    private final int patrolRow;
    private int moveDir;

    public PatrolEnemy(GameEngine engine, CollisionCheck collisionCheck,
                       int startCol, int patrolRow, int initialMoveDir) {
        super(collisionCheck, engine, "resource/sprites/entities/Enemy1.png");
        this.patrolRow = patrolRow;
        this.moveDir = initialMoveDir;
        reset(startCol, patrolRow);
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
            if (!collisionCheck.isSolid(nextCol, patrolRow)) {
                tryMoveTo(nextCol, patrolRow);
            }
        }

        spriteRow = moveDir > 0 ? ROW_RIGHT : ROW_LEFT;
        updateWalkAnimation(dt);
        updateSmoothMovement(dt);
    }
}
