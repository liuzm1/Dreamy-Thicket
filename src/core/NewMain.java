package core;

import ui.UIGameInstance;

public class NewMain {
    public static void main(String[] args) {
        GameEngine.createGame(new UIGameInstance(), 60);
    }
}