package CivPackage.Models;

import CivPackage.GameProject;
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
    private static final String[] hexNames = {(GameProject.fileName + "Hex/Hex0.png")
            , (GameProject.fileName + "Hex/OceanDeep.png"), (GameProject.fileName + "Hex/OceanLight.png")
            , (GameProject.fileName + "Hex/Plains.png"), (GameProject.fileName + "Hex/PlainsHill.png"), (GameProject.fileName + "Hex/PlainsMountain.png")
            , (GameProject.fileName + "Hex/Grass.png"), (GameProject.fileName + "Hex/GrassHill.png"), (GameProject.fileName + "Hex/GrassMountain.png")
            , (GameProject.fileName + "Hex/Desert.png"), (GameProject.fileName + "Hex/DesertHill.png"), (GameProject.fileName + "Hex/DesertMountain.png")
            , (GameProject.fileName + "Hex/Hex7.png")
    };

    private static final Texture[] textures = {new Texture(GameProject.fileName + "Hex/Hex0.png")
            , new Texture(GameProject.fileName + "Hex/OceanDeep.png"), new Texture(GameProject.fileName + "Hex/OceanLight.png")
            , new Texture(GameProject.fileName + "Hex/Plains.png"), new Texture(GameProject.fileName + "Hex/PlainsHill.png"), new Texture(GameProject.fileName + "Hex/PlainsMountain.png")
            , new Texture(GameProject.fileName + "Hex/Grass.png"),  new Texture(GameProject.fileName + "Hex/GrassHill.png"),  new Texture(GameProject.fileName + "Hex/GrassMountain.png")
            , new Texture(GameProject.fileName + "Hex/Desert.png"), new Texture(GameProject.fileName + "Hex/DesertHill.png"), new Texture(GameProject.fileName + "Hex/DesertMountain.png")
            , new Texture(GameProject.fileName + "Hex/Hex7.png")
    };
    private static final Pixmap[] rivers = {new Pixmap(Gdx.files.internal(GameProject.fileName + "Hex/River1.png"))
            ,new Pixmap(Gdx.files.internal(GameProject.fileName + "Hex/River2.png"))
            ,new Pixmap(Gdx.files.internal(GameProject.fileName + "Hex/River3.png"))
            ,new Pixmap(Gdx.files.internal(GameProject.fileName + "Hex/River4.png"))
            ,new Pixmap(Gdx.files.internal(GameProject.fileName + "Hex/River5.png"))
            ,new Pixmap(Gdx.files.internal(GameProject.fileName + "Hex/River6.png"))
    };

    public static final Texture SELECTED = new Texture(GameProject.fileName + "Hex/Selected.png");
    public static final Pixmap PATH = new Pixmap(Gdx.files.internal(GameProject.fileName + "Hex/Path.png"));

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
        texture = textures[getImageId(id)];

        if (id / 100 == 1){
            Pixmap p = new Pixmap(Gdx.files.internal(GameProject.fileName + "Hex/Hex7.png"));
            p.drawPixmap(rivers[0], 0, 0);
            p.drawPixmap(rivers[2], 0, 0);
            texture = new Texture(p);
        }

        cost = 1;
        if (id == 9){
            walkable = false;
        }

        pixelPos.x = pos.x * HexD + (pos.y %2)*HexR;
        pixelPos.y = pos.y * HexHS;
    }

    private int getImageId(int id){
        switch (id){
            case 1:
                return 1;
            case 2:
                return 2;
            case 10:
                return 3;
            case 18:
                return 4;
            case 19:
                return 5;
            case 20:
                return 6;
            case 28:
                return 7;
            case 29:
                return 8;
            case 30:
                return 9;
            case 38:
                return 10;
            case 39:
                return 11;
            case 40:
                return 12;
            case 48:
                return 12;
            case 49:
                return 12;
        }
        return 0;
    }

    public void addRiver(String where){
        Pixmap p = new Pixmap(Gdx.files.internal(hexNames[getImageId(id)]));
        for (int i = 0; i < where.length(); i++){
            if (where.charAt(i) == '1'){
                p.drawPixmap(rivers[i], 0, 0);
            }
        }
        texture = new Texture(p);
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
