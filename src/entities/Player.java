package entities;

import core.GameEngine;
import maps.CollisionCheck;
import maps.MapManager;

import java.awt.*;
import java.awt.Image;


public abstract class Player {
    // 逻辑位置（网格）
    public int col;
    public int row;
    //像素位置（屏幕）
    public int x;
    public int y;

    // 每个格子的像素大小
    protected final int TILE_SIZE = 40;

    protected int hp = 3;

    //上0 下1 左2 右3
    // 定义方向常量，方便阅读和维护
    public static final int DIR_UP = 3;    // 对应图片第3行
    public static final int DIR_DOWN = 0;  // 对应图片第0行
    public static final int DIR_LEFT = 1;  // 对应图片第1行
    public static final int DIR_RIGHT = 2; // 对应图片第2行
    protected int direction = DIR_DOWN;

    //人物一帧的图片大小是32x32
    protected final int FRAME_SIZE = 32;
    public Image spriteSheet;

    protected int animationFrame = 0; // 当前是第几帧 (0, 1, 2)
    protected double animationTimer = 0; // 动画计时器
    protected final double ANIMATION_SPEED = 0.3; // 换帧速度（秒），越小越快

    // 增加一个变量判断玩家是否在移动
    protected boolean isMoving = false;

    // ***让子类去实现具体的技能逻辑*** //
    public abstract void useSkill(MapManager mapManager);

    //人物收到伤害
    public void takeDamage(){
        this.hp--;
    }


    //人物移动,dx == 1往右， dx ==-1 往左， dy == 1 往下， dy == -1 往上
    public void move(int dx, int dy, CollisionCheck collisionCheck){
        int nextCol = this.col + dx;
        int nextRow = this.row + dy;

        //判断下一个格子是不是障碍物
        if(!collisionCheck.isSolid(nextCol, nextRow)){
            this.col = nextCol;
            this.row = nextRow;

            x = col * TILE_SIZE;
            y = row * TILE_SIZE;
            this.isMoving = true;
        }
        // 无论移动成没成功，只要按了键，朝向都要变（为了放技能方便）
        updateDirection(dx, dy);
    }

    //绘制人物
    public void draw(GameEngine engine){
        if (spriteSheet == null) { return; }

        // 动态计算裁剪位置
        // animationFrame 是 0, 1, 2，乘以 32 就得到 0, 32, 64
        int sx = animationFrame * FRAME_SIZE;
        int sy = direction * FRAME_SIZE;

        Image currentFrame = engine.subImage(spriteSheet, sx, sy, FRAME_SIZE, FRAME_SIZE);

        if (currentFrame != null) {
            engine.drawImage(currentFrame, x, y, 60, 60);
        }
    }

    public void update(double dt) {
        // 如果玩家在移动，才播放动画
        if (isMoving) {
            animationTimer += dt;
            if (animationTimer >= ANIMATION_SPEED) {
                // 在 0, 1, 2 帧之间循环
                animationFrame = (animationFrame + 1) % 3;
                animationTimer = 0; // 重置计时器
                // 【逻辑微调】如果回到第 0 帧（站立帧），说明一个跨步动作做完了
                if (animationFrame == 0) {
                    isMoving = false;
                }
            } else {
                // 如果不移动，恢复到第 0 帧（站立帧）
                animationFrame = 0;
            }
        }
    }

    //重置逻辑
    public void reset(int startCol, int startRow) {
        // 重置逻辑位置
        this.col = startCol;
        this.row = startRow;

        // 重置物理位置
        this.x = startCol * TILE_SIZE;
        this.y = startRow * TILE_SIZE;

        // 重置状态
        this.direction = DIR_DOWN;
        this.hp = 3;
        this.isMoving = false;
        this.animationFrame = 0;
    }

        //更改人物朝向
        protected void updateDirection ( int dx, int dy){
            if (dx > 0) direction = DIR_RIGHT;
            else if (dx < 0) direction = DIR_LEFT;
            else if (dy > 0) direction = DIR_DOWN;
            else if (dy < 0) direction = DIR_UP;
        }

    }
