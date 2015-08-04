package CivPackage.Models;

import CivPackage.GameProject;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import java.util.HashMap;


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

    public String landType = "";    //Ocean, Shore, Desert, Plains, Grassland, Tundra, Snow
    public int elevation;           //1 = normal, 2 = hill, 3 = mountain, 0 = water
    public static final String[] elevationName = {"", "", "Hills", "Mountain"};
    public boolean freshWater;
    public String river = "000000";
    public String feature = "";     //Atoll, Ice, Oasis, Jungle, Forest, Marsh
    private float[] terrainGains;   //the growth from this tile;    0 = food, 1 = production, 2 = gold, 3 = science, 4 = culture
    private String wonder = "";     //private because then it has to call a function, which allows me to update this hex' data

    //File names of the hex tiles
    private static String[] hexNames = {"Hex0",             //0
            "Ocean",    "OceanAtoll",       "OceanIce",     //3
            "Shore",    "ShoreAtoll",       "ShoreIce",     //6
            "Desert",   "DesertHills",      "DesertMountain",   "DesertFallout",    "DesertOasis",      "DesertFloodplains",    //12
            "Grassland","GrasslandHills",   "GrasslandMountain","GrasslandFallout", "GrasslandForest",  "GrasslandHillForest","GrasslandHillJungle","GrasslandJungle", "GrasslandMarsh",  //21
            "Plains",   "PlainsHills",      "PlainsMountain",   "PlainsFallout",    "PlainsForest",     "PlainsHillForest",     //27
            "Snow",     "SnowHills",        "SnowMountain", //30
            "Tundra",   "TundraHills",      "TundraMountain",   "TundraFallout",    "TundraForest",     "TundraHillForest",     //36
            "Special1"
    };
    //Array of textures preloaded from the file names above
    private static Texture[] textures;

    private static final String[] hexNames2 = {"Ocean", "Shore", "Desert", "Grassland", "Plains", "Snow", "Tundra", "Lake"};
    private static final String[] features = {"Atoll", "Ice", "Oasis", "Jungle", "Forest", "Marsh"};
    private static HashMap generatedHexes = new HashMap();  //key is string of the entire hex, value is the hex pixmap

    static {
        //assembles the file name into the full file name
        for (int i = 0; i < hexNames.length; i++){
            hexNames[i] = GameProject.fileName + "Hex/256/Tiles/" + hexNames[i] + ".png";
        }
        textures = new Texture[hexNames.length];
        for (int i = 0; i < hexNames.length; i++){
            textures[i] = new Texture(hexNames[i]);
        }
        //System.out.print("H");

        /*
        //v2
        Pixmap p, p2;
        Texture t;
        for (int i = 0; i < hexNames2.length; i++){
            for (int n = 0; n < features.length; n++){
                //System.out.println(hexNames2[i] + features[n]);
                p = new Pixmap(Gdx.files.internal(GameProject.fileName + "Hex/256/Tiles/"+hexNames2[i]+".png"));
                generatedHexes.put(hexNames2[i], new Texture(p));
                p2 = new Pixmap(Gdx.files.internal(GameProject.fileName + "Hex/256/Tiles/"+features[n]+".png"));
                generatedHexes.put(features[n], new Texture(p2));
                p = new Pixmap(Gdx.files.internal(GameProject.fileName + "Hex/256/Tiles/"+hexNames2[i]+".png"));
                p.drawPixmap(p2, 0, 0);
                t = new Texture(p);
                generatedHexes.put(hexNames2[i] + features[n], t);
            }
        }*/
    }

    public static final Texture[] ICONS = { //http://civilization.wikia.com/wiki/Category:Civilization_V_icon_templates
            new Texture(GameProject.fileName + "Hex/256/Icons/Food32.png"),
            new Texture(GameProject.fileName + "Hex/256/Icons/Two2.png"),
            new Texture(GameProject.fileName + "Hex/256/Icons/Three.png")
    };

    private static final Pixmap[] rivers = {new Pixmap(Gdx.files.internal(GameProject.fileName + "Hex/256/River1.png"))
            ,new Pixmap(Gdx.files.internal(GameProject.fileName + "Hex/256/River2.png"))
            ,new Pixmap(Gdx.files.internal(GameProject.fileName + "Hex/256/River3.png"))
            ,new Pixmap(Gdx.files.internal(GameProject.fileName + "Hex/256/River4.png"))
            ,new Pixmap(Gdx.files.internal(GameProject.fileName + "Hex/256/River5.png"))
            ,new Pixmap(Gdx.files.internal(GameProject.fileName + "Hex/256/River6.png"))
    };

    public static final Texture BC = new Texture(GameProject.fileName + "Hex/256/Wonders/BC.png");

    public static final Texture SELECTED = new Texture(GameProject.fileName + "Hex/256/Selected1.png");
    public static final Texture SELECTED2 = new Texture(GameProject.fileName + "Hex/256/Selected2.png");
    public static final Pixmap PATH = new Pixmap(Gdx.files.internal(GameProject.fileName + "Hex/256/Path.png"));

    //http://www.gamedev.net/page/resources/_/technical/game-programming/coordinates-in-hexagon-based-tile-maps-r1800
    public static final int HexS = 127;
    public static final int HexH = (int)Math.round(HexS * Math.sin(3.14156/6));  //S * sin(30) (height)
    public static final int HexR = (int)Math.round(HexS * Math.cos(3.14156/6));  //S * cos(30) (radius)
    public static final int HexD = HexR * 2; //2 * HexR    (diameter)
    public static final int HexHS= HexH + HexS; //HexH + HexS

    /*
    Id directory:       NO LONGER BEING USED
        abcdefghijklmn

        |letter and name|------------------|a,          b,          c,              d,          e,      f,      g,      h
        0  a: terrain type;                 flat land,  hill,       mountain
        1  b: terrain;                      ocean,      coast/lake, desert,         grassland,  plains, snow,   tundra
        2  d: rivers/fresh water;           River,      lake
        3  c: terrain features;             atoll,      fallout,    flood plains,   forest,     ice,    jungle, marsh,  oasis
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

    /*
            Banana - Jungle
            Wheat  - flood plains, plains
            Cattle - plains, hills, grassland
            Sheep  - plains, hills
            Bison  - grassland, plains
            Deer   - forest, tundra
            Fish   - coast
            Stone  - desert, plains, tundra, hills, snow
     */

    public Hex(int x, int y){
        pixelPos = new Vector2();
        pos = new Vector2(x,y);
        if (pos.y%2 == 0){
            even = true;
        }else{
            even = false;
        }
        cost = 1;
        terrainGains = new float[5];
        for (int i = 0; i < terrainGains.length; i++)
            terrainGains[0] = 0;

        pixelPos.x = pos.x * HexD + (pos.y %2)*HexR;
        pixelPos.y = pos.y * HexHS;
    }

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
        //texture = textures[getImageId(id.substring(0, 4))];
        cost = 1;

        pixelPos.x = pos.x * HexD + (pos.y %2)*HexR;
        pixelPos.y = pos.y * HexHS;
    }

    public void makeWonder(String s){
        wonder = s;
        switch (s){
            case "BC":              //becomes mountain
            case "Mesa":
            case "OF":
            case "CdP":
            case "SP":
            case "MSin":
            case "Kail":
            case "Ulu":
            case "Kili":
                elevation = 3;
                break;
            case "Fuji":
            case "Krak":            //becomes grassland mountain
                landType = "Grassland";
                elevation = 3;
                break;
            case "ED":              //flatland plains
            case "FoY":
            case "Vic":
            case "Mos":
                landType = "Plains";
                elevation = 1;
                break;
        }
        update();
    }

    /**
     * Call this method to update the tile after changes
     */
    public void update(){
        //============================= update the output values ==================
        //0 = food, 1 = production, 2 = gold, 3 = science, 4 = culture
        for (int i = 0; i < terrainGains.length; i++)
            terrainGains[i] = 0;        //reset all values

        if (elevation == 0) {
            terrainGains[0] = 1;                    //all water values are just +1 food
            if (landType.equals("Lake"))
                terrainGains[0] = 2;                //except lakes
            if (feature.equals("Ice")){             //ice has no gains
                terrainGains[0] = 0;
                terrainGains[1] = 0;
            }

        }else if (elevation == 1){
            if (feature == "") {                    //if there is no terrain feature,
                switch (landType) {                 //update landtype values
                    case "Tundra":
                        terrainGains[0] = 1;
                        break;
                    case "Grassland":
                        terrainGains[0] = 2;
                        break;
                    case "Plains":
                        terrainGains[0] = 1;
                        terrainGains[1] = 1;
                        break;
                    //desert and snow have no output values, so dont need to include
                }
            }else{                                  //if there is terrain feature, update according to the feature
                switch (feature){
                    case "Forest":
                    case "Atoll":
                        terrainGains[0] = 1;
                        terrainGains[1] = 1;
                        break;
                    case "Jungle":
                        terrainGains[0] = 1;
                        terrainGains[1] = -1;
                        break;
                    case "Marsh":
                        terrainGains[0] = -1;
                        break;
                    case "FloodPlains":
                        terrainGains[0] = 2;
                        break;
                    case "Oasis":
                        terrainGains[0] = 3;
                        terrainGains[2] = 1;
                        break;
                }
            }
        }else if (elevation == 2){
            terrainGains[1] = 2;        //hills are always +2 production
        }                               //mountains are +0, so not included

        //============================= update the image ==========================
        boolean test = true;
        if (elevation == 3) {
            walkable = false;
        }else{
            walkable = true;
        }

        if (test) {
            //v2
            String[] l = {landType, elevationName[elevation], feature};
            texture = makePixMap(l);
        }else {

            if (wonder.equals("")) {
                texture = textures[getImageId()];
            } else {
                texture = BC;
            }
        }
    }

    private Texture makePixMap(String[] list){
        String s = "";
        for (String str: list){
            s += str;
        }
        if (s.equals("")){
            return BC;
        }

        if (!wonder.equals("")){
            Pixmap p = new Pixmap(Gdx.files.internal(GameProject.fileName + "Hex/256/Tiles/"+landType+".png"));
            Pixmap wdr = new Pixmap(Gdx.files.internal(GameProject.fileName + "Hex/256/Wonders/" + wonder + ".png"));
            p.drawPixmap(wdr, 0, 0);
            return new Texture(p);
        }

        if (generatedHexes.containsKey(s)){
            return (Texture)generatedHexes.get(s);
        }

        Pixmap p = new Pixmap(Gdx.files.internal(GameProject.fileName + "Hex/256/Tiles/"+landType+".png"));
        if (!feature.equals(""))
            p.drawPixmap(new Pixmap(Gdx.files.internal(GameProject.fileName + "Hex/256/Tiles/"+feature+".png")), 0, 0);
        if (elevation > 1)
            p.drawPixmap(new Pixmap(Gdx.files.internal(GameProject.fileName + "Hex/256/Tiles/"+elevationName[elevation]+".png")), 0, 0);
        Texture t = new Texture(p);
        generatedHexes.put(s, t);

        return t;
    }

    /**
     * Returns the image id of the hex using a bunch of switches
     * @return  integer of the location in the array which the image is stored in
     */
    private int getImageId(){
        switch (landType){
            case "Ocean":{                                  //ocean
                if (feature.equals("Ice"))    //ice
                    return 3;
                else
                    return 1;               //ocean
            }case "Shore":{                                 //shore/lake
                switch (feature){
                    case "":
                        return 4;           //shore/lake
                    case "Atoll":
                        return 5;           //atoll
                    case "Ice":
                        return 6;           //ice
                    default:
                        return 4;
                }
            }case "Lake":{
                return 1;
            }case "Desert":{                                 //desert
                switch (elevation){
                    case 3:   //mountain
                        return 9;          //desert mountain
                    case 1:{  //flat land
                        switch (feature){
                            case "":
                                return 7;    //desert
                            case "FloodPlains"://'c':
                                return 12;   //flood plains
                            case "Oasis":
                                return 11;   //oasis
                            default:
                                return 7;
                        }
                    }
                    case 2:{  //hill
                        return 8;          //desert hills
                    }
                }
            }case "Grassland":{                                 //grassland
                switch (elevation){
                    case 3:   //mountain
                        return 15;           //grassland mountain
                    case 1:{  //flat land
                        switch (feature){
                            case "":
                                return 13;   //grassland
                            case "Forest":
                                return 19;   //forest
                            case "Jungle":
                                return 20;   //jungle
                            case "Marsh":
                                return 21;   //marsh
                            default:
                                return 13;
                        }
                    }
                    case 2:{  //hill
                        switch (feature){
                            case "":
                                return 14;   //grassland hill
                            case "Forest":
                                return 18;   //forest
                            case "Jungle":
                                return 19;   //jungle
                            default:
                                return 14;
                        }
                    }
                }
            }case "Plains":{                                 //plains
                switch (elevation){
                    case 3:   //mountain
                        return 24;           //plains mountain
                    case 1:{  //flat land
                        switch (feature){
                            case "":
                                return 22;   //plains
                            case "Forest":
                                return 26;   //forest
                            default:
                                return 22;
                        }
                    }
                    case 2:{  //hill
                        switch (feature){
                            case "":
                                return 23;   //plains hill
                            case "Forest":
                                return 27;   //forest
                            default:
                                return 23;
                        }
                    }
                }
            }case "Snow":{                                 //snow
                switch (elevation){
                    case 1:{  //flat land
                        return 28;
                    }case 2:{ //hill
                        return 29;
                    }case 3:{ //mountain
                        return 30;
                    }
                }
            }case "Tundra":{                                 //tundra
                switch (elevation){
                    case 1:{  //flat land
                        if (feature.equals("Forest"))
                            return 35;      //forest
                        else
                            return 31;      //tundra
                    }case 2:{ //hill
                        if (feature.equals("Forest"))
                            return 36;      //forest
                        else
                            return 32;      //tundra hill
                    }case 3:  //mountain
                        return 33;          //tundra mountain
                }
            }case "Special":{
                return 37;
            }
            default: {
                System.out.println(pos.x + " " + pos.y + " " + id);
                return 0;
            }
        }
    }

    public void addRiver(String where){
        Pixmap p;
        if (wonder.equals(""))
            p = new Pixmap(Gdx.files.internal(hexNames[getImageId()]));
        else
            p = new Pixmap(Gdx.files.internal(GameProject.fileName + "Hex/256/Wonders/" + wonder + ".png"));
        river = where;
        for (int i = 0; i < where.length(); i++){
            if (where.charAt(i) == '1'){
                p.drawPixmap(rivers[i], 0, 0);
            }
        }
        texture = new Texture(p);
    }

    public float[] getTerrainGains(){return terrainGains;}
    public String getWonder(){return wonder;}
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
