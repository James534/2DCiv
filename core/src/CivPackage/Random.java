package CivPackage;

import CivPackage.Models.Hex;
import CivPackage.Util.Capsule;
import CivPackage.Util.DebugClass;
import CivPackage.Util.MathCalc;
import CivPackage.Util.Point;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by Lu on 2014-07-12.
 */
public class Random {

    private int seed;
    private java.util.Random random;
    public Random(int seed){
        this.seed = seed;
        random = new java.util.Random(seed);
    }

    Array<Point> riverPoints;
    Array<Point> startingPoints;

    /**
     * Generates a hex map
     * @param xSize
     * @param ySize
     * @return
     */
    public Hex[][] generateHexMap(int xSize, int ySize, Array<Capsule> sp){

        /**
         * From MapGenerator.lua that civ5 is actually using, the generating in order is:
         *      -plot types (land or sea, flatland, hill, mountains)        (yes)
         *      -terrain/climate (grassland, plains, desert, tundra, snow)  (yes)
         *      -rivers and then lakes                                      (reversed; lakes first, not implemented in the hexes in this step)
         *      -features (forest, jungle, etc)                             (need to improve)
         *      -starting plots (starting points > wonders > resources)     (2/3)
         *      -goodies (???)                                              (no?)
         */

        Hex[][] map = new Hex[xSize][ySize];

        for (int y = 0; y < ySize; y++){
            for (int x = 0; x < xSize; x++){
                map[y][x] = new Hex(x, y);
            }
        }

        generatePlotTypes(map);         //generates the plot map
        generateTerrain(map);                               //generates the terrain
        generateWaterBodies(map);
        generateFeatures(map);
        generateStartPlots(map, sp);
        generateWonders(map);
        generateResources(map);

        for (int y = 0; y < ySize; y++){
            for (int x = 0; x < xSize; x++){
                map[y][x].update();
            }
        }
        //adding rivers
        for (Point p: riverPoints){
            map[p.y][p.x].addRiver(p.data);
        }
        //adding resources


        //debug stuff
        DebugClass.startingPoints = startingPoints;

        return map;
    }

    private void generateResources(final Hex[][] map){
        //http://www.reddit.com/r/civ/comments/2a37ws/effects_of_setting_resources_to_abundant_or_sparse/
        //temp class to hold methods only this function will call
        class temp{

        }

        temp t = new temp();
        for (Point p: startingPoints){
            if (!onIsland(map, p, 40)){
                //if the area is less than 40, give that civ more resources
                int area = calculateArea(map, p);
                DebugClass.generateLog("Starting Point| " + p.x + " " + p.y + " with area| " + area+" given aid");
            }
        }
    }

    /**
     * Returns True if the land mass containing _p_ is greater than the area specified
     * @param p
     * @param area
     * @return
     */
    private boolean onIsland (Hex[][] map, Point p, int area){
        int landArea = 1;
        LinkedList<Point> li = new LinkedList<>();
        LinkedList<Hex> history = new LinkedList<>();
        li.push(p);
        history.push(map[p.y][p.x]);
        //BFS search from the point P, until there is no more land to be found
        //if the landmass area is less than specified, return false
        Point t;
        while (li.size() > 0){
            t = li.pop();
            for (Point neighbour: getNeighbours(t.x, t.y)){
                if (map[neighbour.y][neighbour.x].elevation > 0 &&              //check if the tile is not water
                        !history.contains(map[neighbour.y][neighbour.x])){      //and if it has been visited yet
                    li.push(neighbour);
                    history.push(map[neighbour.y][neighbour.x]);
                    landArea++;
                }
            }
            if (landArea > area)
                return true;
        }

        return false;
    }

    /**
     * Calculates the area of the island _p_ is on
     * Similar to onIsland, except it calculates the entire area instead of exiting early if the condition has been reached
     * Use this to calculate area of small island to give offset balance
     * @param p
     * @return
     */
    private int calculateArea(Hex[][] map, Point p){
        int area = 0;
        LinkedList<Point> li = new LinkedList<>();
        LinkedList<Hex> history = new LinkedList<>();
        li.push(p);
        history.push(map[p.y][p.x]);
        Point t;
        while (li.size() > 0) {
            t = li.pop();
            for (Point neighbour: getNeighbours(t.x, t.y)){
                if (map[neighbour.y][neighbour.x].elevation > 0 &&              //check if the tile is not water
                        !history.contains(map[neighbour.y][neighbour.x])){      //and if it has been visited yet
                    li.push(neighbour);
                    history.push(map[neighbour.y][neighbour.x]);
                    area++;
                }
            }
        }

        return area;
    }

    private Array<Hex> getIslandTiles(Hex[][] map, Point p){
        LinkedList<Point> li = new LinkedList<>();
        Array<Hex> island = new Array<>();
        li.push(p);
        island.add(map[p.y][p.x]);
        Point t;
        while (li.size() > 0) {
            t = li.pop();
            for (Point neighbour: getNeighbours(t.x, t.y)){
                if (map[neighbour.y][neighbour.x].elevation > 0 &&              //check if the tile is not water
                        !island.contains(map[neighbour.y][neighbour.x], true)){      //and if it has been visited yet
                    li.push(neighbour);
                    island.add(map[neighbour.y][neighbour.x]);
                }
            }
        }
        return island;
    }

