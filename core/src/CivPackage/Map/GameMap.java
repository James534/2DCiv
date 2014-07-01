package CivPackage.Map;

import CivPackage.Models.Hex;

/**
 * Created by james on 6/30/2014.
 */
public class GameMap {

    private Hex[][] map;
    public final int xSize, ySize;

    public GameMap(int xSize, int ySize){
        this.xSize = xSize;
        this.ySize = ySize;

        map = new Hex[xSize][ySize];
        for (int y = 0; y < map.length; y++){
            for (int x = 0; x < map[0].length;x++){
                map[y][x] = new Hex(1, x, y);
            }
        }
        map[0][0].selected(true);
    }

    public Hex getPixelHex(float x, float y){
        int intX = (int)x/56;
        int intY = (int)y/56;

        getHex(intX, intY).selected(true);

        return null;
    }

    public Hex getHex(int x, int y){
        if (x < 0 || x > xSize || y < 0 || y > ySize){
            return null;
        }else{
            return map[y][x];
        }
    }

}
