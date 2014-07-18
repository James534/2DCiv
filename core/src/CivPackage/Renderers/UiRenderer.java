package CivPackage.Renderers;

import CivPackage.Models.Entity;
import CivPackage.Models.Hex;
import CivPackage.Systems.UISystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Lu on 2014-07-16.
 */
public class UiRenderer{

    private OrthographicCamera cam;
    private UISystem uis;
    private SpriteBatch batch;

    public UiRenderer(OrthographicCamera camera, UISystem uiSystem, SpriteBatch batch){
        cam = camera;
        uis = uiSystem;
        this.batch = batch;
    }

    public void render(){
        batch.setProjectionMatrix(cam.combined);
        batch.begin();

        for (Hex h: uis.getSurrounding()){
            batch.draw(Hex.highlighted, h.getPixelPos().x, h.getPixelPos().y);
        }

        batch.end();
    }

}
