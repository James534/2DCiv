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

    private String id;         //what this tile is
    private int resource;   //id of the resource
    private int SRCount;    //amount of strategic resource, iron, horses, etc
    private float cost;       //how much it costs to move across this tile
    private boolean walkable = true;//
    private Entity unit;    //the unit that is currently on this hex

    private Texture texture;

    //File names of the hex tiles
    private static String[] hexNames = {"Hex0",             //0
            "Ocean",    "OceanAtoll",       "OceanIce",     //3
            "Shore",    "ShoreAtoll",       "ShoreIce",     //6
            "Desert",   "DesertHills",      "DesertMountain",   "DesertFallout",    "DesertOasis",      "DesertFloodplains",    //12
            "Grassland","GrasslandHills",   "GrasslandMountain","GrasslandFallout", "GrasslandForest",  "GrasslandHillForest","GrasslandHillJungle","GrasslandJungle",  //20
            "Plains",   "PlainsHills",      "PlainsMountain",   "PlainsFallout",    "PlainsForest",     "PlainsHillForest",     //26
            "Snow",     "SnowHills",        "SnowMountain", //29
            "Tundra",   "TundraHills",      "TundraMountain",   "TundraFallout",    "TundraForest",     "TundraHillForest"
    };
    static{
        //assembles the file name into the full file name, to avoid repeating code above
        for (int i = 0; i < hexNames.length; i++){
            hexNames[i] = GameProject.fileName + "Hex/256/Tiles/" + hexNames[i] + ".png";
        }
    }

    //Array of textures preloaded from the file names above
    private static Texture[] textures;
    static {
        textures = new Texture[hexNames.length];
        for (int i = 0; i < hexNames.length; i++){
            textures[i] = new Texture(hexNames[i]);
        }
    }

    private static final Pixmap[] rivers = {new Pixmap(Gdx.files.internal(GameProject.fileName + "Hex/256/River1.png"))
            ,new Pixmap(Gdx.files.internal(GameProject.fileName + "Hex/256/River2.png"))
            ,new Pixmap(Gdx.files.internal(GameProject.fileName + "Hex/256/River3.png"))
            ,new Pixmap(Gdx.files.internal(GameProject.fileName + "Hex/256/River4.png"))
            ,new Pixmap(Gdx.files.internal(GameProject.fileName + "Hex/256/River5.png"))
            ,new Pixmap(Gdx.files.internal(GameProject.fileName + "Hex/256/River6.png"))
    };

    public static final Texture SELECTED = new Texture(GameProject.fileName + "Hex/256/SelectedHex.png");
    public static final Pixmap PATH = new Pixmap(Gdx.files.internal(GameProject.fileName + "Hex/256/Path.png"));

    //http://www.gamedev.net/page/resources/_/technical/game-programming/coordinates-in-hexagon-based-tile-maps-r1800
    public static final int HexS = 128;
    public static final int HexH = 64;  //S * sin(30) (height)
    public static final int HexR = 110; //S * cos(30) (radius)
    public static final int HexD = 220; //2 * HexR    (diameter)
    public static final int HexHS= 192; //HexH + HexS

    /*
    Id directory:
        abcdefghijklmn

        |letter and name|------------------|a,          b,          c,              d,          e,      f,      g,      h
        0  a: terrain type;                 flat land,  hill,       mountain
        1  b: terrain;                      ocean,      coast/lake, desert,         grassland,  plains, snow,   tundra
        2  c: terrain features;             atoll,      fallout,    flood plains,   forest,     ice,    jungle, marsh,  oasis
        3  d: rivers/fresh water;           top right,  right,      bottom right,   bottom left,left,   top left,lake
        4  e: wonders;                      17 of them
        5  f: food resource;                bananas,    wheat,      cattle,         sheep,      deer,   fish,   stone
        6  g: luxury resource;              25 of them
        7  h: strategic resource;           horses,     iron,       coal,           aluminum,   oil,    uranium
        8  i: pillaged improvement;         yes,        no
        9  j: great people improvements;    8 of them
        10 k: regular improvements;         21 of them
        11 l: roads;                        road,       railroad
        12 m: exploration;                  explored,   unexplored
        13 n: land ownership;               free land,  your land

        Wonders:
        Luxury resource:
        GP improvements:                    academy, ancient ruins, citadel, city ruins, customs house, encampment, holy site, manufactory
        Regular improvements:               archaeological dig, brazilwood camp, camp, chateau, farm, feitoria, fishing boats, fort, kasbah,
                                            landmark, lumber mill, mine, moai, offshore platform, oil well, pasture, plantation, polder, quarry, terrance farm, trading post
     */

    /**
     *  Creates a new hex
     * @param id    refer to the chart on top for what the id means
     * @param x
     * @param y
     */
    public Hex(String id, int x, int y){
        pixelPos = new Vector2();
        pos = new Vector2(x,y);
        if (pos.y%2 == 0){
            even = true;
        }else{
            even = false;
        }
        id = id.toLowerCase();
        this.id = id;

        //assign the texture
        /*if (id.charAt(4) != 0){     //checks if this is a wonder, if it is, just assign it the wonder texture
            texture = textures[0];
        }else {
            texture = textures[getImageId(id.substring(0, 3))];
        }*/
        texture = textures[getImageId(id.substring(0, 3))];
        cost = 1;

        pixelPos.x = pos.x * HexD + (pos.y %2)*HexR;
        pixelPos.y = pos.y * HexHS;
    }

    /**
     * Returns the image id of the hex using a bunch of switches
     * @param id the first 3 characters of the hex id
     * @return  integer of the location in the array which the image is stored in
     */
    private int getImageId(String id){
        switch (id.charAt(1)){
            case 'a':{                                  //ocean
                if (id.charAt(2) == 'e')    //ice
                    return 3;
                else
                    return 1;               //ocean
            }case 'b':{                                 //shore/lake
                switch (id.charAt(2)){
                    case '0':
                        return 4;           //shore/lake
                    case 'a':
                        return 5;           //atoll
                    case 'e':
                        return 6;           //ice
                    default:
                        return 4;
                }
            }case 'c':{                                 //desert
                switch (id.charAt(0)){
                    case 'c':   //mountain
                        return 9;          //desert mountain
                    case 'a':{  //flat land
                        switch (id.charAt(2)){
                            case '0':
                                return 7;    //desert
                            case 'c':
                                return 12;   //flood plains
                            case 'h':
                                return 11;   //oasis
                            default:
                                return 7;
                        }
                    }
                    case 'b':{  //hill
                        return 8;          //desert hills
                    }
                }
            }case 'd':{                                 //grassland
                switch (id.charAt(0)){
                    case 'c':   //mountain
                        return 15;           //grassland mountain
                    case 'a':{  //flat land
                        switch (id.charAt(2)){
                            case '0':
                                return 13;   //grassland
                            case 'd':
                                return 19;   //forest
                            case 'f':
                                return 20;   //jungle
                            case 'g':
                                return 21;   //marsh
                            default:
                                return 13;
                        }
                    }
                    case 'b':{  //hill
                        switch (id.charAt(2)){
                            case '0':
                                return 14;   //grassland hill
                            case 'd':
                                return 18;   //forest
                            case 'f':
                                return 19;   //jungle
                            default:
                                return 14;
                        }
                    }
                }
            }case 'e':{                                 //plains
                switch (id.charAt(0)){
                    case 'c':   //mountain
                        return 23;           //plains mountain
                    case 'a':{  //flat land
                        switch (id.charAt(2)){
                            case '0':
                                return 21;   //plains
                            case 'd':
                                return 25;   //forest
                            default:
                                return 21;
                        }
                    }
                    case 'b':{  //hill
                        switch (id.charAt(2)){
                            case '0':
                                return 22;   //plains hill
                            case 'd':
                                return 26;   //forest
                            default:
                                return 22;
                        }
                    }
                }
            }case 'f':{                                 //snow
                switch (id.charAt(0)){
                    case 'a':{  //flat land
                        return 28;
                    }case 'b':{ //hill
                        return 29;
                    }case 'c':{ //mountain
                        return 30;
                    }
                }
            }case 'g':{                                 //tundra
                switch (id.charAt(0)){
                    case 'a':{  //flat land
                        if (id.charAt(2) == 'd')
                            return 34;      //forest
                        else
                            return 30;      //tundra
                    }case 'b':{ //hill
                        if (id.charAt(2) == 'd')
                            return 35;      //forest
                        else
                            return 31;      //tundra hill
                    }case 'c':  //mountain
                        return 32;          //tundra mountain
                }
            }
            default:
                return 0;
        }
    }

    public void addRiver(String where){
        Pixmap p = new Pixmap(Gdx.files.internal(hexNames[getImageId(id.substring(0, 3))]));
        for (int i = 0; i < where.length(); i++){
            if (where.charAt(i) == '1'){
                p.drawPixmap(rivers[i], 0, 0);
            }
        }
        texture = new Texture(p);
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
