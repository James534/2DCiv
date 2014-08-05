package CivPackage;

import com.badlogic.gdx.utils.Array;

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

    /**
     * Generates the terrain using diamond-square
     * @param xSize
     * @param ySize
     * @return
     */
    public int[][] generateTerrain(int xSize, int ySize){
        float[][] map = diamondSquare(xSize, ySize, 2f);    //generates the map
        map = zScores(map);                             //converts the map to a z score

        //Landmass Codes:
        //1 is deep ocean, 2 is light ocean, 3 is land, 8 is hill, 9 is mountain
        int[][] finalMap = new int[ySize][xSize];
        for (int Y = 0; Y < map.length; Y++){
            for (int X = 0; X < map[0].length; X++){
                if (map[Y][X] < 0.5){
                    finalMap[Y][X] = 1;     //deep ocean, light ocean is 2, to be added in fixLand
                }else if (map[Y][X] < 1.4){
                    finalMap[Y][X] = 3;     //land
                }else if (map[Y][X] < 1.6){
                    finalMap[Y][X] = 8;     //hills
                }else if (map[Y][X] < 1.8){
                    finalMap[Y][X] = 3;     //land
                }else if (map[Y][X] < 2.5){
                    finalMap[Y][X] = 8;     //hills
                }else{
                    finalMap[Y][X] = 9;     //mountains
                }
            }
        }
        finalMap = fixLand(finalMap);       //fixes up the patchy land and adds light ocean
        finalMap = moisture(finalMap);      //generates the other terrain
        return finalMap;
    }

    /**
     * Generates the terrain and features of the map <br>
     *     There are 3 main terrains; plains, grasslands, and desert <br>
     *     Tundra and snow are near the poles
     * @param       map using Landmass Codes
     * @return      map with terrain and features, using Terrain Codes
     */
    private int[][] moisture(int[][] map){
        /*
        Terrain codes:              Terrain feature codes:
        -1 is invalid               x0 is regular
        0-9 is water stuff          x1 is forest/jungle
        10-19 is plains
        20-29 is grassland
        30-39 is desert
        40-49 is tundra             x8 is hill
        50-59 is snow               x9 is mountain
        60+ are wonders
         */
        //http://pcg.wikidot.com/pcg-algorithm:fractal-river-basins   generate rivers

        float[][] climateMap = diamondSquare(map[0].length, map.length, 12f);
        climateMap = zScores(climateMap);
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map.length; x++) {
                if (map[y][x] > 2){
                    if (climateMap[y][x] < -0.75){
                        climateMap[y][x] = 10;
                    }else if (climateMap[y][x] < 0.75) {
                        climateMap[y][x] = 20;
                    }else{
                        climateMap[y][x] = 30;
                    }
                    if (map[y][x] == 3){
                        map[y][x] = 0;
                    }
                }else{
                    climateMap[y][x] = -1;
                }
            }
        }
        for (int y = 0; y < map.length; y++){
            for (int x = 0; x < map.length; x++){
                if (climateMap[y][x] != -1){
                    map[y][x] += climateMap[y][x];
                }
            }
        }
        return map;
    }

    /**
     * Converts the map to a Z score map <br>
     *      The "Z" score is based on normal distribution, and the diamond square's output numbers are close enough to a normal distribution
     * @param map   map to be converted to it's Z score
     * @return      Z score map, where 0 is average, negative is below average, and positive is above average
     */
    private float[][] zScores(float[][] map){
        //Converts the randomized data to a Standard Normal Distribution
        //the random data is close enough to a Normal Distribution so i can just base the terrain on the Z score
        float mean = 0;
        float stdDev = 0;
        int counter = 0;
        float max = -9999;
        float min = 9999;
        float maxZ = -9999;
        float minZ = 9999;
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
                if (map[Y][X] > maxZ) maxZ = map[Y][X];     //gets the max and min of the z value
                else if (map[Y][X] < minZ) minZ = map[Y][X];
            }
        }

        System.out.println (stdDev);
        System.out.println("min " + minZ + " max " + maxZ);
        System.out.println (mean);                          //mean
        fl.sort();
        System.out.println (fl.get((fl.size-1)/2));         //median
        return map;
    }

    /**
     * Generates a map using the diamond square algorithm
     * @param xSize     width of the map, must be 2^n + 1
     * @param ySize     height of the map, must be 2^n + 1
     * @return          a 2d array of floats representing the map
     */
    private float[][] diamondSquare(int xSize, int ySize, float variation){
        float [][] map = new float[ySize][xSize];
        /*map[0][0]               = inRange(0, 20, 30);
        map[0][xSize-1]         = inRange(0, 20, 30);
        map[ySize-1][0]         = inRange(0, 20, 30);
        map[ySize-1][xSize-1]   = inRange(0, 20, 30);

        map[0][xSize/2]         = inRange(0, 20, 30);
        map[ySize/2][0]         = inRange(0, 20, 30);
        map[ySize/2][xSize/2]   = inRange(0, 20, 30);
        map[ySize/2][xSize-1]   = inRange(0, 20, 30);
        map[ySize-1][xSize/2]   = inRange(0, 20, 30);*/
        for (int y = 0; y <= ySize/32; y++){     //splits it up into 32x32 chunks
            for (int x = 0; x <= xSize/32; x++){
                System.out.println (x + " " + y + " generate");
                map[y*32][x*32] = inRange(0, 20, 30);
            }
        }

        int step = 5;
        int size = xSize/2;
        int hSize = size/2;

        int x0 = 0;         //four corners of the square
        int y0 = 0;
        int x1 = xSize/2;
        int y1 = ySize/2;
        int x, y;
        float a,b,c,d;
        Array<Float> al = new Array<Float>(4);
        //http://www.javaworld.com/article/2076745/learn-java/3d-graphic-java--render-fractal-landscapes.html
        while (step > 0){
            //diamond step
            System.out.println("Diamond");
            while (true) {
                a = map[y0][x0];    //four corners of square
                b = map[y0][x1];
                c = map[y1][x0];
                d = map[y1][x1];
                y = y0 + hSize;
                x = x0 + hSize;
                map[y][x] = Math.round((a + b + c + d) / 4f);
                map[y][x] += inRange((int)c,map[y][x]/10f*3 , map[y][x]/10f*5) * variation * next(0);
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

            x0 = hSize;
            y0 = hSize;
            for (int Y = 0; Y < map.length; Y++){
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
            System.out.println("Square");
            //square step
            while (true){
                // ==================== left point ======================
                if (map[y0][x0-hSize] == 0) {
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
                    map[y0][x] = c / b + (inRange((int)c, map[y0][x]/10f*3, map[y0][x]/10f*5) * variation * next(0));
                }
                // ==================== right point =====================
                if (map[y0][x0+hSize] == 0) {
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
                    map[y0][x] = c / b + (inRange((int) c, map[y0][x]/10f*3, map[y0][x]/10f*5) * variation * next(0));
                }
                // ==================== top point =======================
                if (map[y0+hSize][x0] == 0) {
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
                    map[y][x0] = c / b + (inRange((int) c, map[y][x0]/10f*3, map[y][x0]/10f*5) * variation * next(0));
                }
                // =================== bottom point =====================
                if (map[y0-hSize][x0] == 0) {
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
                    y = y0 - hSize;
                    map[y][x0] = c / b + (inRange((int) c, map[y][x0]/10f*3, map[y][x0]/10f*5) * variation * next(0));
                }

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
            for (int Y = 0; Y < map.length; Y++){
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
            System.out.println(variation);
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
        smooth(map, 1, 2);          //helps smooth out the deep ocean tiles
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                if (map[y][x] == 1){                //if its deep ocean
                    if (next(0) > 0.2){             //randomly get the neighbours in 1 or 2 range
                        adj = getPointInRange (x, y, 2);
                    }else {
                        adj = getNeighbours(x, y);
                    }
                    for (Point p: adj){                 //checks the neighbours
                        if (valueAt(map, p.x, p.y) > 2){//if there's a land tile
                            map[y][x] = 2;              //changes this tile to light ocean
                            break;
                        }
                    }
                }
            }
        }
        deleteSingleHex(map, 1);    //deletes the single deep ocean tiles leftover
        return map;
    }

    /**
     * Gets rid of the random single hexes
     * @param map
     * @param terrain
     * @return
     */
    private int[][] deleteSingleHex(int[][] map, int terrain){
        int valueAt, max, counter;
        int[] terrains = new int[10];
        for (int y = 0; y < map.length; y++){
            for (int x = 0; x < map.length; x++){
                if (map[y][x] == terrain) {                         //only does these stuff if its on the terrain tile i want to smooth out
                    max = 0;
                    counter = terrain;
                    for (int i = 0; i < terrains.length; i++) {     //resets the array
                        terrains[i] = 0;
                    }
                    for (Point p : getNeighbours(x, y)) {
                        valueAt = valueAt(map, p.x, p.y);
                        if (valueAt != -1) {         //if its not invalid and if its not an ocean tile
                            terrains[valueAt]++;
                        }
                    }
                    if (terrains[terrain] < 2){                     //if there's less than 2 hexes of the same terrain
                        for (int i = 0; i < terrains.length; i++){
                            if (terrains[i] > max){
                                max = terrains[i];
                                counter = i;
                            }
                        }
                        map[y][x] = counter;
                    }
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
    private int[][] smooth (int[][] map, int terrain, int threshold){
        int valueAt;
        int[] terrains = new int[10];     //the terrain beside this one, based on how many terrains there are

        for (int y = 0; y < map.length; y++){
            for (int x = 0; x < map[0].length; x++) {
                if (map[y][x] == terrain) {                         //only does these stuff if its on the terrain tile i want to smooth out
                    for (int i = 0; i < terrains.length; i++) {     //resets the array
                        terrains[i] = 0;
                    }
                    for (Point p: getNeighbours(x, y)){
                        valueAt = valueAt(map, p.x, p.y);
                        if (valueAt != -1 && valueAt > 2){          //if its not invalid and if its not an ocean tile
                            terrains[valueAt]++;
                        }
                    }
                    for (int i = 0; i < terrains.length; i++){
                        if (terrains[i] > threshold) {
                            map[y][x] = i;
                        }
                    }
                }
            }
        }
        return map;
    }

    /**
     * Used to smooth out the map
     * @param map; an array of ints to be smoothed
     * @return
     */
    private int[][] smoothLand(int[][] map){
        int counter, terrain, max;
        Array<Point> adj;
        for (int y = 0; y < map.length; y++){
            for (int x = 0; x < map[0].length; x++){
                counter = 0;
                max = 4;
                if (map[y][x] == 1){    //sees if its water or land, and sets terrain accordingly
                    terrain = 4;
                }else{
                    terrain = 1;
                }
                adj = getNeighbours(x, y);
                for (Point p: adj){
                    if (valueAt(map, p.x, p.y) == terrain){
                        counter++;
                    }
                    if (valueAt(map, p.x, p.y) == -1){
                        max--;
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
        if (range > 0) {
            Array<Point> open = new Array<Point>();
            for (Point p : getNeighbours(x, y)) {         //gets the neighbours
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

    public int nextPosInt(int x, int range){
        return (int)Math.abs(Math.round(next(x)*range));
    }

    public float inRange(int x, double min, double max){
        return (float)(min + random.nextDouble() * (max-min));
    }

    public float next(int x){
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
