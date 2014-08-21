package CivPackage.Systems;


import com.badlogic.gdx.math.Vector2;

import java.util.Random;

/**
 * Created by James on 2014-08-14.
 */
public class TerrainGenerationSystem {

    private Random r;

    public TerrainGenerationSystem(int seed){
        r = new Random(seed);
    }

    private Vector2[][] gradients;
    public int[][] generatePerlin(int xSize, int ySize){
        int[][] intMap = new int[ySize][xSize];
        float[][] map = new float[ySize][xSize];
        gradients = setupGradients(2, 2);

        for (int y = 0; y< map.length; y++){
            for (int x = 0; x < map[0].length; x++){
                map[y][x] = r.nextFloat();
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
        System.out.println (max + " " + min);

        //figure out the z score, and find a max and min value
        map = CivPackage.Random.zScores(map);
        max = -999;
        min = 999;
        for (int y = 0; y< map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                temp = map[y][x];
                if (temp > max){ max = temp;}
                else if (temp < min) {min = temp;}
            }
        }
        System.out.println (max + " Z " + min);

        //move the max/min to 1/-1
        for (int y = 0; y< map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                if (map[y][x] >= 0){
                    map[y][x] = map[y][x]/max;
                }else{
                    map[y][x] = map[y][x]/min;
                }
            }
        }


        for (int y = 0; y< map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                intMap[y][x] = Math.round(map[y][x]);
            }
        }
        return intMap;
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
                temp = r.nextFloat()*3.14159f;
                gradients[x][y] = new Vector2((float)Math.sin(temp),(float)Math.cos(temp));
            }
        }
        return gradients;
    }
}
