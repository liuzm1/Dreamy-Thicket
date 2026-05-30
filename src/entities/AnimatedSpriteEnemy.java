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
 * Enemy using a 5×4 sprite sheet (same layout as Enemy1).
 * Row 0=down, row 1=left, row 2=right, row 3=up, row 4 unused.
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
    private Image stunIcon; // Stun state icon

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
     * Update sprite row from movement direction.
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
     * Whether this enemy occupies or is heading to the given cell.
     */
    public boolean occupiesOrHeadingTo(int c, int r) {
        return super.occupiesOrHeadingTo(c, r);
    }

    @Override
    public void draw(GameEngine engine) {
        if (spriteSheet == null) return;

        // 1. Draw normal walk/slide pixel animation
        int sx = animationFrame * FRAME_W;
        int sy = spriteRow * FRAME_H;

        Image frame = engine.subImage(spriteSheet, sx, sy, FRAME_W, FRAME_H);
        if (frame != null) {
            engine.drawImage(frame, x - 11, y - 15, 52, 52);
        }

        // 2. While on cooldown/stunned, render 16×16 effect icon centered above head
        if (isOnCooldown() && stunIcon != null) {
            // Center: x + 12 aligns 16px-wide icon on 40px DRAW_SIZE sprite
            // y - 25 floats icon above the enemy head
            int iconX = x + 12;
            int iconY = y - 25;

            // Draw 16×16 retro stun icon (scaled to 32×32 on screen)
            engine.drawImage(stunIcon, iconX-10, iconY-5, 32, 32);
        }
    }
}
