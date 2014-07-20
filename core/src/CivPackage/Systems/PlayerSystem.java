package CivPackage.Systems;

/**
 * Created by Lu on 2014-07-19.
 */
public class PlayerSystem {

    private UnitManagementSystem unitManagementSystem;

    private float research;
    private float gold;
    private float income;

    public PlayerSystem(){
        research = 100;
        gold = 500;
        income = 5;
    }

    public float getResearch(){return research;}
    public float getGold(){return gold;}
    public float getIncome(){return income;}
}
