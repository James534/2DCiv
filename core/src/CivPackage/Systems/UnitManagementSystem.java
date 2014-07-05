package CivPackage.Systems;

import CivPackage.Map.GameMap;
import CivPackage.Models.Entity;
import CivPackage.Models.Warrior;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * This class keeps track of all the units of one player
 * Created by james on 7/1/2014.
 */
public class UnitManagementSystem {

    private GameMap gameMap;

    private int id;                 //id of the player; human player has an id of 0
    private Array<Entity> units;
    private Entity entity;
    private int unitId;             //keep track of the units, sort them by their unit ids; unique to each unit

    public UnitManagementSystem(int id, GameMap gameMap){
        this.gameMap = gameMap;
        this.id = id;
        units = new Array<Entity>();
        unitId = 0;
    }

    public void createUnit(int classId, int x, int y){
        unitId ++;
        if (classId == 1) {
            entity = new Warrior(new Vector2(x,y), unitId);
            units.add(entity);
            gameMap.addUnit(entity);
            entity = null;
        }
    }

    public Array<Entity> getUnits(){
        return units;
    }

}
