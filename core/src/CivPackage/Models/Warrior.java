package CivPackage.Models;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by james on 7/1/2014.
 */
public class Warrior extends Entity{

    public Warrior(Vector2 pos, int id){
        super(pos, id, 20, 2);
        this.texture = new Texture("core/assets/Warrior.png");
        this.type = 1;
        this.range = 1;
    }
}
