package CivPackage.Models;

import CivPackage.MathCalc;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by james on 6/30/2014.
 */
public class Entity extends Actor{

    protected Texture texture;
    protected Vector2 pos;          //position in tile coordinate
    protected Vector2 pixelPos;     //position in pixel coordinate

    protected int id;               //unit id, unique to each unit
    protected int type;             //what type of unit this is; 1 is land, 2 is sea, 3 is air
    protected boolean fighting;
    protected int hp;
    protected int maxMovement;      //how much this unit can move
    protected int movement;
    protected int exp;
    protected int level;
    protected int range;            //1 for melee, 2+ for ranged

    public Entity (Vector2 pos, int id, int hp, int movement){
        setMapPos(pos);
        this.id = id;
        this.hp = hp;
        this.maxMovement = movement;
        this.movement = maxMovement;
        exp = 0;
        level = 1;
    }

    public void setMapPos(Vector2 pos){
        this.pos = pos;
        pixelPos = MathCalc.getPixelPos(pos);
        pixelPos.x += 30;
        pixelPos.y += 20;
    }

    public boolean move(float d){
        if (movement - d >= 0){
            movement -= d;
            return true;
        }else{
            return false;
        }
    }
    public void resetMovement(){
        movement = maxMovement;
    }

    public void draw (SpriteBatch batch, float Alpha){

    }


    public Texture getTexture(){return texture;}
    public Vector2 getPos(){return pos;}
    public int getMapX(){return (int)pos.x;}
    public int getMapY(){return (int)pos.y;}
    public Vector2 getPixelPos(){return pixelPos;}
    public int getMaxMovement(){return maxMovement;}
    public int getMovement(){return movement;}

}
