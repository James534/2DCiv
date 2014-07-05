package CivPackage.Systems;

import CivPackage.Map.GameMap;
import CivPackage.Models.Hex;
import com.badlogic.gdx.scenes.scene2d.Stage;

/**
 * Created by james on 7/1/2014.
 */
public class UISystem {

    private GameMap map;
    private Stage stage;
    private Hex selected;       //currently selected hex

    public UISystem(GameMap map, Stage stage){
        this.map = map;
    }

    public void selectHex(Hex selected){
        this.selected = selected;

    }

    public Hex getSelectedHex(){
        return selected;
    }
}