    private void generateWonders(Hex[][] map){
        /*
        -1  BC   -Barringer Crater: Must be in tundra or desert; cannot be adjacent to grassland;
                    can be adjacent to a maximum of 2 mountains and a maximum of 4 hills and mountains; avoids oceans; becomes mountain
        -2  Fuji -Mt. Fuji: Must be in grass or plains; cannot be adjacent to tundra, desert, marsh, or mountains;
                    can be adjacent to a maximum of 2 hills; avoids oceans and the biggest landmass; becomes grassland and mountain
        -3  Mesa -Grand Mesa: Must be in plains, desert, or tundra, and must be adjacent to at least 2 hills;
                    cannot be adjacent to grass; can be adjacent to a maximum of 2 mountains; avoids oceans; becomes mountain
        -4  GBR  -Great Barrier Reef: Specifics currently unknown (needs more scrutiny, as the XML file just says "EligibilityMethod" and "TileChangesMethodNumber" as 1;
                    However, by my observations, it takes 2 tiles within shallow waters near the coast)
        -5  Krak -Krakatoa: Must spawn in the ocean next to at least 1 shallow water tile;
                    cannot be adjacent to ice; changes tiles around it to shallow water; becomes grassland and mountain
        -6  RoG  -Rock of Gibraltar: Specifics currently unknown (like the Great Barrier Reef, it has "EligibilityMethod" and "TileChangesMethodNumber" as 2;
                    However, from my observations it appears in shallow waters near the coast)
        -7  OF   -Old Faithful: Must be adjacent to at least 3 hills and mountains;
                    cannot be adjacent to more than 4 mountains, and cannot be adjacent to more than 3 desert or 3 tundra tiles; avoids oceans; becomes mountain
        -8  CdP  -Cerro de Potosi: Must be adjacent to at least 1 hill;
                    avoids oceans; becomes mountain
        -9  ED   -El Dorado: Must be next to at least 1 jungle tile;
                    avoids oceans; becomes flatland plains
        -10 FoY  -Fountain of Youth: Avoids oceans; becomes flatland plains
        -11 SP   -Sri Pada: Must be in a grass or plains;
                    cannot be adjacent to desert, tundra, or marshes; can be adjacent to a maximum of 2 mountain tiles; avoids oceans and the biggest landmass; becomes mountain
        -12 MSin -Mt. Sinai: Must be in plains or desert, and must be adjacent to a minimum of 3 desert tiles;
                    cannot be adjacent to tundra, marshes, or grassland; avoids oceans; becomes mountain
        -13 Kail -Mt. Kailash: Must be in plains or grassland, and must be adjacent to at least 4 hills and/or mountains;
                    cannot be adjacent to marshes; can be adjacent to a maximum of 1 desert tile; avoids oceans; becomes mountain
        -14 Ulu  -Uluru: Must be in plains or desert, and must be adjacent to a minimum of 3 plains tiles;
                    cannot be adjacent to grassland, tundra, or marshes; avoids oceans; becomes mountain
        -15 Vic  -Lake Victoria: Avoids oceans; becomes flatland plains
        -16 Kili -Mt. Kilimanjaro: Must be in plains or grassland, and must be adjacent to at least 2 hills;
                    cannot be adjacent to more than 2 mountains; avoids oceans; becomes mountain
        -17 MoS  -Mines of Solomon: Cannot be adjacent to more than 2 mountains;
                    avoids oceans; becomes flatland plains*/
        //http://gaming.stackexchange.com/questions/95095/do-natural-wonders-spawn-more-closely-to-city-states


        //should check to see if land is big enough, but to be implemented later

        //64*64 ~ 80*52, which is standard size; 4 wonders
        String[] wonders = {"BC", "Fuji", "Mesa", "GBR", "Krak", "RoG", "OF", "CdP", "ED", "FoY", "SP", "MSin", "Kail", "Ulu", "Vic", "Kili", "MoS"};

        HashMap hm = new HashMap();

        /**
         * I put the landtypes and filters in an temp "capsule" Object[], and put that in the hash map
         * Hash map is used so i dont have to repeat code for every different wonder
         * I just get the land types each wonder looks for, and what to filter out
         * Then i use the same code for each wonder and generate them
         */
        //have to make new arrays for each wonder info because i cant do arrayName = {Stuff1, Stuff2} on already declared arrays
        //they are going to be all dereferenced eventually, so it wont take that much more memory

        //------------------ DATA ------------------------------------
        //-4  GBR  -Great Barrier Reef: Specifics currently unknown (needs more scrutiny, as the XML file just says "EligibilityMethod" and "TileChangesMethodNumber" as 1;
        // However, by my observations, it takes 2 tiles within shallow waters near the coast)

        //-6  RoG  -Rock of Gibraltar: Specifics currently unknown (like the Great Barrier Reef, it has "EligibilityMethod" and "TileChangesMethodNumber" as 2;
        // However, from my observations it appears in shallow waters near the coast)



        //-1  BC   -Barringer Crater: Must be in tundra or desert; cannot be adjacent to grassland;
        //can be adjacent to a maximum of 2 mountains and a maximum of 4 hills and mountains; avoids oceans; becomes mountain
        String[] BCLand = {"Tundra", "Desert"};       //since java dosnt allow array initalization in method calls
        Capsule[] BCFilter = {new Capsule("Grassland", 0), new Capsule("Water", 0), new Capsule("Mountains", 2), new Capsule("Hills", 2)};
        Object[] BCCap = {BCLand, BCFilter};
        hm.put("BC", BCCap);

        //-2  Fuji -Mt. Fuji: Must be in grass or plains; cannot be adjacent to tundra, desert, marsh, or mountains;
        //can be adjacent to a maximum of 2 hills; avoids oceans and the biggest landmass; becomes grassland and mountain
        String[] FujiLand = {"Grassland", "Plains"};
        Capsule[] FujiFilter = {new Capsule("Tundra", 0), new Capsule("Desert", 0), new Capsule("fMarsh", 0),
                new Capsule("Water", 0), new Capsule("Mountains", 0), new Capsule("Hills", 2)};
        Object[] FujiCap = {FujiLand, FujiFilter};
        hm.put("Fuji", FujiCap);

        //-3  Mesa -Grand Mesa: Must be in plains, desert, or tundra, and must be adjacent to at least 2 hills;
        //cannot be adjacent to grass; can be adjacent to a maximum of 2 mountains; avoids oceans; becomes mountain
        String[] MesaLand = {"Plains", "Desert", "Tundra"};
        Capsule[] MesaFilter = {new Capsule("Hills", -2), new Capsule("Grassland", 0), new Capsule("Mountain", 2), new Capsule("Water", 0)};
        Object[] MesaCap = {MesaLand, MesaFilter};
        hm.put("Mesa", MesaCap);

        //-5  Krak -Krakatoa: Must spawn in the ocean next to at least 1 shallow water tile;
        //cannot be adjacent to ice; changes tiles around it to shallow water; becomes grassland and mountain
        String[] KrakLand = {"Shore"};
        Capsule[] KrakFilter = {new Capsule("fIce", 0)};
        Object[] KrakCap = {KrakLand, KrakFilter};
        hm.put("Krak", KrakCap);

        //-7  OF   -Old Faithful: Must be adjacent to at least 3 hills and mountains;
        //cannot be adjacent to more than 4 mountains, and cannot be adjacent to more than 3 desert or 3 tundra tiles; avoids oceans; becomes mountain
        String[] OFLand = {"Grassland", "Plains", "Desert", "Tundra"};
        //MODIFIED: cannot be around 4+ mountains, has to be near 2 hills and a mountain
        Capsule[] OFFilter = {new Capsule("Mountain", 4), new Capsule("Hills", -2), new Capsule("Mountain", -1),
                new Capsule("Desert", 3), new Capsule("Tundra", 3), new Capsule("Water", 0)};
        Object[] OFCap = {OFLand, OFFilter};
        hm.put("OF", OFCap);

        //-8  CdP  -Cerro de Potosi: Must be adjacent to at least 1 hill;
        //avoids oceans; becomes mountain
        String[] CdPLand = {"Grassland", "Plains", "Desert", "Tundra", "Snow"};
        Capsule[] CdPFilter = {new Capsule("Hills", -1), new Capsule("Water", 0)};
        Object[] CdPCap = {CdPLand, CdPFilter};
        hm.put("CdP", CdPCap);

        //-9  ED   -El Dorado: Must be next to at least 1 jungle tile;
        //avoids oceans; becomes flatland plains
        String[] EDLand = {"Grassland", "Plains", "Desert", "Tundra", "Snow"};
        Capsule[] EDFilter = {new Capsule("fJungle", 1), new Capsule("Water", 0)};
        Object[] EDCap = {EDLand, EDFilter};
        hm.put("ED", EDCap);

        //-10 FoY  -Fountain of Youth: Avoids oceans; becomes flatland plains
        String[] FoYLand = {"Grassland", "Plains", "Desert", "Tundra", "Snow"};
        Capsule[] FoYFilter = {new Capsule("Water", 0)};
        Object[] FoYCap = {FoYLand, FoYFilter};
        hm.put("FoY", FoYCap);

        //-11 SP   -Sri Pada: Must be in a grass or plains;
        //cannot be adjacent to desert, tundra, or marshes; can be adjacent to a maximum of 2 mountain tiles;
        //avoids oceans and the biggest landmass; becomes mountain
        String[] SPLand = {"Grassland", "Plains"};
        Capsule[] SPFilter = {new Capsule("Desert", 0), new Capsule("Tundra", 0), new Capsule("fMarsh", 0), new Capsule("Mountain", 2)};
        Object[] SPCap = {SPLand, SPFilter};
        hm.put("SP", SPCap);

        //-12 MSin -Mt. Sinai: Must be in plains or desert, and must be adjacent to a minimum of 3 desert tiles;
        //cannot be adjacent to tundra, marshes, or grassland; avoids oceans; becomes mountain
        String[] MSinLand = {"Plains", "Desert"};
        Capsule[] MSinFilter = {new Capsule("Desert", -3), new Capsule("Tundra", 0), new Capsule("fMarsh", 0), new Capsule("Grassland", 0), new Capsule("Water",0)};
        Object[] MSinCap = {MSinLand, MSinFilter};
        hm.put("MSin", MSinCap);

        //-13 Kail -Mt. Kailash: Must be in plains or grassland, and must be adjacent to at least 4 hills and/or mountains;
        //cannot be adjacent to marshes; can be adjacent to a maximum of 1 desert tile; avoids oceans; becomes mountain
        String[] KailLand = {"Plains", "Grassland"};
        Capsule[] KailFilter = {new Capsule("Hills", -2), new Capsule("Mountain", -2), new Capsule("fMarsh", 0), new Capsule("Desert", 1), new Capsule("Water", 0)};
        Object[] KailCap = {KailLand, KailFilter};
        hm.put("Kail", KailCap);

        //-14 Ulu  -Uluru: Must be in plains or desert, and must be adjacent to a minimum of 3 plains tiles;
        //cannot be adjacent to grassland, tundra, or marshes; avoids oceans; becomes mountain
        String[] UluLand = {"Plains", "Desert"};
        Capsule[] UluFilter = {new Capsule("Plains", -3), new Capsule("Grassland", 0), new Capsule("Tundra", 0), new Capsule("fMarsh", 0), new Capsule("Water", 0)};
        Object[] UluCap = {UluLand, UluFilter};
        hm.put("Ulu", UluCap);

        //-15 Vic  -Lake Victoria: Avoids oceans; becomes flatland plains
        String[] VicLand = {"Grassland", "Plains", "Desert", "Tundra", "Snow"};
        Capsule[] VicFilter = {new Capsule("Water", 0)};
        Object[] VicCap = {VicLand, VicFilter};
        hm.put("Vic", VicCap);

        //-16 Kili -Mt. Kilimanjaro: Must be in plains or grassland, and must be adjacent to at least 2 hills;
        //cannot be adjacent to more than 2 mountains; avoids oceans; becomes mountain
        String[] KiliLand = {"Plains", "Grassland"};
        Capsule[] KiliFilter = {new Capsule("Hills", -2), new Capsule("Mountain", 2), new Capsule("Water", 0)};
        Object[] KiliCap = {KiliLand,KiliFilter};
        hm.put("Kili", KiliCap);

        //-17 MoS  -Mines of Solomon: Cannot be adjacent to more than 2 mountains;
        //avoids oceans; becomes flatland plains
        String[] MoSLand = {"Grassland", "Plains", "Desert", "Tundra", "Snow"};
        Capsule[] MosFilter = {new Capsule("Mountain", 2), new Capsule("Water", 0)};
        Object[] MoSCap = {MoSLand, MosFilter};
        hm.put("MoS", MoSCap);

        //-------------------- END DATA -------------------------------------

        Array<String> wonderList = new Array<>(wonders);
        Array<Hex> generatedWonders = new Array<>();
        int counter = 0;
        int maxSize = 4;
        while (counter < maxSize){
            int n = random.nextInt(wonderList.size);
            String wonderName = wonderList.get(n);
            wonderList.removeIndex(n);
            DebugClass.generateLog("Generating: " + wonderName);
            Object[] tempCapsule = (Object[]) hm.get(wonderName);
            if (tempCapsule == null)
                continue;      //so it dosnt crash when it tries to generate a wonder i havent implemented
            String[] landToLookFor = (String[]) tempCapsule[0];
            Capsule[] toFilter = (Capsule[]) tempCapsule[1];
            Array<Hex> list = filterLand(map, getLandTypes(map, landToLookFor), toFilter);
            Array<Hex> validPositions = new Array<>();
            for (Hex h : list) {                    //checks distance between possible point
                checkList:
                {
                    if (!h.getWonder().equals("")) //if there is already a wonder at that point
                        continue;                  //skip this tile
                    for (Point p : startingPoints) {    //and every starting point
                        /*not sure if wonders can be beside each other, assume so for now*/
                        if (MathCalc.distanceBetween(p.x, p.y, h.getMapX(), h.getMapY()) < 6) { //if they are not enough apart
                            break checkList;                                                    //this hex is not a valid position
                        }
                    }
                    for (Hex wonderLoc: generatedWonders){        //checks aginst every other wonder
                        if (MathCalc.distanceBetween(h.getMapX(), h.getMapY(), wonderLoc.getMapX(), wonderLoc.getMapY()) < 6){
                            break checkList;
                        }
                    }
                    validPositions.add(h);
                }
            }

            if (validPositions.size > 0) {
                Hex h = validPositions.get(random.nextInt(validPositions.size));
                map[h.getMapY()][h.getMapX()].makeWonder(wonderName);    //make a wonder there
                generatedWonders.add(h);
                counter++;
                DebugClass.generateLog(wonderName + " generated at: " + h.getMapX() + " " + h.getMapY());

                //post generation processing
                if (wonderName.equals("Krak")){
                    for (Point p: getNeighbours(h.getMapX(), h.getMapY())){
                        map[p.y][p.x].landType = "Shore";
                    }
                }

            } else {
                DebugClass.generateLog("Failed to generate " + wonderName);
            }
        }
    }

