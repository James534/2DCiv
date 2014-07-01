package CivPackage.Models;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by james on 6/30/2014.
 */
public class Entity extends Actor{

    protected Texture texture;
    protected Vector2 pos;
    protected Vector2 pixelPos;

    public Entity (Vector2 pos){
        this.pos = pos;
    }

    public void draw (SpriteBatch batch, float Alpha){

    }

}
