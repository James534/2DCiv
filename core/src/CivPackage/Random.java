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

    public int[][] generateTerrainPerlin(int xSize, int ySize){
        float [][] map = new float[ySize][xSize];
        int [][] intMap = new int[ySize][xSize];

        float low = 999;
        float high = 0;
        int ix, iy;
        for (float y = 0; y < ySize/4; y+=0.25){
            for (float x = 0; x < xSize/4; x+=0.25){
                ix = Math.round(x*4);
                iy = Math.round(y*4);
                map[iy][ix] = perlinNoise(x, y);
                if (map[iy][ix] > high) high = map[iy][ix];
                if (map[iy][ix] < low) low = map[iy][ix];
            }
        }

        System.out.println(low + " " + high);

        for (int y = 0; y < ySize; y++){
            for (int x = 0; x < xSize; x++){
                map[y][x] = (map[y][x] - low)/(high-low);
                System.out.println(map[y][x]);
                intMap[y][x] = Math.round(map[y][x] * 2);
            }
        }
        return intMap;
    }

    private float perlinNoise(float x, float y){
        //http://freespace.virgin.net/hugo.elias/models/m_perlin.htm
        float total = 0;
        float p = 1/4;
        float n = 5;

        float freq, amp;

        for (int i = 0; i < n; i++){
            freq = (float)Math.pow(2, i);
            amp = (float)Math.pow(p, i);
            total = total + interpolateNoise(x * freq, y * freq) * amp;
        }
        return total;
    }

    private float interpolateNoise (float x, float y){
        int ix = (int)x;
        int iy = (int)y;
        float fX = x-ix;
        float fY = y-iy;

        float v1 = smoothNoise1(ix, iy);
        float v2 = smoothNoise1(ix + 1, iy);
        float v3 = smoothNoise1(ix, iy + 1);
        float v4 = smoothNoise1(ix + 1, iy + 1);

        float i1 = interpolate(v1, v2, fX);
        float i2 = interpolate(v3, v4, fX);
        return interpolate(i1, i2, fY);
    }

    private float interpolate(float a, float b, float x){
        double ft = x * 3.1415927f;
        double f = (1 - Math.cos(ft))* 0.5f;
        return (float) (a*(1-f) + b*f);
    }

    private float smoothNoise1 (float x, float y){
        float corners = (noise1(x-1, y-1) + noise1(x+1, y-1) + noise1(x-1, y+1) + noise1(x+1, y+1) ) /16;
        float sides = (noise1 (x-1, y) + noise1(x+1, y) + noise1(x, y-1) + noise1(x, y+1) ) /8;
        float center = noise1(x, y)/4;
        return corners + sides + center;
    }

    private float noise1(float x, float y){
        int n = Math.round(x + y) * 57;
        n = (n<<13);
        double N = Math.pow(n, n);
        N = ( 1 - ( Math.round(N* (N*N * 15731 + 789221) + 1376312589) & 0x7ffff) / 1073741824d);
        //return (float) N;
        return random.nextFloat();
    }

    /**
     * Generates the terrain using diamond-square
     * @param xSize
     * @param ySize
     * @return
     */
    public int[][] generateTerrain(int xSize, int ySize){
        float [][] map = new float[ySize][xSize];
        map[0][0]               = inRange(0, 20, 30);
        map[0][xSize-1]         = inRange(0, 20, 30);
        map[ySize-1][0]         = inRange(0, 20, 30);
        map[ySize-1][xSize-1]   = inRange(0, 20, 30);

        map[0][xSize/2]         = inRange(0, 20, 30);
        map[ySize/2][0]         = inRange(0, 20, 30);
        map[ySize/2][xSize/2]   = inRange(0, 20, 30);
        map[ySize/2][xSize-1]   = inRange(0, 20, 30);
        map[ySize-1][xSize/2]   = inRange(0, 20, 30);

        int step = 5;
        float variation = 2f;
        int size = xSize/2;
        int hSize = size/2;

        int x0 = 0;         //four corners of the square
        int y0 = 0;
        int x1 = xSize/2;
        int y1 = ySize/2;
        int x, y;
        float a,b,c,d;
        float ln = 4;
        Array<Float> al = new Array<Float>(4);
        //http://www.javaworld.com/article/2076745/learn-java/3d-graphic-java--render-fractal-landscapes.html
        while (step >= 1){
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
                ln = map[y0+hSize][x0+hSize];
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
                    map[y0][x] = c / b + (float) (inRange((int)c, map[y0][x]/10f*3, map[y0][x]/10f*5) * variation * next(0));
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
                    map[y0][x] = c / b + (float) (inRange((int) c, map[y0][x]/10f*3, map[y0][x]/10f*5) * variation * next(0));
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
                    map[y][x0] = c / b + (float) (inRange((int) c, map[y][x0]/10f*3, map[y][x0]/10f*5) * variation * next(0));
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
                    map[y][x0] = c / b + (float) (inRange((int) c, map[y][x0]/10f*3, map[y][x0]/10f*5) * variation * next(0));
                    ln = map[y0-hSize][x0];
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

        System.out.println("min " + minZ + " max " + maxZ);
        System.out.println (mean);                          //mean
        fl.sort();
        System.out.println (fl.get((fl.size-1)/2));         //median

        int[][] finalMap = new int[ySize][xSize];
        for (int Y = 0; Y < map.length; Y++){
            for (int X = 0; X < map[0].length; X++){
                if (map[Y][X] < -0.2){
                    finalMap[Y][X] = 1;     //deep ocean, light ocean is 2
                }else if (map[Y][X] < 0){
                    finalMap[Y][X] = 3;     //terrain 1
                }else if (map[Y][X] < 0.75){
                    finalMap[Y][X] = 4;     //terrain 2
                }else if (map[Y][X] < 1.5){
                    finalMap[Y][X] = 5;     //terrain 3
                }else if (map[Y][X] < 2.25){
                    finalMap[Y][X] = 6;     //terrain 4
                }else if (map[Y][X] < 3){
                    finalMap[Y][X] = 7;     //terrain 5
                }else{
                    finalMap[Y][X] = 9;     //mountains
                }
            }
        }
        finalMap = smoothLand(finalMap);    //smooths out the land
        finalMap = smoothLand(finalMap);
        finalMap = moisture(finalMap);
        return finalMap;
    }

    private int[][] moisture(int[][] map){
        Array<Point> adj;
        for (int i = 6; i >= 3; i--){
            smooth(map, i, 3);
        }
        smooth(map, 1, 2);
        smooth(map, 1, 2);
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
        deleteSingleHex(map, 1);
        for (int i = 3; i <= 8; i++){
            deleteSingleHex(map, i);
        }
        for (int i = 3; i <= 8; i++){
            deleteSingleHex(map, i);
        }
        deleteSingleHex(map, 1);
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
