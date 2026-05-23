package entities;

import core.GameEngine;
import maps.CollisionCheck;

import java.awt.Image;

/**
 * 第一关敌人：固定行上水平巡逻，使用 Enemy1 精灵表动画。
 * 图集 5 行×4 列（每帧 24×24），第 5 行不用：
 * 行0=朝下，行1=朝左，行2=朝右，行3=朝上。
 */
public class PatrolEnemy extends Enemy {

    private static final int ROW_DOWN = 0;
    private static final int ROW_LEFT = 1;
    private static final int ROW_RIGHT = 2;
    private static final int ROW_UP = 3;

    private static final int FRAME_W = 24;
    private static final int FRAME_H = 24;
    private static final int FRAMES_PER_ROW = 4;
    private static final int DRAW_SIZE = 40;

    private final int patrolRow;
    private int moveDir;

    private Image spriteSheet;
    private int spriteRow = ROW_LEFT;
    private int animationFrame = 0;
    private double animationTimer = 0;
    private static final double ANIMATION_SPEED = 0.12;

    public PatrolEnemy(GameEngine engine, CollisionCheck collisionCheck,
                       int startCol, int patrolRow, int initialMoveDir) {
        super(collisionCheck);
        this.patrolRow = patrolRow;
        this.moveDir = initialMoveDir;
        spriteSheet = engine.loadImage("resource/sprites/entities/Enemy1.png");
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

        if (isMoving) {
            animationTimer += dt;
            if (animationTimer >= ANIMATION_SPEED) {
                animationFrame = (animationFrame + 1) % FRAMES_PER_ROW;
                animationTimer = 0;
            }
        } else {
            animationFrame = 0;
            animationTimer = 0;
        }

        updateSmoothMovement(dt);
    }

    @Override
    public void draw(GameEngine engine) {
        if (spriteSheet == null) return;

        int sx = animationFrame * FRAME_W;
        int sy = spriteRow * FRAME_H;

        Image frame = engine.subImage(spriteSheet, sx, sy, FRAME_W, FRAME_H);
        if (frame != null) {
            engine.drawImage(frame, x, y, DRAW_SIZE, DRAW_SIZE);
        }
    }
}
