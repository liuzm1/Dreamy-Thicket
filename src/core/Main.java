package core;
//初始化窗口和启动游戏循环
import javax.swing.SwingUtilities;

public class Main {
    public static void main() {
      GameInstance game = new GameInstance();
      GameEngine.createGame(game,60);
    }
}
