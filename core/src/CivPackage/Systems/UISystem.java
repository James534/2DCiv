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
    private Hex selected;       //currently selected hex
    private Array<Hex> surrounding;

    private Table table;

    public UISystem(GameMap map, UnitManagementSystem ums, Stage stage){
        this.map = map;
        this.ums = ums;
        this.stage = stage;
        table = new Table();    //add skin
        surrounding = new Array<Hex>();
    }

    private void showMovement(){
        int x = selected.getMapX();
        int y = selected.getMapY();
        if (selected.getEven()) {
            surrounding.add(map.getHex(x, y - 1));
            surrounding.add(map.getHex(x, y + 1));
            surrounding.add(map.getHex(x - 1, y));
            surrounding.add(map.getHex(x + 1, y));
            surrounding.add(map.getHex(x - 1, y - 1));
            surrounding.add(map.getHex(x - 1, y + 1));
        }else{
            surrounding.add(map.getHex(x, y - 1));
            surrounding.add(map.getHex(x, y + 1));
            surrounding.add(map.getHex(x - 1, y));
            surrounding.add(map.getHex(x + 1, y));
            surrounding.add(map.getHex(x + 1, y - 1));
            surrounding.add(map.getHex(x + 1, y + 1));
        }
        for (Hex h: surrounding){
            h.selected(true);
        }
    }

    public void cancelSelection(){
        selected = null;
        for (Hex h: surrounding){
            h.selected(false);
        }
        surrounding.clear();
    }

    public void selectHex(Hex selected){
        if (this.selected == null && selected.getUnit() != null) {
            this.selected = selected;
            System.out.println("Unit: " + selected.getUnit());
            showMovement();
        }else{
            if (surrounding.contains(selected,false)){
                ums.moveUnit(this.selected.getUnit(), selected.getMapX(), selected.getMapY());
            }
            cancelSelection();
        }
    }

    public Hex getSelectedHex(){
        return selected;
    }

}
