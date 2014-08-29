package CivPackage.Models;

import CivPackage.GameProject;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by James on 2014-08-23.
 */
public class Settler extends Entity{

    public Settler(Vector2 pos, int id){
        super (pos, id, 20, 2);
        //this.texture = new Texture(GameProject.fileName + "Warrior.png");
        this.type = 1;
        this.range = 0;
        this.fighting = false;
    }
}
