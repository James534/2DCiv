package CivPackage;

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
}
