package entities;

import core.GameEngine;
import maps.CollisionCheck;
import maps.MapManager;

import java.awt.*;
import java.awt.Image;

public abstract class Player {
    // 网格位置（逻辑）
    public int col;
    public int row;

    // 屏幕像素位置（实际绘制用）
    public int x;
    public int y;

    // 目标像素位置（平滑移动目的地）
    protected int targetX;
    protected int targetY;

    protected final int TILE_SIZE = 40;
    protected int hp = 3;

    // 方向
    public static final int DIR_UP = 3;
    public static final int DIR_DOWN = 0;
    public static final int DIR_LEFT = 1;
    public static final int DIR_RIGHT = 2;
    protected int direction = DIR_DOWN;

    // 精灵帧
    protected final int FRAME_SIZE = 32;
    public Image spriteSheet;

    protected int animationFrame = 0;
    protected double animationTimer = 0;
    protected final double ANIMATION_SPEED = 0.15; // 调快一点更流畅

    // 移动状态
    protected boolean isMoving = false;

    // 移动速度（像素/秒）
    protected final int MOVE_SPEED = 200;

    // 技能
    public abstract void useSkill(MapManager mapManager);

    // 受伤
    public void takeDamage() {
        this.hp--;
    }

    // ========================== 【移动逻辑】 =========================
    public void move(int dx, int dy, CollisionCheck collisionCheck) {
        // 正在移动时，不接受新移动指令 → 防止连点瞬移
        if (isMoving) return;

        int nextCol = this.col + dx;
        int nextRow = this.row + dy;

        if (!collisionCheck.isSolid(nextCol, nextRow)) {
            // 更新逻辑格子
            this.col = nextCol;
            this.row = nextRow;

            // 设置新的目标坐标
            targetX = col * TILE_SIZE;
            targetY = row * TILE_SIZE;

            isMoving = true; // 开始移动
        }

        // 无论是否移动成功，都更新朝向
        updateDirection(dx, dy);
    }

    // ====================== 【平滑移动 + 动画更新】 ======================
    public void update(double dt) {
        // 平滑移动
        if (isMoving) {
            // 水平移动
            if (x < targetX) {
                x += MOVE_SPEED * dt;
                if (x > targetX) x = targetX;
            } else if (x > targetX) {
                x -= MOVE_SPEED * dt;
                if (x < targetX) x = targetX;
            }

            // 垂直移动
            if (y < targetY) {
                y += MOVE_SPEED * dt;
                if (y > targetY) y = targetY;
            } else if (y > targetY) {
                y -= MOVE_SPEED * dt;
                if (y < targetY) y = targetY;
            }

            // 到达目标 → 停止移动
            if (x == targetX && y == targetY) {
                isMoving = false;
            }

            // 播放走路动画
            animationTimer += dt;
            if (animationTimer >= ANIMATION_SPEED) {
                animationFrame = (animationFrame + 1) % 3;
                animationTimer = 0;
            }
        } else {
            // 不移动 → 停在第一帧
            animationFrame = 0;
            animationTimer = 0;
        }
    }

    // 绘制
    public void draw(GameEngine engine) {
        if (spriteSheet == null) return;

        int sx = animationFrame * FRAME_SIZE;
        int sy = direction * FRAME_SIZE;

        Image currentFrame = engine.subImage(spriteSheet, sx, sy, FRAME_SIZE, FRAME_SIZE);
        if (currentFrame != null) {
            engine.drawImage(currentFrame, x, y, 60, 60);
        }
    }

    // 重置
    public void reset(int startCol, int startRow) {
        this.col = startCol;
        this.row = startRow;
        this.x = startCol * TILE_SIZE;
        this.y = startRow * TILE_SIZE;
        this.targetX = x;
        this.targetY = y;
        this.direction = DIR_DOWN;
        this.hp = 3;
        this.isMoving = false;
        this.animationFrame = 0;
    }

    // 更新方向
    protected void updateDirection(int dx, int dy) {
        if (dx > 0) direction = DIR_RIGHT;
        else if (dx < 0) direction = DIR_LEFT;
        else if (dy > 0) direction = DIR_DOWN;
        else if (dy < 0) direction = DIR_UP;
    }

}