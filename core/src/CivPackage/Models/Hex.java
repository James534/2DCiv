package CivPackage.Models;

import CivPackage.MathCalc;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;


/**
 * Created by james on 6/29/2014.
 */
public class Hex extends Actor{
    //the tiles in pixel coordinates,
    private Vector2 pixelPos;

    //the tiles in map x and y coordinates, stored in the array
    private Vector2 pos;
    private boolean even;   //if this tile is even or odd
    private boolean selected;

    private int id;         //what this tile is
    private int resource;   //id of the resource
    private int SRCount;    //amount of strategic resource, iron, horses, etc
    private float cost;       //how much it costs to move across this tile
    private boolean walkable = true;//
    private Entity unit;    //the unit that is currently on this hex

    private Texture texture;
    private static Texture[] textures = {new Texture("core/assets/Hex/Hex0.png")
            , new Texture("core/assets/Hex/OceanDeep.png"), new Texture("core/assets/Hex/OceanLight.png")
            , new Texture("core/assets/Hex/Beach.png"), new Texture("core/assets/Hex/Grass.png")
            , new Texture("core/assets/Hex/Hex3.png"), new Texture("core/assets/Hex/Hex4.png")
            , new Texture("core/assets/Hex/Hex5.png"), new Texture("core/assets/Hex/Hex6.png"), new Texture("core/assets/Hex/Mountain.png")};
    public static final Texture SELECTED = new Texture("core/assets/Hex/Selected.png");
    public static final Pixmap PATH = new Pixmap(Gdx.files.internal("core/assets/Hex/Path.png"));

    //http://www.gamedev.net/page/resources/_/technical/game-programming/coordinates-in-hexagon-based-tile-maps-r1800
    public static final int HexS = 30;
    public static final int HexH = 15;  //S * sin(30) (height)
    public static final int HexR = 26;  //S * cos(30) (radius)
    public static final int HexD = 52;  //2 * HexR    (diameter)
    public static final int HexHS= 45;  //HexH + HexS

    public Hex(int id, int x, int y){
        pixelPos = new Vector2();
        pos = new Vector2(x,y);
        if (pos.y%2 == 0){
            even = true;
        }else{
            even = false;
        }
        this.id = id;
        texture = textures[id];

        cost = id;
        if (id == 9){
            walkable = false;
        }else if (id == 5){
            cost = 0.5f;
        }

        pixelPos.x = pos.x * HexD + (pos.y %2)*HexR;
        pixelPos.y = pos.y * HexHS;
    }

    public void selected(boolean t){
        selected = t;
        if (selected){
            texture = textures[7];
        }else {
            texture = textures[id%8];
        }
    }

    public Texture getTexture(){
        return texture;
    }
    public Vector2 getPixelPos(){return pixelPos;}
    public Vector2 getPos(){return pos;}
    public int getMapX(){return (int)pos.x;}
    public int getMapY(){return (int)pos.y;}
    public boolean getEven(){return even;}
    public Entity getUnit() {return unit;}
    public void addUnit(Entity unit){this.unit = unit;}
    public float getCost(){return cost;}
    public boolean getWalkable(){return walkable;}
}
