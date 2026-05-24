package ui;

import core.GameEngine;
import java.awt.Color;

/**
 * 独立演示 UI 组件的程序
 * 不依赖 GameInstance，不修改任何原有代码
 */
public class UIDemo extends GameEngine {
    private UI gameUI;
    private int hp = 3;
    private int score = 0;
    private double timeLeft = 90.0;
    private long lastSecond = 0;

    public static void main(String[] args) {
        createGame(new UIDemo(), 60);
    }

    @Override
    public void init() {
        gameUI = new UI(this);
        lastSecond = getTime();
    }

    @Override
    public void update(double dt) {
        // 模拟倒计时（每秒减1）
        long now = getTime();
        if (now - lastSecond >= 1000) {
            if (timeLeft > 0) {
                timeLeft -= 1;
                if (timeLeft < 0) timeLeft = 0;
            }
            lastSecond = now;
        }

        // 演示：按键盘数字 1 减少血量，数字 2 增加血量，数字 3 增加得分
        // 注意：键盘事件需要 KeyListener，已在 GameEngine 中提供空实现，我们可以覆写
    }

    @Override
    public void paintComponent() {
        // 清屏
        changeColor(Color.BLACK);
        drawSolidRectangle(0, 0, width(), height());

        // 绘制 UI
        if (gameUI != null) {
            gameUI.draw(this, hp, score, timeLeft);
        }

        // 显示操作提示
        changeColor(Color.WHITE);
        drawText(200, 150, "Press 1 (lose life)  2 (gain life)  3 (add score)", "Arial", 18);
        drawText(260, 200, "ESC to exit", "Arial", 16);
    }

    // 可选：覆写键盘事件，方便测试
    @Override
    public void keyPressed(java.awt.event.KeyEvent e) {
        int key = e.getKeyCode();
        if (key == java.awt.event.KeyEvent.VK_1) {
            hp = Math.max(0, hp - 1);
        } else if (key == java.awt.event.KeyEvent.VK_2) {
            hp = Math.min(3, hp + 1);
        } else if (key == java.awt.event.KeyEvent.VK_3) {
            score += 10;
        } else if (key == java.awt.event.KeyEvent.VK_ESCAPE) {
            System.exit(0);
        }
    }
}