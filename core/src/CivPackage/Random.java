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
        map[0][0]               = 28;//inRange(0, 20, 30);
        map[0][xSize-1]         = 28;//inRange(0, 20, 30);
        map[ySize-1][0]         = 28;//inRange(0, 20, 30);
        map[ySize-1][xSize-1]   = 28;//inRange(0, 20, 30);

        int step = 7;
        float variation = 4f;
        int size = xSize;
        int hSize = size/2;

        int x0 = 0;         //four corners of the square
        int y0 = 0;
        int x1 = xSize-1;
        int y1 = ySize-1;
        int x, y;
        float a,b,c,d;
        float max = 0;
        float min = 9999;
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
                    if (map[Y][X] > max) max = map[Y][X];
                    else if (map[Y][X] < min && map[Y][X] != 0) min = map[Y][X];
                }
                System.out.println();
            }
            System.out.println("________________");
            System.out.println( variation);
            System.out.println("min " + min + " max " + max);
        }

        int[][] finalMap = new int[ySize][xSize];
        for (int Y = 0; Y < map.length; Y++){
            for (int X = 0; X < map[0].length; X++){
                if (map[Y][X] > (max+min)/2){
                    finalMap[Y][X] = 1;
                }else{
                    finalMap[Y][X] = 2;
                }
                /*if (map[Y][X] < 0) {
                    finalMap[Y][X] = 0;
                }else{
                    finalMap[Y][X] = Math.round(map[Y][X]);
                }*/
                //finalMap[Y][X] = Math.round(Math.abs(map[Y][X]));
                //System.out.print(finalMap[Y][X] + " ");
            }
            //System.out.println();
        }
        //finalMap = smooth(finalMap);
        return finalMap;
    }
    private int[][] smooth(int[][] map){
        int i;
        int c;
        for (int y = 0; y < map.length; y++){
            for (int x = 0; x < map[0].length; x++){
                i = 0;
                if (map[y][x] == 1){    //only works for now
                    c = 2;
                }else{
                    c = 1;
                }
                try{
                    if (map[y][x+1] == c){
                        i++;
                    }if (map[y+1][x] == c){
                        i++;
                    }if (map[y-1][x] == c){
                        i++;
                    }if (map[y+1][x+1] == c){
                        i++;
                    }if (map[y+1][x-1] == c) {
                        i++;
                    }if (map[y-1][x+1] == c){
                        i++;
                    }if (map[y+1][x-1] == c){
                        i++;
                    }
                    if (i > 4){
                        //map[y][x] = c;
                    }
                }catch (ArrayIndexOutOfBoundsException e){}
            }
        }
        return map;
    }

    private float valueAt (float[][] map, int x, int y){
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
