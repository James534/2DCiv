package CivPackage.Util;

/**
 * A class to hold an x and y position of a point
 * Created by james on 7/30/2014.
 */
public class Point {

    public int x, y;
    public String data;

    public Point(int x, int y){
        this.x = x;
        this.y = y;
    }

    public Point (int x, int y, String data){
        this (x, y);
        this.data = data;
    }
}
