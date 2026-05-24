package ui;

import core.GameEngine;

import java.awt.Image;

/**
 * 游戏界面 UI 组件
 * 显示生命值（图片）、得分（右上角）、90秒倒计时（中间）
 */
public class GameUI {
    private Image heartImage;
    private Image SocreBoardImage;
    private Image Board;
    private Image cup_Icon;
    private Image star_Icon, clock_Icon;
    private Image player1,P1_headshot, player2, P2_headshot;
    private Image[] Numbers;


    public GameUI(GameEngine engine) {
        heartImage = engine.loadImage("resource/sprites/menus/ui_heart_UI.png");
        SocreBoardImage = engine.loadImage("resource/sprites/menus/ui_board.png");
        Board = engine.loadImage("resource/sprites/menus/ui_board_1.png");
        cup_Icon = engine.loadImage("resource/sprites/menus/icon_cup.png");
        star_Icon = engine.loadImage("resource/sprites/menus/icon_star.png");
        clock_Icon = engine.loadImage("resource/sprites/menus/icon_clock.png");

        player1 = engine.loadImage("resource/sprites/entities/P1.png");
        P1_headshot = engine.subImage(player1,40, 2, 16, 19);

        player2 = engine.loadImage("resource/sprites/entities/P2.png");
        P2_headshot = engine.subImage(player2,40, 2, 16, 19);

        Numbers = new Image[10];
        for(int i = 0; i < 10; i++){
            Numbers[i] = engine.loadImage("resource/sprites/menus/numbers/" + i +".png");
        }

        if (heartImage == null) {
            heartImage = engine.loadImage("ui_heart_UI.png");
        }
    }

    public void draw(GameEngine engine, int hp, int score, double timeLeft,int target,boolean isTwoPlayer) {
        engine.drawImage(SocreBoardImage,10,5,120,40);
        engine.drawImage(SocreBoardImage,140,5,120,40);
        //左侧头像 生命值
        engine.drawImage(Board, 30, 580, 180, 45);
        engine.drawImage(P1_headshot, 34, 573, 45, 50);
        if(isTwoPlayer){
            //左侧头像 生命值
            engine.drawImage(Board, 430, 580, 180, 45);
            engine.drawImage(P2_headshot, 561, 573, 45, 50);
        }

        engine.drawImage(Board, 260, 580, 120, 45);
        engine.drawImage(clock_Icon,253,571,57,57);

        engine.drawImage(cup_Icon,6,-6,57,57);
        engine.drawImage(star_Icon,136,-6,57,57);

        for(int i = 3; i >= 0; i--){
            int divisor = (int) Math.pow(10,i);
            int digit = target / divisor % 10;
            engine.drawImage(Numbers[digit],40 + (3 - i) * 18,4,45,45);
        }

        //hp
        for (int i = 0; i < hp && i < 3; i++) {
            engine.drawImage(heartImage, 85 + i * 40, 587, 32, 32);
        }
        if(isTwoPlayer){
            for (int i = 0; i < hp && i < 3; i++) {
                engine.drawImage(heartImage, 443 + i * 40, 587, 32, 32);
            }
        }
        //


        for(int i = 3; i >= 0; i--){
            int divisor = (int) Math.pow(10,i);
            int digit = score / divisor % 10;
            engine.drawImage(Numbers[digit],170 + (3 - i) * 18,4,45,45);
        }

        // 3. 倒计时
        for(int i = 2; i >= 0; i--){
            int divisor = (int) Math.pow(10,i);
            int digit =(int) timeLeft / divisor % 10;
            engine.drawImage(Numbers[digit],288 + (2 - i) * 25,575,52,52);
        }
    }
}