package CivPackage.Util;

import CivPackage.Models.Hex;
import com.badlogic.gdx.utils.Array;

/**
 * Created by james on 5/21/2015.
 */
public class DebugClass {
    public static Array<Point> startingPoints;
    public static Array<Array<Hex>> landPatch = new Array<>();
    public static boolean debugGeneration = true;

    public static void generateLog(String s){
        if (debugGeneration)
            System.out.println(s);
    }
}
