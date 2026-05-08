package scenes;

import core.GameEngine;
import java.awt.Image;

public class StartMenu {
    private final Image startBackground;
    public StartMenu(GameEngine engine) {
        startBackground = engine.loadImage("resource/sprites/start_bg.png");
    }
    public void draw(GameEngine engine){
        engine.changeColor(255,204,255);
        engine.drawImage(startBackground,0,0,640,640);
    }
}
