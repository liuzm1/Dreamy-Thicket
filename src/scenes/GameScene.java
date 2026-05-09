package scenes;
//图画处理和处理点击的接口
import core.GameEngine;

public interface GameScene {
    //每个页面都要画自己
    void draw(GameEngine engine);
    //每个页面都要处理自己的点击，并返回“下一步去哪个状态”
    //如果返回 -1 或当前状态值，表示不跳转
    int handleMouseClick(int mx, int my);

}
