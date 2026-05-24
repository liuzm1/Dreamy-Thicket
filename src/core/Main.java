package core;
//初始化窗口和启动游戏循环
import static core.GameEngine.createGame;

public class Main {
    public static void main(String[] args) {
        createGame(new GameInstance(), 60);
    }
}