    /**
     * Filters the _list_ by checking the map for what to filter out in the _filters_ array
     * String of capsule = what to look for (landtype)
     * int of capsule = how many of them max can exist around the tile(0-5)
     * @param map
     * @param list
     * @param filters       check for elevation: "Hills", "Mouintains", "Water";
     *                      check for features: string start with f;
     *                      check for landtype: normal landtype string
     * @return
     */
    private Array<Hex> filterLand(Hex[][] map, Array<Point> list, Capsule[] filters){
        Array<Hex> l = new Array<>();

        class innerMethod{  //hack to use functions in a function, shortens code
            /**
             * Returns True if this hex' neighbours' elevation are less than max.
             * Note, if max < 0, then it check if they are more than max (ie, mininium of 2 hills)
             * @param map
             * @param p            = point to check neighbours around
             * @param level        = elevation
             * @param max          = max occurrence (1-5)
             * @return
             */
            public boolean checkNeighbourHeight(Hex[][] map, Point p, int level, int max){
                //p = point to check, level = elevation, max = max occurrence
                int count = 0;
                for (Point n : getNeighbours(p.x, p.y))
                    if (map[n.y][n.x].elevation == level)
                        count++;
                if (max >= 0)
                    return (count <= max);      //bigger than max = false, smaller = true
                else {
                    return (count >= Math.abs(max));
                }
            }
        }
        innerMethod im = new innerMethod();
        HashMap hm = new HashMap(); //used to check for elevation, shortens code
        hm.put("Hills", 2);
        hm.put("Mountains", 3);
        hm.put("Water", 0);

        for (Point p: list){                    //goes through each point
            validPoint:{                 //if this point fails a check, skip all other filter checks
                for (Capsule c: filters) {          //checks each filter
                    if (c.s.equals("Hills") || c.s.equals("Mountains") || c.s.equals("Water")) {
                        //checks for elevation
                        if (!im.checkNeighbourHeight(map, p, (int)hm.get(c.s), c.n))
                            break validPoint;
                    }else if(c.s.charAt(0) == 'f') {        //checks for features
                        int count = 0;
                        String s = c.s.substring(1);        //have to substring, since all features start with f
                        for (Point n: getNeighbours(p.x, p.y)) {
                            if (map[n.y][n.x].feature.equals(s))
                                count++;
                        }
                        if (count > c.n)
                            break validPoint;
                    }else{  //if it dosnt check for elevation or features, then it checks for landtypes
                        int count = 0;
                        for (Point n: getNeighbours(p.x, p.y)) {         //checks each neighbour
                            if (map[n.y][n.x].landType.equals(c.s))     //if their landtype equals to what im looking for
                                count++;
                        }
                        if (count > c.n)                            //if theres more than allowed, break all filter checks for this point
                            break validPoint;
                    }
                }
                //if it reached this point, then its a valid point
                l.add(map[p.y][p.x]);
            }
        }

        return l;
    }

    /**
     * Returns a list of points indicating where the landtypes of _land_ are found
     * EDIT: Can check for features by putting 'f' in front of the string
     * @param map
     * @param land      = list of land types to check for
     * @return
     */
    private Array<Point> getLandTypes(Hex[][] map, String[] land){
        Array<Point> l = new Array<>();
        for (int y = 0; y < map.length; y++){
            for (int x = 0; x < map[0].length; x++) {
                for (String s: land) {
                    if (s.charAt(0) == 'f'){        //checks for features too
                        if (map[y][x].feature.equals(s.substring(1))) {
                            l.add(new Point(x, y));
                            break;
                        }
                    }
                    else if (map[y][x].landType.equals(s)) {
                        l.add(new Point(x, y));
                        break;
                    }
                }
            }
        }
        return l;
    }

    private void generateStartPlots(Hex[][] map, Array<Capsule> sp){
        DebugClass.generateLog("Generate Starting Points");
        startingPoints = new Array<>();
        for (Capsule c: sp){
            switch (c.s){
                case "Shore":{
                    Array<Point> shore = getShore(map);
                    //System.out.println (shore.size);
                    for (int i = 0; i < c.n; i++){
                        if (shore.size == 0){
                            DebugClass.generateLog("Failed to generate all starting points");
                            break;
                        }
                        Point t = null;
                        while (shore.size > 0) {
                            int n = random.nextInt(shore.size);
                            t = shore.removeIndex(n);
                            if (onIsland(map, t, 20)){     //use this spot if the area is > 20
                                startingPoints.add(t);
                                break;
                            }
                            //map[t.x][t.y].landType = "Special";
                        }

                        //remove neighbour tiles from the list
                        Array<Point> toRemove = new Array<>();
                        for (Point p: shore){
                            if (MathCalc.distanceBetween(p.x, p.y, t.x, t.y) < 5){
                                toRemove.add(p);
                            }
                        }
                        shore.removeAll(toRemove, true);
                    }

                }
            }
        }
    }

    private Array<Point> getShore(Hex[][] map){
        //gets the shoreline
        Array<Point> shore = new Array<>();
        for (int y = 0; y < map.length; y++){
            for (int x = 0; x < map[0].length; x++){
                if (map[y][x].elevation == 1){      //only flat land
                    boolean isShore = false, isLand = false;
                    //map[y][x].landType = "Special";
                    for (Point p : getNeighbours(x, y)) {
                        if (existsAt(map, p.x, p.y)) {
                            if (map[p.y][p.x].landType.equals("Shore"))
                                isShore = true;
                            else if (map[p.y][p.x].elevation != 0)
                                isLand = true;
                            if (isLand && isShore)      //adds the tile to the shore list iff there is shore and land around it
                                shore.add(new Point(x, y));
                            //map[y][x].landType = "Tundra";
                        }
                    }
                }
            }
        }
        return shore;
    }

