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

    protected MapManager mapManager;

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

    //藤蔓
    protected boolean isCasting;
    protected boolean isClearing; // 消除状态开关

    protected double castTimer = 0;
    protected int remainingVineGrids = 0;
    protected int currentCastCol, currentCastRow;
    protected final double GROW_INTERVAL = 0.1; // 每 0.15 秒长一格

    // 移动状态
    protected boolean isMoving = false;

    // 移动速度（像素/秒）
    protected final int MOVE_SPEED = 200;

    // 你的对手
    protected Player opponent;

    public void setOpponent(Player opponent) {
        this.opponent = opponent;
    }

    // 技能
    public abstract void useSkill(MapManager mapManager);

    // 受伤
    public void takeDamage() {
        this.hp--;
    }

    // ========================== 【移动逻辑】 =========================
    public void move(int dx, int dy, CollisionCheck collisionCheck,Player otherPlayer) {
        //锁死
        if (isCasting || isClearing) return;
        // 无论是否移动成功，都更新朝向
        updateDirection(dx, dy);
        // 正在移动时，不接受新移动指令 → 防止连点瞬移
        if (isMoving) return;

        int nextCol = this.col + dx;
        int nextRow = this.row + dy;

        // 1. 检查地图障碍 (墙、藤蔓)
        if (collisionCheck.isSolid(nextCol, nextRow)) return;

        if (otherPlayer != null && otherPlayer.col == nextCol && otherPlayer.row == nextRow) {
            // 如果格子里有人，撞不动，直接返回
            return;
        }
        // 更新逻辑格子
        this.col = nextCol;
        this.row = nextRow;

        // 设置新的目标坐标
        targetX = col * TILE_SIZE;
        targetY = row * TILE_SIZE;

        isMoving = true; // 开始移动

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
            engine.drawImage(currentFrame, x-25, y-25, 90, 90);
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
        //锁死
        if (isCasting || isClearing) return;
        if (dx > 0) direction = DIR_RIGHT;
        else if (dx < 0) direction = DIR_LEFT;
        else if (dy > 0) direction = DIR_DOWN;
        else if (dy < 0) direction = DIR_UP;
    }


}