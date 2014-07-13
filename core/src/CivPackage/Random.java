package CivPackage;

import com.badlogic.gdx.utils.Array;

/**
 * Created by Lu on 2014-07-12.
 */
public class Random {

    private int seed;
    public Random(int seed){
        this.seed = seed;
    }

    public int[][] generateTerrain(int xSize, int ySize){
        float [][] map = new float[ySize][xSize];
        map[0][0]               = nextPosInt(5,8);
        map[0][xSize-1]         = nextPosInt(10,8);
        map[ySize-1][0]         = nextPosInt(15,8);
        map[ySize-1][xSize-1]   = nextPosInt(20,8);

        int step = 5;
        float variation = 2.4f;
        int size = xSize;
        int hSize = size/2;

        int x0 = 0;         //four corners of the square
        int y0 = 0;
        int x1 = xSize-1;
        int y1 = ySize-1;
        float a,b,c,d;
        Array<Float> al = new Array<Float>(4);
        //http://www.javaworld.com/article/2076745/learn-java/3d-graphic-java--render-fractal-landscapes.html
        while (step >= 1){
            //diamond step
            while (true) {
                a = map[y0][x0];    //four corners of square
                b = map[y0][x1];
                c = map[y1][x0];
                d = map[y1][x1];
                map[y0 + hSize][x0 + hSize] = Math.round(a + b + c + d / 4f) * (float) next((x0 + y1)) * variation;
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
                    b = al.size;
                    c = a;
                    for (Float f : al) {
                        if (f != -1) {   //checks if they are out of bounds
                            c += f;
                        } else {
                            b--;
                        }
                    }
                    map[y0][x0 - hSize] = c / b * (float) next((int) c) * variation;
                }
                // ==================== right point =====================
                if (map[y0][x0+hSize] == 0) {
                    al.clear();
                    al.add(valueAt(map, x0 + hSize, y0 - hSize));
                    al.add(valueAt(map, x0 + hSize, y0 + hSize));
                    al.add(valueAt(map, x0 + size, y0));
                    b = al.size;
                    c = a;
                    for (Float f : al) {
                        if (f != -1) {
                            c += f;
                        } else {
                            b--;
                        }
                    }
                    map[y0][x0 + hSize] = c / b * (float) next((int) c) * variation;
                }
                // ==================== top point =======================
                if (map[y0+hSize][x0] == 0) {
                    al.clear();
                    al.add(valueAt(map, x0 + hSize, y0 + hSize));
                    al.add(valueAt(map, x0 - hSize, y0 + hSize));
                    al.add(valueAt(map, x0, y0 + size));
                    b = al.size;
                    c = a;
                    for (Float f : al) {
                        if (f != -1) {
                            c += f;
                        } else {
                            b--;
                        }
                    }
                    map[y0 + hSize][x0] = c / b * (float) next((int) c) * variation;
                }
                // =================== bottom point =====================
                if (map[y0-hSize][x0] == 0) {
                    al.clear();
                    al.add(valueAt(map, x0 + hSize, y0 + hSize));
                    al.add(valueAt(map, x0 - hSize, y0 + hSize));
                    al.add(valueAt(map, x0, y0 + size));
                    b = al.size;
                    c = a;
                    for (Float f : al) {
                        if (f != -1) {
                            c += f;
                        } else {
                            b--;
                        }
                    }
                    map[y0 - hSize][x0] = c / b * (float) next((int) c) * variation;
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
            variation /= 1.5;
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
        }

        int[][] finalMap = new int[ySize][xSize];
        for (int Y = 0; Y < map.length; Y++){
            for (int X = 0; X < map[0].length; X++){
                finalMap[Y][X] = Math.round(map[Y][X]);
                //System.out.print(finalMap[Y][X] + " ");
            }
            //System.out.println();
        }

        return finalMap;
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

    public double next(int x){
        int n = x * 331 + seed*x*338;
        n = (int)Math.round(Math.pow(n<<13, n));
        int nn=(n*(n*n*41333 +53307781)+1376312589)&0x7fffffff;
        return ((1.0-((double)nn/1073741824.0))+1)/2.0;
    }
}