    /**
     * Generates the terrain using diamond-square
     */
    public void generatePlotTypes(Hex[][] hexMap){
        int xSize = hexMap[0].length;
        int ySize = hexMap.length;
        float[][] map = diamondSquare(xSize, ySize, 32, 4f, true);    //generates the map

        float[][][] mapGen = new float[5][ySize][xSize];
        int size = 64;
        float variation = 16;
        for (int i = 0; i < mapGen.length; i++){
            mapGen[i] = diamondSquare(xSize, ySize, size, variation, false);
            size /= 2;
            variation /= 1.3;
            for (int y = 0; y < mapGen[0].length; y++){
                for (int x = 0; x < mapGen[0][0].length; x++){
                    if (i%2 == 0) {
                        map[y][x] += mapGen[i][y][x];
                    }else{
                        map[y][x] += mapGen[i][mapGen[0].length-1-y][x];
                    }
                }
            }
        }

        map = gradient(map);
        map = zScores(map);                             //converts the map to a z score

        //Landmass Codes:
        //49 is deep ocean, 50 is light ocean, 97 is land, 98 is hill, 99 is mountain
        int[][] finalMap = new int[ySize][xSize];
        for (int Y = 0; Y < map.length; Y++){
            for (int X = 0; X < map[0].length; X++){
                if (map[Y][X] < 0.5){
                    finalMap[Y][X] = 49;     //deep ocean, light ocean is 50, to be added in fixLand
                }else if (map[Y][X] < 1.4){
                    finalMap[Y][X] = 97;     //land
                }else if (map[Y][X] < 1.6){
                    finalMap[Y][X] = 98;     //hills
                }else if (map[Y][X] < 1.8){
                    finalMap[Y][X] = 97;     //land
                }else if (map[Y][X] < 2.5){
                    finalMap[Y][X] = 98;     //hills
                }else{
                    finalMap[Y][X] = 99;     //mountains
                }
            }
        }

        finalMap = smoothLand(finalMap);      //smooths out the land, gets rid of some patchy tiles
        finalMap = smoothLand(finalMap);
        Array<Integer> ocean = new Array<Integer>();    //a list of the ocean tiles, for the smooth method to check if to ignore or not
        ocean.add (49);
        smooth(finalMap, 1, 2, ocean);                       //helps smooth out the deep ocean tiles

        //changed to work with the new hex format, can be made more efficient
        for (int y = 0; y < map.length; y++){
            for (int x = 0; x < map.length; x++){
                if (finalMap[y][x] == 49) {
                    hexMap[y][x].landType = "Ocean";
                    hexMap[y][x].elevation = 0;
                }else if (finalMap[y][x] == 50){
                    hexMap[y][x].landType = "Shore";
                    hexMap[y][x].elevation = 0;
                }else if (finalMap[y][x] == 97) {
                    hexMap[y][x].elevation = 1;
                }else if (finalMap[y][x] == 98) {
                    hexMap[y][x].elevation = 2;
                }else if (finalMap[y][x] == 99) {
                    hexMap[y][x].elevation = 3;
                }
            }
        }
    }

