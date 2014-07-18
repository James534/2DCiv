package CivPackage.Systems;

import CivPackage.Map.GameMap;
import CivPackage.Models.Hex;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Lu on 2014-07-13.
 */
public class PathfindingSystem {

    private GameMap map;
    private Node[][] nodes;
    private boolean[][] nodeMap;

    private Array<Node> open, closed;
    private int ex, ey;

    public PathfindingSystem(GameMap map){
        this.map = map;
        open = new Array<Node>();
        closed = new Array<Node>();
        reset();
    }

    /**
     * Finds the best path using A*
     * @param sx
     * @param sy
     * @param ex
     * @param ey
     * @return
     */
    public Array<Hex> getPath(int sx, int sy, int ex, int ey){
        reset();
        this.ex = ex;
        this.ey = ey;
        Array<Hex> path = new Array<Hex>();
        Array<Node> adj = new Array<Node>();

        nodes[sy][sx] = new Node(sx, sy);
        open.add(nodes[sy][sx]);

        while (open.size > 0){
            Node best = findBestNode();
            open.removeValue(best, true);
            closed.add(best);

            if (best.x == ex && best.y == ey){
                //FOUND THE END
                Node t = best;
                while (t != null){
                    path.add (map.getHex(t.x, t.y));
                    t = t.parent;
                }
                return path;
            }else{
                adj = getNeighbourNode(best.x,best.y);

                for (Node n: adj){
                    if (n.walkable) {
                        if (open.contains(n, false)) {
                            Node temp = new Node(n.x, n.y, n);
                            if (temp.getFinalCost() >= n.getFinalCost()) {
                                continue;
                            }
                        } else if (closed.contains(n, false)) {
                            Node temp = new Node(n.x, n.y, n);
                            if (temp.getFinalCost() >= n.getFinalCost()) {
                                continue;
                            }
                        }

                        //n.parent = best;
                        open.removeValue(n, false);
                        closed.removeValue(n, false);
                        open.add(n);
                    }
                }
            }
        }

        return path;
    }

    private Node findBestNode(){
        Node best = open.get(0);
        for (Node n: open){
            if (n.getFinalCost() < best.getFinalCost()){
                best = n;
            }
        }
        return best;
    }

    private void reset(){
        open.clear();
        closed.clear();
        nodeMap = new boolean[map.ySize][map.xSize];
        nodes = new Node[map.ySize][map.xSize];
        ex = 0;
        ey = 0;
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
    private void getHexInRange(int x, int y, float range, Array<Hex> path){
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

    private void checkNode(int x, int y, Node p){
        if (x>=0 && x <nodes[0].length && y >= 0 && y <nodes.length) {
            if (nodes[y][x] == null) {
                nodes[y][x] = new Node(x, y, p);
                nodes[y][x].walkable = map.getHex(x, y).getWalkable();
            }
        }
    }

    /**
     * Returns a list of Nodes adjacent to the specified Node <br>
     *     Creates the nodes if they don't exist already
     * @param x of the specified Node
     * @param y of the specified Node
     * @return Neighbouring Nodes
     */
    private Array<Node> getNeighbourNode(int x, int y){
        Array<Node> neighbour = new Array<Node>(6);
        checkNode(x,y-1,nodes[y][x]);
        checkNode(x,y+1,nodes[y][x]);
        checkNode(x-1,y,nodes[y][x]);
        checkNode(x+1,y,nodes[y][x]);
        addNode(x,y-1,neighbour);
        addNode(x,y+1,neighbour);
        addNode(x-1,y,neighbour);
        addNode(x+1,y,neighbour);
        if (map.getHex(x,y).getEven()){
            checkNode(x-1,y-1,nodes[y][x]);
            checkNode(x-1,y+1,nodes[y][x]);
            addNode(x-1,y-1,neighbour);
            addNode(x-1,y+1,neighbour);
        }else{
            checkNode(x+1,y-1,nodes[y][x]);
            checkNode(x+1,y+1,nodes[y][x]);
            addNode(x+1,y-1,neighbour);
            addNode(x+1,y+1,neighbour);
        }
        return neighbour;
    }

    private void addNode(int x, int y, Array<Node> list){
        if (x>=0 && x<nodes[0].length && y>=0 && y<nodes.length){
            if (nodes[y][x] != null){
                list.add (nodes[y][x]);
            }
        }
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
        private float startCost, endCost, finalCost;
        private Node parent;

        public Node(int sx, int sy){
            x = sx;
            y = sy;
            startCost = 0;
            endCost = getEndCost();
            finalCost = endCost;
        }

        public Node(int sx, int sy, Node p){
            this (sx,sy);
            parent = p;
            startCost = parent.startCost+1;
            finalCost = startCost + endCost;
        }

        private float getFinalCost(){
            return startCost + getEndCost();
        }

        /**
         * Calculates the heuristics using hex distance
         * @return
         */
        private float getEndCost(){
            int dx = x - ex;
            int dy = y - ey;
            int dz = dx - dy;
            //return Math.max(Math.abs(dx), Math.max(Math.abs(dy), Math.abs(dz)));
            return Math.max(Math.abs(dx), Math.abs(dy));
        }
    }
}
