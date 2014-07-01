package CivPackage.Models;

import CivPackage.MathCalc;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;


/**
 * Created by james on 6/29/2014.
 */
public class Hex extends Actor{
    //the tiles in pixel coordinates,
    private Vector2 pos;

    //the tiles in map x and y coordinates, stored in the array
    private Vector2 mapCoordinates;
    private boolean even;   //if this tile is even or odd

    private int id;
    private Texture texture;
    private static Texture texture1 = new Texture("core/assets/Hex6.png");
    private static Texture texture2 = new Texture("core/assets/Hex5.png");
    private float h;    //height
    private boolean selected;

    public Hex(int id, int x, int y){
        pos = new Vector2();
        mapCoordinates = new Vector2(x,y);
        if (mapCoordinates.x%2 == 0){
            even = true;
        }else{
            even = false;
        }
        this.id = id;

        texture = texture1;

        //https://weblogs.java.net/blog/malenkov/archive/2009/02/hexagonal_tile.html
        float w = texture.getWidth()+5;
        float r = w /2;
        h = 3*r / 2*(float)Math.sin((MathCalc.DegToRad(60)));


        pos.x = (2*x - y + 1) * w/2;
        pos.y = (y+2/3) * h +1;

    }


    public void selected(boolean t){
        selected = t;
        if (selected){
            texture = texture2;
        }else {
            texture = texture1;
        }
    }

    public Texture getTexture(){
        return texture;
    }

    public Vector2 getPos(){
        return pos;
    }
}
