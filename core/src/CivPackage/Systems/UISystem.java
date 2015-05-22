package CivPackage.Systems;

import CivPackage.GameProject;
import CivPackage.Map.GameMap;
import CivPackage.Models.Hex;
import CivPackage.Util.Point;
import CivPackage.Screens.GameScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Array;


/**
 * A class for managing the UI
 * Created by james on 7/1/2014.
 */
public class UISystem {

    private GameMap map;
    private UnitManagementSystem ums;
    private PlayerSystem playerSystem;
    private Stage stage;
    private PathfindingSystem pfs;
    private Hex selected;       //currently selected hex
    public Hex debugSelect;
    private Array<Hex> surrounding;
    private Array<Hex> path;
    private Array<Integer> turns;

    private Table table;
    private Skin skin;
    private BitmapFont font;

    //debug stuff
    private boolean debug = false;
    private Table debugTable;
    private TextButton debugOutput;

    public UISystem(GameMap map, UnitManagementSystem ums, PlayerSystem playerSystem, Stage stage){
        this.map = map;
        this.ums = ums;
        this.stage = stage;
        this.playerSystem = playerSystem;
        pfs = new PathfindingSystem(map);
        surrounding = new Array<>();
        path = new Array<>();
        turns = new Array<>();

        skin = new Skin(Gdx.files.internal(GameProject.fileName + "uiskin.json"));
        table = new Table();    //add skin
        table.setPosition(50,650);
        debugTable = new Table();
        debugTable.setPosition(500, 300);
        font = new BitmapFont();
        createUi();
    }

    private void createUi(){
        table.debug();
        stage.addActor(table);
        TextButton science = new TextButton("Science +" + playerSystem.getResearch(), skin);
        table.add(science).left().top().expand();
        table.row();
        TextButton gold = new TextButton("Gold: " + playerSystem.getGold() + "(+" + playerSystem.getIncome() +")", skin);
        table.add (gold).left().top();

        //debug stuff
        debugOutput = new TextButton("", skin);
        debugTable.add(debugOutput);
    }

    /**
     * Sets a path from the currently selected hex to the specified hex
     * @param h; Hex to move to
     */
    public void setPath(Hex h){
        if (selected != null && selected.getUnit() != null) {
            turns.clear();
            if (h != null) {
                path = pfs.getPath(selected.getMapX(), selected.getMapY(), h.getMapX(), h.getMapY());   //gets the path
                if (path.size > 0)  //gets rid of the current tile from being selected in the path
                {
                    path.pop();
                }

                int maxMove = selected.getUnit().getMaxMovement();
                int move = selected.getUnit().getMovement();
                int turn = 1;
                for (int i = path.size - 1; i >= 0; i--) {
                    move -= path.get(i).getCost();
                    turns.add(turn);
                    if (move <= 0) {
                        move = maxMove;
                        turn++;
                    }
                }
            }
        }
    }

    /**
     * Cancels the tile selection
     */
    public void cancelSelection(){
        selected = null;
        surrounding.clear();
        path.clear();
        turns.clear();

        //debug stuff
        debugSelect = null;
        debugOutput.setText("");
    }

    public void setDebug(boolean b){
        debug = b;
        if (debug) {
            debugTable.setPosition(900, 500);
            stage.addActor(debugTable);
        }
        else {
            debugTable.remove();
        }
    }

    /**
     * Selects the passed on hex
     * @param selected
     */
    public void selectHex(Hex selected){
        //if theres nothing selected, and the selection is valid, and there is a unit on that hex, and that unit can move
        if (this.selected == null && selected != null){
            if (selected.getUnit() != null && selected.getUnit().getMovement() > 0) {

                this.selected = selected;
                System.out.println("Unit: " + selected.getUnit());

                int x = selected.getMapX();
                int y = selected.getMapY();
                surrounding = pfs.getHexInRange(x, y, map.getHex(x, y).getUnit().getMovement());
                //gets rid of the current tile on the selection
                surrounding.removeValue(map.getHex(x, y), false);
            }
        }else if (selected != this.selected){ // ----- Not sure if this should be in UISystem or UnitManagementSystem
            //if the surrounding tile contains the newly selected tile, and its not the same tile
            if (surrounding.contains(selected,false)){
                float d = 0;
                for (Hex h: path){
                    if (surrounding.contains(h, false)){
                        d += h.getCost();       //gets the cost of moving from the current tile to the new tile
                    }
                }
                //moves the unit from the old tile to the new tile
                ums.moveUnit(this.selected.getUnit(), selected.getMapX(), selected.getMapY(), d);
            }else if (path.size > 0){ //if a path is defined; if the user selects a tile outside of the walkable range
                float d = 0;
                for (int i = path.size-1; i >= 0; i--){
                    if (surrounding.contains(path.get(i), false)){
                        d += path.get(i).getCost();                 //gets the cost it takes to move from current tile to new tile
                    }
                    else{
                        //if the surrounding tiles dosnt contain the path anymore, than the last path is the max in the movement range
                        ums.moveUnit(this.selected.getUnit(), path.get(i+1).getMapX(), path.get(i+1).getMapY(), d);
                    }
                }
            }
            cancelSelection();
        }
        path.clear();
        if (GameScreen.getDebug()){
            debugSelect = selected;
            if (debugSelect != null) {
                /*System.out.println("----------------------------");
                System.out.println("x: " + debugSelect.getMapX() + " y: " + debugSelect.getMapY());
                System.out.println("Landtype: " + debugSelect.landType);
                System.out.println("Elevation: " + debugSelect.elevation);
                System.out.println("Feature: " + debugSelect.feature);*/
                String s = "x: " + debugSelect.getMapX() + " y: " + debugSelect.getMapY() + "\n"
                        + "Landtype: " + debugSelect.landType + "\n"
                        + "Elevation: " + debugSelect.elevation + "\n"
                        + "Feature: " + debugSelect.feature + "\n"
                        + "Wonder: " + debugSelect.getWonder();

                debugOutput.setText(s);
                //System.out.println("Has Fresh water " + debugSelect.freshWater);
            }
        }
    }

    public boolean getDebug(){return debug;}
    public Hex getSelectedHex(){return selected;}
    public Array<Hex> getSurrounding(){return surrounding;}
    public Array<Hex> getPath(){return path;}
    public Table getTable(){return table;}
    public int getTurn(int i){return turns.get(i);}
    public Array<Integer> getTurns(){return turns;}
}
