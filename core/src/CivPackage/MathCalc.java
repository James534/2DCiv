package CivPackage;

import CivPackage.Models.Hex;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by james on 6/29/2014.
 */
public class MathCalc {

    public static final float PI = 3.14159f;

    public static float CalculateH(float side){
        return (float)Math.sin(PI/6) * side;
    }

    public static float CalculateR(float side){
        return (float)Math.cos(PI/6) * side;
    }

    public static double DegToRad(double degree){
        return degree * PI / 180;
    }

    /**
     * Converts map coordinates to pixel coordinates
     * @param pos, map coordinate
     * @return pixel coordinates
     */
    public static Vector2 getPixelPos (Vector2 pos){
        Vector2 pixelPos = new Vector2();
        pixelPos.x = pos.x * Hex.HexD + (pos.y %2)*Hex.HexR;
        pixelPos.y = pos.y * Hex.HexHS;
        return pixelPos;
    }

    /**
     * Returns the distance between the 2 points in a hex grid
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public static int distanceBetween(int x1, int y1, int x2, int y2){
        return (int)Math.max (Math.abs(y2 - y1),
                Math.abs( (x2+Math.floor(y2/2f)) - (x1+Math.floor(y1/2f)) ));
    }
}
