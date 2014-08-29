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
                    return 0;
                else
                    return 1;               //ocean
            }case 'b':{                                 //shore/lake
                switch (id.charAt(2)){
                    case '0':
                        return 2;           //shore/lake
                    case 'a':
                        return 0;           //atoll
                    case 'e':
                        return 0;           //ice
                    default:
                        return 2;
                }
            }case 'c':{                                 //desert
                switch (id.charAt(0)){
                    case 'c':   //mountain
                        return 11;          //desert mountain
                    case 'a':{  //flat land
                        switch (id.charAt(2)){
                            case '0':
                                return 9;   //desert
                            case 'c':
                                return 0;   //flood plains
                            case 'h':
                                return 0;   //oasis
                            default:
                                return 9;
                        }
                    }
                    case 'b':{  //hill
                        switch (id.charAt(2)){
                            case '0':
                                return 10;  //desert hill
                            case 'c':
                                return 0;   //flood plains
                            case 'h':
                                return 0;   //oasis
                            default:
                                return 10;
                        }
                    }
                }
            }case 'd':{                                 //grassland
                switch (id.charAt(0)){
                    case 'c':   //mountain
                        return 8;           //grassland mountain
                    case 'a':{  //flat land
                        switch (id.charAt(2)){
                            case '0':
                                return 6;   //grassland
                            case 'd':
                                return 0;   //forest
                            case 'f':
                                return 0;   //jungle
                            case 'g':
                                return 0;   //marsh
                            default:
                                return 6;
                        }
                    }
                    case 'b':{  //hill
                        switch (id.charAt(2)){
                            case '0':
                                return 7;   //grassland hill
                            case 'd':
                                return 0;   //forest
                            case 'f':
                                return 0;   //jungle
                            case 'g':
                                return 0;   //marsh
                            default:
                                return 7;
                        }
                    }
                }
            }case 'e':{                                 //plains
                switch (id.charAt(0)){
                    case 'c':   //mountain
                        return 5;           //plains mountain
                    case 'a':{  //flat land
                        switch (id.charAt(2)){
                            case '0':
                                return 3;   //plains
                            case 'd':
                                return 0;   //forest
                            case 'f':
                                return 0;   //jungle
                            default:
                                return 3;
                        }
                    }
                    case 'b':{  //hill
                        switch (id.charAt(2)){
                            case '0':
                                return 4;   //plains hill
                            case 'd':
                                return 0;   //forest
                            case 'f':
                                return 0;   //jungle
                            default:
                                return 4;
                        }
                    }
                }
            }case 'f':{                                 //snow
                switch (id.charAt(0)){
                    case 'a':{  //flat land
                        return 0;
                    }case 'b':{ //hill
                        return 0;
                    }case 'c':{ //mountain
                        return 0;
                    }
                }
            }case 'g':{                                 //tundra
                switch (id.charAt(0)){
                    case 'a':{  //flat land
                        if (id.charAt(2) == 'd')
                            return 0;       //forest
                        else
                            return 12;      //tundra
                    }case 'b':{ //hill
                        if (id.charAt(2) == 'd')
                            return 0;       //forest
                        else
                            return 12;      //tundra hill
                    }case 'c':  //mountain
                        return 0;           //tundra mountain
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