    private boolean isLand (Hex[][]map, int x, int y){
        try{
            return map[y][x].elevation != 0;
        }catch (IndexOutOfBoundsException e){
            return false;
        }
    }
    /**
     * Generates the terrain of the map <br>
     *     Terrains are: ocean, shore, grassland, plains, desert, tundra, snow
     * @param map
     * @return a string map with all the terrain generated on it
     */
    private void generateTerrain(Hex[][] map){

        Array<Point> adj;
        /** Generates coastal tiles for ocean tiles that are touching land*/
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                if (map[y][x].landType.equals("Ocean")){                //if its ocean
                    findShore:
                    {
                        if (next() > 0.2) {             //randomly get the neighbours in 1 or 2 range
                            adj = getPointInRange(x, y, 2);
                        } else {
                            adj = getNeighbours(x, y);
                        }
                        for (Point p : adj) {                 //checks the neighbours
                            if (isLand (map, p.x, p.y))         {//if there's a land tile
                                map[y][x].landType = "Shore";              //changes this tile to light ocean
                                break findShore;
                            }
                        }
                        //map[y][x].landType = "Ocean";
                    }
                }
            }
        }

        //deletes the single deep ocean tiles leftover and replaces it with whatever occurs the most around the tile
        int max;
        int counter;
        char lastTerrain, mostOccurredTerrain;
        Array<Character> terrains = new Array<Character>();     //the different types of terrain
        for (int y = 0; y < map.length; y++){
            for (int x = 0; x < map[0].length; x++){
                if (map[y][x].landType.equals("Ocean")) {                      //only does this action on ocean tiles
                    mostOccurredTerrain = '0';
                    max = 0;
                    counter = 0;
                    terrains.clear();
                    for (Point p : getNeighbours(x, y)) {
                        if (existsAt(map, p.x, p.y)) {
                            if (map[p.y][p.x].elevation == 0){       //if its an water tile
                                //System.out.println(map[p.y][p.x] + " OCCEAN PLZ");
                                if (map[p.y][p.x].landType.equals("Ocean"))
                                    terrains.add('o');          //cant use a/b, since you cant tell ocean/shore from land/hill
                                else
                                    terrains.add('s');          //adds o for ocean and s for shore
                            }else                                   //if its still a land tile, just add whatever the land tile is
                                terrains.add('l');
                        }
                    }
                    terrains.sort();
                    if (!terrains.contains('o', true)){             //if there is no ocean tiles nearby
                        lastTerrain = terrains.get(0);              //change it to a different tile
                        //System.out.println(map[y][x]);
                        for (int n = 0; n < terrains.size; n++){
                            if (terrains.get(n) != lastTerrain){
                                if (counter > max){
                                    max = counter;
                                    mostOccurredTerrain = lastTerrain;
                                    DebugClass.generateLog("LAST TERRAIN " + lastTerrain);
                                    counter = 0;
                                }
                            }else{
                                counter++;
                            }
                        }
                        if (mostOccurredTerrain == 's') {//if the shore is most common
                            map[y][x].elevation = 0;
                            map[y][x].landType = "Shore";
                        }
                        else {                          //if not, just change it to the normal elevation
                            map[y][x].elevation = 1;    //normal elevation
                            //System.out.println("SINGLE TILE " + mostOccurredTerrain);
                        }
                    }
                }
            }
        }

        /*--------------------------------------- Generating noise -----------------------------------------*/
        float[][] climateMap = diamondSquare(map[0].length, map.length, 8, 8f, false);
        float[][][]mapGen = new float[5][map.length][map[0].length];                       //generates more noise, to "average" it out
        int size = 64;
        float variation = 2;
        for (int i = 0; i < mapGen.length; i++){
            mapGen[i] = diamondSquare(map[0].length, map.length, size, variation, false);
            size /= 2;
            variation *= 1.3;
            for (int y = 0; y < mapGen[0].length; y++){
                for (int x = 0; x < mapGen[0][0].length; x++){
                    if (i%2 == 0) {
                        climateMap[y][x] += mapGen[i][y][x];
                    }else{
                        climateMap[y][x] += mapGen[i][mapGen[0].length-1-y][x];             //flips the y axis every other run, to help get rid of artifacts
                    }
                }
            }
        }
        /* --------------------------------- Converting noise to terrain ----------------------------------*/
        //Array<Point> shore = new Array<Point>();    //list of all the shallow ocean tiles with land next to them, used in river generation
        climateMap = zScores(climateMap);   //convert it to z score again
        int[][] intClimateMap = new int[map.length][map[0].length];
        int middle = map.length/2;
        float temp;
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                temp = Math.abs(middle - y);                //how far away this tile is from the middle
                temp /= middle;
                //System.out.println(map[y][x].charAt(0));
                if (map[y][x].elevation != 0){                         //if its not an ocean tile, do stuff to it
                    //whittaker diagram; http://pcg.wikidot.com/pcg-algorithm:whittaker-diagram
                    //adaptation http://www-cs-students.stanford.edu/~amitp/game-programming/polygon-map-generation/
                    //using y as temperature, hottest in the middle of the map, and the z scores as precipitation

                    if (temp < .25){
                        if (climateMap[y][x] < -0.25) {
                            intClimateMap[y][x] = 30;       //high temp, low rain, desert
                        }else if (climateMap[y][x] < 0.25){
                            intClimateMap[y][x] = 10;       //high temp, med rain, plains/grasslands
                        }else{
                            intClimateMap[y][x] = 20;       //high temp, high rain, forest/jungle, but grasslands for now
                        }
                    }else if (temp < 0.45){
                        if (climateMap[y][x] < -0.25){
                            intClimateMap[y][x] = 20;       //med temp, low rain, grassland
                        }else if (climateMap[y][x] < 0.25){
                            intClimateMap[y][x] = 20;       //med temp, med rain, grassland/forest
                        }else{
                            intClimateMap[y][x] = 10;
                        }
                    }else if (temp < 0.7){
                        if (climateMap[y][x] < -0.25){
                            intClimateMap[y][x] = 30;       //low temp, low rain, desert
                        }else if (climateMap[y][x] < 0.25){
                            intClimateMap[y][x] = 20;       //low temp, med rain, forest
                        }else{
                            intClimateMap[y][x] = 10;       //low temp, high rain, forest
                        }
                    }else{
                        if (climateMap[y][x] < .5) {
                            intClimateMap[y][x] = 40;       //freezing temp, low-mid rain, tundra
                        }else{
                            intClimateMap[y][x] = 50;        //freezing temp, high rain, snow
                        }
                    }
                }
            }
        }
        Array<Integer> coldTerrain = new Array<Integer>();
        coldTerrain.add(40);
        coldTerrain.add(50);
        coldTerrain.add(0);
        coldTerrain.add(-2);
        //smooths the int array
        intClimateMap = smooth(intClimateMap, 10, 2, coldTerrain);
        intClimateMap = smooth(intClimateMap, 20, 2, coldTerrain);
        intClimateMap = smooth(intClimateMap, 30, 2, coldTerrain);

        for (int y = 0; y < map.length; y++){
            for (int x = 0; x < map.length; x++){
                switch (intClimateMap[y][x]){
                    case 10:
                        map[y][x].landType = "Plains";
                        break;
                    case 20:
                        map[y][x].landType = "Grassland";
                        break;
                    case 30:
                        map[y][x].landType = "Desert";
                        break;
                    case 40:
                        map[y][x].landType = "Tundra";
                        break;
                    case 50:
                        map[y][x].landType = "Snow";
                        break;
                }
            }
        }

        /*for (int y = 0; y < map.length; y++){
            for (int x = 0; x < map[0].length; x++){
                if (map[y][x].length() < 2){
                    System.out.println(map[y][x] + " WTF");
                    map[y][x] += '0';
                }
            }
        }*/ //old debug code
    }

    private float[][] gradient(float[][] map){
        for (int x = 0; x < map[0].length; x++){
            for (int y = 0; y < map.length/9; y++) {
                map[y][x] *= y/(map.length/9) * next();
            }
            for (int y = map.length - map.length/9; y < map.length; y++){
                map[y][x] *= (map.length-y)/(map.length/9) * next();
            }
        }

        for (int y = 0; y < map.length; y++){
            for (int x = 0; x < map[0].length/8; x++){
                map[y][x] *= x/(map[0].length/8);
            }
            for (int x = map[0].length - map[0].length/8; x < map[0].length; x++){
                map[y][x] *= (x-map[0].length)/(map[0].length/8);
            }
        }

        return map;
    }



    /**
     * Generates the lakes and rivers of the map. <br>
     *     For now, the rivers will be stored in another array riverPoints
     * @param map
     * @return
     */
    private void generateWaterBodies(Hex[][] map){
        int temp;
        //gets the shoreline
        Array<Point> shore = new Array<Point>();
        for (int y = 0; y < map.length; y++){
            for (int x = 0; x < map[0].length; x++){
                if (map[y][x].landType.equals("Shore")){      //shore
                    temp = 0;
                    for (Point p : getNeighbours(x, y)) {
                        if (isLand(map, p.x, p.y)) {
                            //checks its neighbours for a land tile, if there is one, add it to the shore list
                            temp++;
                        }
                    }
                    if (temp > 2){                      //if there are more than 2 land tiles neighbouring this tile, add it to the shore lists
                        shore.add(new Point(x, y));
                    }
                }
            }
        }
        /* --------------------------- Lakes and Rivers ------------------------ */
        Array<Point> lakes = new Array<Point>();
        //checks for already existing lakes
        for (int y = 0; y < map.length; y++){
            for (int x = 0; x < map[0].length; x++){
                if (map[y][x].landType.equals("Shore")) {
                    lakes:{
                        for (Point p : getNeighbours(x, y)) {
                            if (!existsAt(map, p.x, p.y) || map[p.y][p.x].landType.equals("Shore")){
                                break lakes;
                            }
                        }
                        lakes.add(new Point (x, y));
                        map[y][x].landType = "Lake";      //changes the tile to be a lake tile
                        map[y][x].elevation = 0;
                        DebugClass.generateLog("Already existing lake at " + x + " " + y);
                    }
                }
            }
        }
        //adds new lakes
        int numLakes = 3;
        int tempX, tempY;
        while (numLakes > 0){
            tempX = Math.round(nextPosInt(map[0].length-1));
            tempY = Math.round(nextPosInt(map.length-1));
            if (map[tempY][tempX].elevation != 0){
                addLake:{
                    for (Point p : getNeighbours(tempX, tempY)) {
                        if (existsAt(map, p.x, p.y) && map[p.y][p.x].elevation == 0) {
                            //checks the neighbours and see if the are water tiles
                            break addLake;
                        }
                    }
                    for (Point p: lakes) {
                        if (MathCalc.distanceBetween(tempX, tempY, p.x, p.y) < 13){
                            break addLake;                      //dont add a lake if there is already a lake less than 13 hexes from this one
                        }
                    }
                    map[tempY][tempX].landType = "Lake";      //changes the tile to be a lake tile
                    map[tempY][tempX].elevation = 0;
                    lakes.add(new Point (tempX, tempY));
                    DebugClass.generateLog("Added lake to " + tempX + " " + tempY);
                    numLakes--;
                }
            }
        }

        //adds rivers
        int numRivers = 12;
        riverPoints = new Array<Point>();
        for (Point p: lakes){   //adds a river from each lake
            if (generateRiver(map, p.x, p.y, riverPoints, nextPosInt(5), 0) != null)
                numRivers--;
        }

        //generate extra rivers if there needs to be more
        Point tempPoint;
        int direction = 0;
        Array<Point> riverStartLocations = new Array<Point>();
        while (numRivers > 0 && shore.size > 0){
            tempX = nextPosInt(shore.size-1);
            tempPoint = shore.get(tempX);                               //gets a random shore point
            rivers:
            {
                shore.removeIndex(tempX);                               //removes this point, even if its a bad point. If it is a bad point, it wont be used again
                for (Point p : lakes) {                                 //checks if the shore is actually a lake, sometimes this happens and the lake generates 2 rivers
                    if (MathCalc.distanceBetween(tempPoint.x, tempPoint.y, p.x, p.y) < 5){   //if there is already a lake within 5 hexes of the location, don't use it
                        break rivers;
                    }
                }
                for (Point p: riverStartLocations){
                    if (MathCalc.distanceBetween(tempPoint.x, tempPoint.y, p.x, p.y) < 5){          //checks if there is another river starting within 5 hexes, if so, don't use this hex
                        break rivers;
                    }
                }
                riverStartLocations.add(tempPoint);                     //if this is a good hex, add it to the river starting locations
                for (Point p : getNeighbours(tempPoint.x, tempPoint.y)) {
                //goes through the surrounding tiles and see which direction the river should flow to flow into land
                    if (map[p.y][p.x].elevation != 0) {
                        if (p.x > tempPoint.x) {
                            if (p.y > tempPoint.y) {
                                direction = 1;
                            } else {
                                direction = 2;
                            }
                        } else {
                            if (p.y > tempPoint.y) {
                                direction = 5;
                            } else {
                                direction = 4;
                            }
                        }
                        break;
                    }
                }
                if (generateRiver(map, tempPoint.x, tempPoint.y, riverPoints, direction, 0) != null) {
                    numRivers--;
                }
            }
        }

        if (numRivers > 0){
            DebugClass.generateLog ("Failed to generate " + numRivers + " rivers");
        }

        //since the lakes were added to the string map as they were being generated, now i add the rivers
        for (Point p: riverPoints){
            if (!map[p.y][p.x].landType.equals("Lake")){   //checks if there are no lakes on the tile
                map[p.y][p.x].freshWater = true;           //adds a river
            }
        }
        /*for (int y = 0; y < map.length; y++){
            for (int x = 0; x < map[0].length; x++){
                if (map[y][x].length() == 2){   //if there are no lakes or rivers, add a 0
                    map[y][x] += '0';
                }
            }
        }*/ //legacy code
    }

    private void generateFeatures(Hex[][] map){
        /* ------------------------------ Features ---------------------------------*/
        float[][] noiseMap = generatePerlin(map[0].length, map.length);
        //float[][] noiseMap = diamondSquare(map[0].length, map.length, 32, 4, false);
        //noiseMap = zScores(noiseMap);

        float temp;
        int middle = map.length/2;
        //uses whittaker diagram again to create the features
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                temp = Math.abs(middle - y);                //how far away this tile is from the middle
                temp /= middle;
                switch (map[y][x].landType) {
                    case "Shore": {          //coast
                        if (noiseMap[y][x] > 0.95) {
                            map[y][x].feature = "Atoll";             //atoll
                        }
                        if (temp > .8){
                            if (temp > .95)
                                map[y][x].feature = "Ice";
                            else if (noiseMap[y][x] > (.7 - 10*(temp-.8)))
                                map[y][x].feature = "Ice";
                        }
                        break;
                    }
                    case "Ocean":{
                        if (temp > .8){
                            if (temp > .95)
                                map[y][x].feature = "Ice";
                            else if (noiseMap[y][x] > (.7 - 10*(temp-.8)))
                                map[y][x].feature = "Ice";
                        }
                        break;
                    }
                    case "Desert": {          //desert
                        if (noiseMap[y][x] > 0.98) {
                            map[y][x].feature = "Oasis";             //oasis
                        }
                        break;
                    }
                    case "Grassland": {          //grassland
                        if (noiseMap[y][x] > 0.5 && noiseMap[y][x] < 0.75) {
                            if (temp < 0.25) {
                                map[y][x].feature = "Jungle";         //jungle
                            } else {
                                map[y][x].feature = "Forest";         //forest
                            }
                        } else if (noiseMap[y][x] < -0.75) {
                            map[y][x].feature = "Marsh";             //marsh
                        }
                        break;
                    }
                    case "Plains": {          //plains
                        if (noiseMap[y][x] > 0.5 && noiseMap[y][x] < 0.75) {
                            map[y][x].feature = "Forest";             //forest
                        }
                        break;
                    }
                    case "Tundra": {           //tundra
                        if (noiseMap[y][x] > 0.6 && noiseMap[y][x] < 0.75) {
                            map[y][x].feature = "Forest";
                        }
                        break;
                    }
                }
            }
        }
    }

    private Vector2[][] gradients;

    public float[][] generatePerlin(int xSize, int ySize){
        float[][] map = new float[ySize][xSize];
        gradients = setupGradients(2, 2);

        for (int y = 0; y< map.length; y++){
            for (int x = 0; x < map[0].length; x++){
                map[y][x] = random.nextFloat();
            }
        }

        float yInc = 1f/ySize;
        float xInc = 1f/xSize;
        float temp;
        float max = -999;
        float min = 999;

        //generate the perlin noise
        for (float y = 0; y < 1; y+=yInc){
            for (float x = 0; x < 1; x+=xInc){
                temp = perlinNoise(x, y);
                map[Math.round(y*yInc)][Math.round(x*xInc)] = temp;

                if (temp > max){ max = temp;}
                else if (temp < min) {min = temp;}
            }
        }
        //System.out.println (max + " " + min);

        //figure out the z score, and find a max and min value
        map = zScores(map);
        max = -999;
        min = 999;
        for (int y = 0; y< map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                temp = map[y][x];
                if (temp > max){ max = temp;}
                else if (temp < min) {min = temp;}
            }
        }
        //System.out.println (max + " Z " + min);

        //move the max/min to 1/-1
        for (int y = 0; y< map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                if (map[y][x] >= 0){
                    map[y][x] = map[y][x]/max;
                }else{
                    map[y][x] = -map[y][x]/min;
                }
            }
        }

        return map;
    }

    private float perlinNoise(float x, float y){
        //http://staffwww.itn.liu.se/~stegu/simplexnoise/simplexnoise.pdf
        Vector2[] g = new Vector2[4];
        float[] n = new float[4];
        Vector2 p;
        float nx0, nx1, nxy;

        p = new Vector2(x, y);
        int i = (int)Math.floor(x);
        int j = (int)Math.floor(y);
        g[0] = gradients[i][j];
        g[1] = gradients[i+1][j];
        g[2] = gradients[i][j+1];
        g[3] = gradients[i+1][j+1];
        float u = x-i;
        float v = y-j;
        n[0] = dot(g[0], u, v);
        n[1] = dot(g[1], u-1, v);
        n[2] = dot(g[2], u, v-1);
        n[3] = dot(g[3], u-1, v-1);
        nx0 = n[0] * (1-interpolation(u)) + n[1] * interpolation(u);
        nx1 = n[2] * (1-interpolation(u)) + n[3] * interpolation(u);
        nxy = nx0 * (1-interpolation(v)) + nx1 * interpolation(v);
        return nxy;
    }

    private float dot(Vector2 g, float u, float v){
        return g.x * u + g.y * v;
    }

    private float interpolation(float t){
        //f(t) = 6t^5 - 15t^4 + 10t^3
        return 6 * (t*t*t*t*t) - 15 * (t*t*t*t) + 10 * (t*t*t);
    }

    private Vector2[][] setupGradients(int xSize, int ySize){
        Vector2[][] gradients = new Vector2[xSize][ySize];
        float temp;
        for (int y = 0; y < ySize; y++){
            for (int x = 0; x < xSize; x++){
                temp = random.nextFloat()*3.14159f;
                gradients[x][y] = new Vector2((float)Math.sin(temp),(float)Math.cos(temp));
            }
        }
        return gradients;
    }
    /**
     * Generates rivers on the map, starting from the given x and y coordinates
     * @param map       map to generate on, used to check for locations of sea
     * @param oY        x of the starting point
     * @param oX        y of the starting point
     * @param riverPoints   array of already existing rivers
     * @param oDirection initial direction of the river
     * @return          array of hexes with rivers on them, located in the data variable on the point
     */
    private Array<Point> generateRiver(Hex[][] map, int oX, int oY, Array<Point> riverPoints, int oDirection, int numAttempts){
        if (numAttempts > 10){      //if there has been more than 10 failed attempts, then just return null and ignore this point
            return null;
        }
        int x = oX, y = oY, direction = oDirection;
        Array<Point> newRiverPoints = new Array<Point>();
        int riverLength = Math.round(inRange(8, 16));    //generate how long the river will be
        String data;
        String[] dirString = {"100000", "010000", "001000", "000100", "000010", "000001"};  //directional strings, for creating river within the hex
        int x0 = 0, y0 = 0;     //x and y of the first point
        int x1 = 0, y1 = 0;     //x and y of the 2nd point
        int dir0 = 0, dir1 = 0; //string array locations of the directions for the first and 2nd point
        int counter;            //used to count the number of rivers on the tile, if there is more than 4, retry generating the river
        boolean makeRiver0, makeRiver1;         //booleans used to make new Points, they are turned false when there is already a point in the array
        boolean alternate;      //used to keep directions relatively straight, alternates between first and 2nd point
        alternate = true;
        riverGen:{
            for (int i = 0; i < riverLength; i++){
                switch (direction){     //there's 2 hexes touching one side of the river, this switch figures out the 2 hexes
                    case 0:     //top
                        x0 = x-1 +y%2;
                        y0 = y+1;
                        dir0 = 1;
                        x1 = x + y%2;
                        y1 = y+1;
                        dir1 = 4;
                        break;
                    case 1:     //top right
                        x0 = x +y%2;
                        y0 = y+1;
                        dir0 = 2;
                        x1 = x+1;
                        y1 = y;
                        dir1 = 5;
                        break;
                    case 2:     //bottom right
                        x0 = x+1;
                        y0 = y;
                        dir0 = 3;
                        x1 = x +y%2;
                        y1 = y-1;
                        dir1 = 0;
                        break;
                    case 3:     //bottom
                        x0 = x + y%2;
                        y0 = y-1;
                        dir0 = 4;
                        x1 = x-1 +y%2;
                        y1 = y-1;
                        dir1 = 1;
                        break;
                    case 4:     //bottom left
                        x0 = x-1 +y%2;
                        y0 = y-1;
                        dir0 = 5;
                        x1 = x-1;
                        y1 = y;
                        dir1 = 2;
                        break;
                    case 5:     //top left
                        x0 = x-1;
                        y0 = y;
                        dir0 = 0;
                        x1 = x-1 +y%2;
                        y1 = y+1;
                        dir1 = 3;
                        break;
                }

                //checks if any of the 2 tiles are water tiles, meaning that the river has reached the ocean
                if (map[y0][x0].elevation == 0){
                    break;
                }else if (map[y1][x1].elevation == 0){
                    break;
                }
                //checks if this point is already a river, if it is, redo the river generation
                for (Point rp: riverPoints){
                    if (rp.x == x0 && rp.y == y0){
                        break riverGen;
                    }else if (rp.x == x1 && rp.y == y1){
                        break riverGen;
                    }
                }

                //checks if there is already a point on the new spots
                makeRiver0 = true;      //resets these
                makeRiver1 = true;
                for (Point rp: newRiverPoints){
                    if (makeRiver0 && rp.x == x0 && rp.y == y0){  //if there is, add the data to it
                        data = "";
                        counter = 0;
                        for (int n = 0; n < 6; n++){
                            if (n == dir0){
                                data += '1';
                                counter++;
                            }else{
                                data += rp.data.charAt(n);
                                if (rp.data.charAt(n) == '1'){
                                    counter++;
                                }
                            }
                        }
                        if (counter > 3){       //if the river flows through the same tile more than 3 times, redo the generation
                            break riverGen;
                        }
                        rp.data = data;
                        makeRiver0 = false;
                        if (!makeRiver1){break;}
                    }
                    if (makeRiver1 && rp.x == x1 && rp.y == y1){
                        data = "";
                        counter = 0;
                        for (int n = 0; n < 6; n++){
                            if (n == dir1){
                                data += '1';
                                counter++;
                            }else{
                                data += rp.data.charAt(n);
                                if (rp.data.charAt(n) == '1'){
                                    counter++;
                                }
                            }
                        }
                        if (counter > 3){
                            break riverGen;
                        }
                        rp.data = data;
                        makeRiver1 = false;
                        if (!makeRiver0){break;}
                    }
                }

                //if the 2 points don't already exist, add them to the array
                if (makeRiver0){
                    newRiverPoints.add(new Point(x0, y0, dirString[dir0]));
                }
                if (makeRiver1){
                    newRiverPoints.add(new Point(x1, y1, dirString[dir1]));
                }

                if (alternate){                             //alternates between first and 2nd point, to keep the direction straight
                    alternate = nextPosFloat() <= 0.25;     //75% of keeping in the same direction it is currently flowing through
                    x = x1;
                    y = y1;
                    if (direction == 0){
                        direction = 5;
                    }else{
                        direction --;
                    }
                }else{
                    alternate = nextPosFloat() > 0.25;
                    x = x0;
                    y = y0;
                    if (direction == 5){
                        direction = 0;
                    }else{
                        direction++;
                    }
                }
            }
            riverPoints.addAll(newRiverPoints);
            return riverPoints;
        }
        //it will only reach this part if the river fails to generate to the requirements, this will call the method again and *hopefully* generate a good river
        //System.out.println(numAttempts);
        return generateRiver(map, oX, oY, riverPoints, oDirection, numAttempts+1);
    }

    /**
     * Converts the map to a Z score map <br>
     *      The "Z" score is based on normal distribution, and the diamond square's output numbers are close enough to a normal distribution
     * @param map   map to be converted to it's Z score
     * @return      Z score map, where 0 is average, negative is below average, and positive is above average
     */
    public static float[][] zScores(float[][] map){
        //Converts the randomized data to a Standard Normal Distribution
        //the random data is close enough to a Normal Distribution so i can just base the terrain on the Z score
        float mean = 0;
        float stdDev = 0;
        int counter = 0;
        float max = -9999;
        float min = 9999;
        Array<Float> fl = new Array<Float>();
        for (int Y = 0; Y < map.length; Y++) {              //gets the mean
            for (int X = 0; X < map[0].length; X++) {
                mean += map[Y][X];
                counter++;
                fl.add(map[Y][X]);
            }
        }
        mean /= counter;                                   //calculates the mean from the total
        for (Float f: fl){
            stdDev += (f-mean) * (f-mean);
            if (f > max) max = f;
            else if (f < min && f != 0) min = f;
        }
        stdDev /= counter;                                  //gets variance
        stdDev = (float)Math.sqrt(stdDev);                  //gets standard deviation
        //convert the values to "Z" values
        for (int Y = 0; Y < map.length; Y++) {
            for (int X = 0; X < map[0].length; X++) {
                map[Y][X] = (map[Y][X] - mean)/stdDev;      //converts the map[y][x] value to it's Z value
                if (map[Y][X] > 5){                         //Locks the max and min z values at +-5
                    map[Y][X] = 5;
                }
                else if (map[Y][X] < -5){
                    map[Y][X] = -5;
                }
            }
        }

        /*System.out.println (stdDev);
        System.out.println("min " + min + " max " + max);
        System.out.println (mean);*/                        //mean
        fl.sort();
        //System.out.println (fl.get((fl.size-1)/2));         //median
        return map;
    }

    /**
     * Generates a map using the diamond square algorithm
     * @param xSize     width of the map, must be 2^n + 1
     * @param ySize     height of the map, must be 2^n + 1
     * @return          a 2d array of floats representing the map
     */
    private float[][] diamondSquare(int xSize, int ySize, int size, float variation, boolean needCenterSeed){
        float [][] map = new float[ySize][xSize];
        if (!(size > 0 && (size & (size-1)) == 0 )){
            size = 32;
        }
        for (int y = 0; y <= ySize/size; y++){     //splits it up into 32x32 chunks
            for (int x = 0; x < xSize/size; x++){
                map[y*size][x*size] = inRange(20, 30);
            }
        }

        if (needCenterSeed) {
            try {
                map[ySize / 2][xSize / 2] = inRange(40, 50);
                map[ySize / 2 + size][xSize / 2] = inRange(35, 40);
                map[ySize / 2 - size][xSize / 2] = inRange(35, 40);
                map[ySize / 2][xSize / 2 + size] = inRange(35, 40);
                map[ySize / 2][xSize / 2 - size] = inRange(35, 40);
            } catch (ArrayIndexOutOfBoundsException e) {
            }

            map[0][0] = inRange(10, 20);
            map[0][xSize / 2] = inRange(10, 20);
            map[0][xSize - 1] = inRange(10, 20);
            map[ySize / 2][0] = inRange(10, 20);
            map[ySize / 2][xSize - 1] = inRange(10, 20);
            map[ySize - 1][0] = inRange(10, 20);
            map[ySize - 1][xSize / 2] = inRange(10, 20);
            map[ySize - 1][xSize - 1] = inRange(10, 20);

            for (int y = 0; y <= ySize / size; y++) {
                map[y * size][xSize - 1] = map[y * size][0];
            }
        }


        int step = (int)Math.round(Math.log(size)/Math.log(2));    //uses log identities to find out how many times i have to run the algorithm; its log2(chunkSize)
        int hSize = size/2;

        int x0 = 0;         //four corners of the square
        int y0 = 0;
        int x1 = size;
        int y1 = size;
        int x, y;
        float a,b,c,d;
        Array<Float> al = new Array<Float>(4);
        while (step > 0){
            //diamond step
            //System.out.println("Diamond");
            while (true) {
                a = map[y0][x0];    //four corners of square
                b = map[y0][x1];
                c = map[y1][x0];
                d = map[y1][x1];
                y = y0 + hSize;
                x = x0 + hSize;
                map[y][x] = Math.round((a + b + c + d) / 4f);
                map[y][x] += inRange(map[y][x]/10f*3 , map[y][x]/10f*5) * variation * next();
                x0 += size;
                x1 += size;
                if (x1 >= xSize){
                    x0 = 0;
                    x1 = size;
                    y0 += size;
                    y1 += size;
                    if (y1 >= ySize){
                        break;
                    }
                }
            }

            /*for (int Y = 0; Y < map.length; Y++){
                for (int X = 0; X < map[0].length; X++){
                    if (map[Y][X] == 0){
                        System.out.print("_ ");
                    }else{
                        System.out.print((int)map[Y][X] + " ");
                    }
                }
                System.out.println();
            }
            System.out.println("________________");
            System.out.println("Square");*/
            x0 = hSize;
            y0 = hSize;
            //square step
            while (true){
                // ==================== left point ======================
                al.clear();
                a = (valueAt(map, x0, y0));   //current point, cant be out of bounds
                al.add(valueAt(map, x0 - hSize, y0 - hSize));
                al.add(valueAt(map, x0 - hSize, y0 + hSize));
                al.add(valueAt(map, x0 - size, y0));
                //using these variables instead of new ones to save memory
                b = al.size+1;
                c = a;
                for (Float f : al) {
                    if (f != -1) {   //checks if they are out of bounds
                        c += f;
                    } else {
                        b--;
                    }
                }
                x = x0 - hSize;
                map[y0][x] = c / b + (inRange(map[y0][x]/10f*3, map[y0][x]/10f*5) * variation * next());

                // ==================== right point =====================
                al.clear();
                al.add(valueAt(map, x0 + hSize, y0 - hSize));
                al.add(valueAt(map, x0 + hSize, y0 + hSize));
                al.add(valueAt(map, x0 + size, y0));
                b = al.size+1;
                c = a;
                for (Float f : al) {
                    if (f != -1) {
                        c += f;
                    } else {
                        b--;
                    }
                }
                x = x0 + hSize;
                map[y0][x] = c / b + (inRange(map[y0][x]/10f*3, map[y0][x]/10f*5) * variation * next());

                // ==================== top point =======================
                al.clear();
                al.add(valueAt(map, x0 + hSize, y0 + hSize));
                al.add(valueAt(map, x0 - hSize, y0 + hSize));
                al.add(valueAt(map, x0, y0 + size));
                b = al.size+1;
                c = a;
                for (Float f : al) {
                    if (f != -1) {
                        c += f;
                    } else {
                        b--;
                    }
                }
                y = y0 + hSize;
                map[y][x0] = c / b + (inRange(map[y][x0]/10f*3, map[y][x0]/10f*5) * variation * next());

                // =================== bottom point =====================
                al.clear();
                al.add(valueAt(map, x0 + hSize, y0 - hSize));
                al.add(valueAt(map, x0 - hSize, y0 - hSize));
                al.add(valueAt(map, x0, y0 - size));
                b = al.size+1;
                c = a;
                for (Float f : al) {
                    if (f != -1) {
                        c += f;
                    } else {
                        b--;
                    }
                }
                y = y0 - hSize;
                map[y][x0] = c / b + (inRange(map[y][x0]/10f*3, map[y][x0]/10f*5) * variation * next());


                x0 += size;
                if (x0 >= xSize-1){
                    x0 = hSize;
                    y0 += size;
                    if (y0 >= ySize-1){
                        break;
                    }
                }
            }
            variation /= 1.5f;
            size /= 2;
            hSize /= 2;
            step--;

            x0 = 0;
            y0 = 0;
            x1 = size;
            y1 = size;
            /*for (int Y = 0; Y < map.length; Y++){
                for (int X = 0; X < map[0].length; X++){
                    if (map[Y][X] == 0){
                        System.out.print("_ ");
                    }else{
                        System.out.print((int)map[Y][X] + " ");
                    }
                }
                System.out.println();
            }
            System.out.println("________________");
            System.out.println(variation);*/
        }
        return map;
    }

    /**
     * Fixes the map, so instead of being patchy this method returns a map with solid landmasses <br>
     *     Only generates the land, sea and shore. The moisture method generates the other terrain <br>
     *     uses Landmass Codes; 1 is deep ocean, 2 is light ocean, 3 is land, 8 is hill, 9 is mountain
     * @param map   map to be fixed using Landmass Codes
     * @return      solid landmass map using Landmass Codes
     */
    private int[][] fixLand(int[][] map){
        map = smoothLand(map);      //smooths out the land, gets rid of some patchy tiles
        map = smoothLand(map);
        Array<Point> adj;
        Array<Integer> ocean = new Array<Integer>();    //a list of the ocean tiles, for the smooth method to check if to ignore or not
        ocean.add (49); ocean.add (50);
        smooth(map, 1, 2, ocean);                       //helps smooth out the deep ocean tiles

        /** Generates coastal tiles for ocean tiles that are touching land*/
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                if (map[y][x] == 49){                //if its deep ocean
                    if (next() > 0.2){             //randomly get the neighbours in 1 or 2 range
                        adj = getPointInRange (x, y, 2);
                    }else {
                        adj = getNeighbours(x, y);
                    }
                    for (Point p: adj){                 //checks the neighbours
                        if (valueAt(map, p.x, p.y) > 50){//if there's a land tile
                            map[y][x] = 50;              //changes this tile to light ocean
                            break;
                        }
                    }
                }
            }
        }

        //deletes the single deep ocean tiles leftover and replaces it with whatever occurs the most around the tile
        int mostOccurredTerrain;
        int max, lastTerrain;
        int counter;
        Array<Integer> terrains = new Array<Integer>();     //the different types of terrain
        for (int y = 0; y < map.length; y++){
            for (int x = 0; x < map[0].length; x++){
                if (map[y][x] == 49) {                      //only does this action on ocean tiles
                    mostOccurredTerrain = 49;
                    max = 0;
                    counter = 0;
                    terrains.clear();
                    for (Point p : getNeighbours(x, y)) {
                        if (valueAt(map, p.x, p.y) != -1) {
                            terrains.add(map[p.y][p.x]);
                        }
                    }
                    terrains.sort();
                    if (terrains.get(0) != 49){             //if the first position is not 49, then it means there are no ocean tiles nearby
                        lastTerrain = terrains.get(0);
                        for (int n = 0; n < terrains.size; n++){
                            if (terrains.get(n) != lastTerrain){
                                if (counter > max){
                                    max = counter;
                                    mostOccurredTerrain = lastTerrain;
                                    counter = 0;
                                }
                            }else{
                                counter++;
                            }
                        }
                    }
                    map[y][x] = mostOccurredTerrain;
                }
            }
        }
        return map;
    }

    /**
     * Smooths out the map
     * @param map   map to smooth out
     * @param terrain   terrain to smooth out
     * @param threshold how many nearby tiles of a different terrain until this terrain changes
     * @return
     */
    private int[][] smooth (int[][] map, int terrain, int threshold, Array<Integer> ignore){
        int valueAt;
        Array<Integer> terrains = new Array<Integer>();     //the terrain beside this one, based on how many terrains there are

        for (int y = 0; y < map.length; y++){
            for (int x = 0; x < map[0].length; x++) {
                if (map[y][x] == terrain) {                         //only does these stuff if its on the terrain tile i want to smooth out
                    terrains.clear();
                    //see what the neighbouring tiles are
                    for (Point p: getNeighbours(x, y)){
                        valueAt = valueAt(map, p.x, p.y);
                        if (valueAt != -1 && !ignore.contains(valueAt, true)) {          //if its not invalid and if its not an ocean tile
                            terrains.add(valueAt);
                        }
                    }
                    terrains.sort();
                    for (int i = 0; i < terrains.size; i++){
                        try {
                            if (terrains.get(i + threshold) == terrains.get(i)) {
                                map[y][x] = terrains.get(i);
                            }
                        }catch (IndexOutOfBoundsException e){}
                    }
                }
            }
        }
        return map;
    }

    /**
     * Used to smooth out the map with only land and water
     * @param map; the map featuring only land and water
     * @return      smoothed out land
     */
    private int[][] smoothLand(int[][] map){
        int counter, terrain, max;
        Array<Point> adj;
        for (int y = 0; y < map.length; y++){
            for (int x = 0; x < map[0].length; x++){
                counter = 0;
                max = 4;
                if (map[y][x] == 49){    //sees if its water or land, and sets terrain to be the opposite
                    terrain = 97;
                }else{
                    terrain = 49;
                }
                adj = getNeighbours(x, y);
                for (Point p: adj){
                    if (valueAt(map, p.x, p.y) == -1){
                        max--;
                        continue;
                    }else if (map[y][x] == 49){
                        if (valueAt(map, p.x, p.y) > 50){
                            counter++;
                        }
                    }else{
                        if (valueAt(map, p.x, p.y) == 49){
                            counter++;
                        }
                    }
                }
                if (counter > max){
                    map[y][x] = terrain;
                }
            }
        }
        return map;
    }
    /**
     * Returns an array of the points in range of the specified point
     * @param x x of the current/specified point
     * @param y
     * @param range
     * @return
     */
    public Array<Point> getPointInRange(int x, int y, int range){
        Array<Point> path = new Array<Point>();
        getPointInRange(x,y,range,path);
        return path;
    }
    private void getPointInRange(int x, int y, float range, Array<Point> path){
        //not sure if this is the best way to find the neighbours
        if (range > 0) {
            Array<Point> open = new Array<Point>();
            for (Point p : getNeighbours(x, y)) {       //gets the neighbours
                if (p != null) {
                    if (!path.contains(p, false)) {     //if the path does not already have this point
                        path.add(p);                    //adds it to the path
                        open.add(p);                    //adds it to the open list to add it's neighbours to the path later
                    }
                }
            }
            for (Point p : open) {                      //adds the points in the neighbours to the path
                getPointInRange(p.x, p.y, range - 1, path);
            }
        }
    }
    private Array<Point> getNeighbours(int x, int y){
        Array<Point> neighbours = new Array<Point>(6);
        if (y%2 == 0){
            neighbours.add(new Point(x, y - 1));
            neighbours.add(new Point(x, y + 1));
            neighbours.add(new Point(x - 1, y));
            neighbours.add(new Point(x + 1, y));
            neighbours.add(new Point(x - 1, y - 1));
            neighbours.add(new Point(x - 1, y + 1));
        }else{
            neighbours.add(new Point(x, y - 1));
            neighbours.add(new Point(x, y + 1));
            neighbours.add(new Point(x - 1, y));
            neighbours.add(new Point(x + 1, y));
            neighbours.add(new Point(x + 1, y - 1));
            neighbours.add(new Point(x + 1, y + 1));
        }
        return neighbours;
    }

    private float valueAt (float[][] map, int x, int y){
        try{
            return map[y][x];
        }catch (ArrayIndexOutOfBoundsException e){
            return -1;
        }
    }
    private int valueAt (int[][] map, int x, int y){
        try{
            return map[y][x];
        }catch (ArrayIndexOutOfBoundsException e){
            return -1;
        }
    }
    private boolean existsAt (Hex[][] map, int x, int y){   //checks if the hex at this position exists
        try{
            if (map[y][x].elevation == -1)
            {}
            return true;
        }catch (ArrayIndexOutOfBoundsException e){
            return false;
        }
    }

    public int nextPosInt(int range){
        return Math.abs(Math.round(next()*range));
    }

    public float inRange(float min, float max){
        return (float)(min + random.nextDouble() * (max-min));
    }

    public float nextPosFloat(){
        return random.nextFloat();
    }

    public float next(){
        float y = (float)random.nextDouble();
        if (random.nextDouble() > 0.5d){
            return y;
        }else{
            return y *-1;
        }
        /*
        int n = x * 331 + seed*x*338;
        n = (int)Math.round(Math.pow(n<<13, n));
        int nn=(n*(n*n*41333 +53307781)+1376312589)&0x7fffffff;
        return ((1.0-((double)nn/1073741824.0))+1)/2.0;*/
    }
}
