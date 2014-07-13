package CivPackage.Systems;

import CivPackage.Map.GameMap;
import CivPackage.Models.Hex;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Lu on 2014-07-13.
 */
public class PathfindingSystem {

    private GameMap map;
    private Node[][] nodeMap;
    public PathfindingSystem(GameMap map){
        this.map = map;

    }

    /**
     * Returns an array of the hexes in range of the specified hex
     * @param x x of the current/specified hex
     * @param y
     * @param range
     * @return
     */
    public Array<Hex> getHexInRange(int x, int y, int range){
        Array<Hex> path = new Array<Hex>();
        getHexInRange(x,y,range,path);
        return path;
    }
    public void getHexInRange(int x, int y, int range, Array<Hex> path){
        Array<Hex> open = new Array<Hex>();
        for (Hex h: getNeighbours(x,y)) {
            if (!path.contains(h, true) && h.getWalkable()) {
                path.add(h);
                open.add(h);
            }
        }
        for (Hex h: open){
            if (range-h.getCost() > 0){
                getHexInRange(h.getMapX(), h.getMapY(), range - h.getCost(), path);
            }
        }
    }

    private Array<Hex> getNeighbours(int x, int y){
        Array<Hex> neighbours = new Array<Hex>(6);
        if (map.getHex(x, y).getEven()) {
            neighbours.add(map.getHex(x, y - 1));
            neighbours.add(map.getHex(x, y + 1));
            neighbours.add(map.getHex(x - 1, y));
            neighbours.add(map.getHex(x + 1, y));
            neighbours.add(map.getHex(x - 1, y - 1));
            neighbours.add(map.getHex(x - 1, y + 1));
        }else{
            neighbours.add(map.getHex(x, y - 1));
            neighbours.add(map.getHex(x, y + 1));
            neighbours.add(map.getHex(x - 1, y));
            neighbours.add(map.getHex(x + 1, y));
            neighbours.add(map.getHex(x + 1, y - 1));
            neighbours.add(map.getHex(x + 1, y + 1));
        }
        return neighbours;
    }
    private Array<Node> getNeighboutNode(int x, int y){
        Array<Node> neighbour = new Array<Node>(6);
        return neighbour;
    }

    public void updateNodes(){
        for (int y = 0; y < nodeMap.length; y++){
            for (int x = 0; x < nodeMap[0].length; x++){

            }
        }
    }

    private class Node{
        private int x, y;
        private boolean walkable;
        private float cost;
        private Node parent;

    }
}
