package CivPackage.Renderers;

import CivPackage.Assets;
import CivPackage.Models.Entity;
import CivPackage.Models.Hex;
import CivPackage.Systems.UISystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

/**
 * Created by Lu on 2014-07-16.
 */
public class UiRenderer{

    private OrthographicCamera cam;
    private UISystem uis;
    private SpriteBatch batch;

    private Stage stage;
    private BitmapFont font;
    private Texture[] num;

    public UiRenderer(OrthographicCamera camera, UISystem uiSystem, SpriteBatch batch, Stage stage){
        cam = camera;
        uis = uiSystem;
        this.batch = batch;

        this.stage = stage;
        font = new BitmapFont();
        num = new Texture[33];
        for (int i = 0; i < num.length; i++){
            num[i] = Assets.drawText(Integer.toString(i), new Texture(Hex.PATH));
        }
    }

    public void render(){
        //draws ui elements
        stage.act();
        stage.draw();
        Table.drawDebug(stage);

        batch.setProjectionMatrix(cam.combined);
        batch.begin();

        //renders the tiles the selected unit can move to
        for (Hex h: uis.getSurrounding()){
            batch.draw(Hex.SELECTED, h.getPixelPos().x, h.getPixelPos().y);
        }

        //renders the path
        int i = uis.getTurns().size;
        for (Hex h: uis.getPath()){
            i--;
            batch.draw(num[uis.getTurn(i)], h.getPixelPos().x, h.getPixelPos().y);
        }

        batch.end();
    }

}
