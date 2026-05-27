package entities;

import core.GameEngine;
import maps.CollisionCheck;

import java.awt.Color;
import java.awt.Image;

/**
 * 使用 5 行×4 列精灵表的敌人（与 Enemy1 相同布局）。
 * 行0=下，行1=左，行2=右，行3=上，行4不用。
 */
public abstract class AnimatedSpriteEnemy extends Enemy {

    protected static final int ROW_DOWN = 0;
    protected static final int ROW_LEFT = 1;
    protected static final int ROW_RIGHT = 2;
    protected static final int ROW_UP = 3;

    protected static final int FRAME_W = 24;
    protected static final int FRAME_H = 24;
    protected static final int FRAMES_PER_ROW = 4;
    protected static final int DRAW_SIZE = 40;
    protected static final double ANIMATION_SPEED = 0.12;

    protected Image spriteSheet;
    protected int spriteRow = ROW_DOWN;
    protected int animationFrame = 0;
    protected double animationTimer = 0;
    private Image[] Numbers;

    protected AnimatedSpriteEnemy(CollisionCheck collisionCheck, GameEngine engine, String spritePath) {
        super(collisionCheck);
        Numbers = new Image[10];
        for (int i = 0; i < 10; i++) {
            Numbers[i] = engine.loadImage("resource/sprites/menus/numbers/" + i + ".png");
        }
        spriteSheet = engine.loadImage(spritePath);
    }

    /**
     * 根据移动方向更新朝向行
     */
    protected void setSpriteRowFromDelta(int dx, int dy) {
        if (dx > 0) spriteRow = ROW_RIGHT;
        else if (dx < 0) spriteRow = ROW_LEFT;
        else if (dy > 0) spriteRow = ROW_DOWN;
        else if (dy < 0) spriteRow = ROW_UP;
    }

    protected void updateWalkAnimation(double dt) {
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
    }

    /**
     * 该格是否被此敌人占据或正在走向
     */
    public boolean occupiesOrHeadingTo(int c, int r) {
        return super.occupiesOrHeadingTo(c, r);
    }

    @Override
    public void draw(GameEngine engine) {
        if (spriteSheet == null) return;

        int sx = animationFrame * FRAME_W;
        int sy = spriteRow * FRAME_H;

        Image frame = engine.subImage(spriteSheet, sx, sy, FRAME_W, FRAME_H);
        if (frame != null) {
            engine.drawImage(frame, x - 11, y - 15, 52, 52);
        }


        if (isOnCooldown()) {
            // 假设你有一个变量叫 currentCooldownTime (从 5.0 递减到 0)
            // float progress = currentCooldownTime / 5.0f;
            // 这里我用你的 cooldownDisplay 简单模拟一个进度比例 (假设倒计时是 5,4,3,2,1)
            float progress = cooldownDisplay / 2.0f;
            if (progress > 1.0f) progress = 1.0f;

            int barWidth = 32;
            int barHeight = 5;
            int barX = x + 4;       // 居中在敌人头顶
            int barY = y - 12;      // 头顶上方

            // 1. 画进度条背景（暗红色或黑色）
            engine.changeColor(Color.black);
            engine.drawSolidRectangle(barX, barY, barWidth, barHeight);

            // 2. 画剩余时间的进度（浅蓝色/冰冻色）
            engine.changeColor(Color.red);
            engine.drawSolidRectangle(barX, barY, (int)(barWidth * progress), barHeight);

            // 3. 画一圈像素小边框
            engine.changeColor(Color.BLACK);
            engine.drawRectangle(barX - 1, barY - 1, barWidth + 2, barHeight + 2);
        }
    }
}
