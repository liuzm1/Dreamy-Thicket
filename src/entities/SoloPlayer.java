package entities;
import core.GameEngine;
import java.awt.*;
import java.awt.Image;

public class SoloPlayer extends Player {

    public SoloPlayer(GameEngine engine, int startCol, int startRow) {
        //人物站在的格子位置
        this.col = startCol;
        this.row = startRow;
        //人物站在的像素位置
        this.x = startCol * TILE_SIZE;
        this.y = startRow * TILE_SIZE;

        spriteSheet = engine.loadImage("resource/sprites/entities/P1.png");
    }


//    **
//            * 虽然现在不设计藤蔓逻辑，但因为父类是 abstract，这里必须实现
//     * 先留个空，保证程序能编译通过
//     */
    @Override
    public void useSkill(maps.MapManager map) {
        // 暂时留空，下一步再写
        System.out.println("技能还没冷却好呢！");
    }



}
