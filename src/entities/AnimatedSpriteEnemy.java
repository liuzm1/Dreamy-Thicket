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
    private Image stunIcon; // 新增：晕眩状态贴图

    protected AnimatedSpriteEnemy(CollisionCheck collisionCheck, GameEngine engine, String spritePath) {
        super(collisionCheck);
        Numbers = new Image[10];
        for (int i = 0; i < 10; i++) {
            Numbers[i] = engine.loadImage("resource/sprites/menus/numbers/" + i + ".png");
        }
        stunIcon = engine.loadImage("resource/sprites/entities/stunIcon.png");
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

        // 1. 正常绘制怪物原本的走路/平移像素动画
        int sx = animationFrame * FRAME_W;
        int sy = spriteRow * FRAME_H;

        Image frame = engine.subImage(spriteSheet, sx, sy, FRAME_W, FRAME_H);
        if (frame != null) {
            engine.drawImage(frame, x - 11, y - 15, 52, 52);
        }

        // 2. 【核心修改】如果正在冷却/眩晕中，直接在头顶正中心渲染 16x16 的特效贴图
        if (isOnCooldown() && stunIcon != null) {
            // 计算居中坐标：x + 12 刚好可以让 16 宽度的贴图居中在 40 DRAW_SIZE 的怪物头顶
            // y - 25 让它悬浮在怪物头顶上方的空中，充满灵动感
            int iconX = x + 12;
            int iconY = y - 25;

            // 完美渲染 16x16 的复古眩晕贴图
            engine.drawImage(stunIcon, iconX-10, iconY-5, 32, 32);
        }
    }
}
