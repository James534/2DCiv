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

    public Hex getPixelHex(float pixelX, float pixelY){
        float SectX = pixelX/52;
        float SectY = pixelY/45;

        float sectPxlX = pixelX % 52;
        float sectPixY = pixelY % 45;

        float m = 15/26f;

        float selX = SectX;
        float selY = SectY;

        //http://www.gamedev.net/page/resources/_/technical/game-programming/coordinates-in-hexagon-based-tile-maps-r1800
        if ((int)SectY % 2 == 0){  //A TYPE
            //left
            if (sectPixY < (15 - sectPxlX*m)){
                selX = SectX  - 1;
                selY = SectY  - 1;
            }
            //right
            if (sectPixY < (-15 + sectPxlX*m)){
                selY = SectY - 1;
            }
        }else{                      //B TYPE
            //right side
            if (sectPxlX >= 26){
                if (sectPixY < (2*15 - sectPxlX*m)){
                    selY = SectY - 1;
                }
            }
            //left side
            if (sectPxlX < (26)){
                if (sectPixY < (sectPxlX*m)){
                    selY = SectY - 1;
                }else{
                    selX = SectX - 1;
                }
            }
        }
        if (selX < 0 || selY < 0){  //if its out of bounds, return null
            return null;
        }
        int intX = (int)selX;
        int intY = (int)selY;

        if (getHex(intX, intY) != null){
            getHex(intX, intY).selected(true);
        }

        return (getHex(intX, intY));
    }

    public Hex getHex(int x, int y){
        if (x < 0 || x >= xSize || y < 0 || y >= ySize){
            return null;
        }else{
            return map[y][x];
        }
    }

}
