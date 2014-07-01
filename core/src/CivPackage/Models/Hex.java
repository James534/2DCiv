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
    private boolean selected;

    private int id;
    private Texture texture;
    private static Texture texture1 = new Texture("core/assets/Hex6.png");
    private static Texture texture2 = new Texture("core/assets/Hex5.png");

    //http://www.gamedev.net/page/resources/_/technical/game-programming/coordinates-in-hexagon-based-tile-maps-r1800
    public static final int HexS = 30;
    public static final int HexH = 15;  //S * sin(30) (height)
    public static final int HexR = 26;  //S * cos(30) (radius)
    public static final int HexD = 52;  //2 * HexR    (diameter)
    public static final int HexHS= 45;  //HexH + HexS

    public Hex(int id, int x, int y){
        pos = new Vector2();
        mapCoordinates = new Vector2(x,y);
        if (mapCoordinates.y%2 == 0){
            even = true;
        }else{
            even = false;
        }
        this.id = id;

        texture = texture1;

        pos.x = mapCoordinates.x * HexD + (mapCoordinates.y %2)*HexR;
        pos.y = mapCoordinates.y * HexHS;
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

    public Vector2 getPos(){return pos;}
}
