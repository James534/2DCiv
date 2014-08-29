package CivPackage.Map;

import CivPackage.Models.Entity;
import CivPackage.Models.Hex;
import CivPackage.Point;
import CivPackage.Random;
import CivPackage.Systems.TerrainGenerationSystem;
import com.badlogic.gdx.utils.Array;

/**
 * Created by james on 6/30/2014.
 */
public class GameMap {

    private Hex[][] map;
    public final int xSize, ySize;

    public GameMap(int xSize, int ySize){
        this.xSize = xSize;
        this.ySize = ySize;

        Random r = new Random(1047155);
        map = r.generateHexMap(xSize, ySize);
        Array<Point> startingPoints = r.getStartingPoints();
        /*TerrainGenerationSystem tgs = new TerrainGenerationSystem(1047155);
        int[][] heightMap = r.generateTerrain(xSize,ySize);
        //int[][] heightMap = r.generateTerrainPerlin(xSize, ySize);
        //int[][] heightMap = tgs.generatePerlin(xSize, ySize);

        map = new Hex[ySize][xSize];
        for (int y = 0; y < map.length; y++){
            for (int x = 0; x < map[0].length;x++){
                map[y][x] = new Hex(heightMap[y][x], x, y);
            }
        }*/
    }

    public void addUnit(Entity unit){
        getHex((int)unit.getPos().x, (int)unit.getPos().y).addUnit(unit);
    }

    /**
     * Gets the hex that is at the pixel coordinate parameters
     * @param pixelX
     * @param pixelY
     * @return
     */
    public Hex getPixelHex(float pixelX, float pixelY){
        float SectX = pixelX/Hex.HexD;
        float SectY = pixelY/Hex.HexHS;

        float sectPxlX = pixelX % Hex.HexD;
        float sectPixY = pixelY % Hex.HexHS;

        float m = (float)Hex.HexH/(float)Hex.HexR;

        float selX = SectX;
        float selY = SectY;

        //http://www.gamedev.net/page/resources/_/technical/game-programming/coordinates-in-hexagon-based-tile-maps-r1800
        if ((int)SectY % 2 == 0){  //A TYPE
            //left
            if (sectPixY < (Hex.HexH - sectPxlX*m)){
                selX = SectX  - 1;
                selY = SectY  - 1;
            }
            //right
            if (sectPixY < (-Hex.HexH + sectPxlX*m)){
                selY = SectY - 1;
            }
        }else{                      //B TYPE
            //right side
            if (sectPxlX >= Hex.HexR){
                if (sectPixY < (2*Hex.HexH - sectPxlX*m)){
                    selY = SectY - 1;
                }
            }
            //left side
            if (sectPxlX < (Hex.HexR)){
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

        //System.out.println ("Hex:" + intX + " " + intY);
        return (getHex(intX, intY));
    }

    /**
     * Gets the hex at the map grid coordinates
     * @param x
     * @param y
     * @return
     */
    public Hex getHex(int x, int y){
        if (x < 0 || x >= xSize || y < 0 || y >= ySize){
            return null;
        }else{
            return map[y][x];
        }
    }
}
