package CivPackage.Renderers;

import CivPackage.Models.Entity;
import CivPackage.Systems.UnitManagementSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by james on 7/1/2014.
 */
public class UnitRenderer {

    private OrthographicCamera cam;
    private UnitManagementSystem ums;
    private SpriteBatch batch;

    public UnitRenderer(OrthographicCamera camera, UnitManagementSystem unitManagementSystem, SpriteBatch batch){
        cam = camera;
        ums = unitManagementSystem;
        this.batch = batch;
    }

    public void render(){
        batch.setProjectionMatrix(cam.combined);
        batch.begin();

        for (Entity e: ums.getUnits()){
            batch.draw(e.getTexture(), e.getPixelPos().x, e.getPixelPos().y);
        }

        batch.end();
    }
}
