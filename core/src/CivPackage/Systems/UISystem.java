package CivPackage.Systems;

import CivPackage.Map.GameMap;
import CivPackage.Models.Hex;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;

/**
 * Created by james on 7/1/2014.
 */
public class UISystem {

    private GameMap map;
    private UnitManagementSystem ums;
    private Stage stage;
    private PathfindingSystem pfs;
    private Hex selected;       //currently selected hex
    private Array<Hex> surrounding;
    private Array<Hex> path;

    private Table table;

    public UISystem(GameMap map, UnitManagementSystem ums, Stage stage){
        this.map = map;
        this.ums = ums;
        this.stage = stage;
        pfs = new PathfindingSystem(map);
        table = new Table();    //add skin
        surrounding = new Array<Hex>();
        path = new Array<Hex>();
    }

    public void setPath(Hex h){
        if (path.size > 0){
            for (Hex hex: path){
                hex.selected(false);
            }
        }
        path = pfs.getPath(selected.getMapX(), selected.getMapY(), h.getMapX(), h.getMapY());
        for (Hex hex: path){
            hex.selected(true);
        }
    }

    private void showMovement(){
        int x = selected.getMapX();
        int y = selected.getMapY();
        surrounding = pfs.getHexInRange(x, y, map.getHex(x,y).getUnit().getMovement());
        /*for (Hex h: surrounding){
            h.selected(true);
        }*/
    }

    public void cancelSelection(){
        selected = null;
        /*for (Hex h: surrounding){
            h.selected(false);
        }*/
        surrounding.clear();
    }

    public void selectHex(Hex selected){
        if (this.selected == null && selected.getUnit() != null) {
            this.selected = selected;
            System.out.println("Unit: " + selected.getUnit());
            showMovement();
        }else{
            if (surrounding.contains(selected,false) && selected != this.selected){
                ums.moveUnit(this.selected.getUnit(), selected.getMapX(), selected.getMapY());
            }
            cancelSelection();
        }
    }

    public Hex getSelectedHex(){
        return selected;
    }
    public Array<Hex> getSurrounding(){return surrounding;}
}
