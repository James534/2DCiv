package CivPackage.Renderers;

import CivPackage.Util.Assets;
import CivPackage.Util.DebugClass;
import CivPackage.Util.MathCalc;
import CivPackage.Models.Hex;
import CivPackage.Util.Point;
import CivPackage.Screens.GameScreen;
import CivPackage.Systems.UISystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import sun.security.ssl.Debug;

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
        //Table.drawDebug(stage);
    }

    public void render(){
        //draws ui elements
        stage.act();
        stage.draw();

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

        if (GameScreen.getDebug()){
            if (uis.debugSelect != null) {
                batch.draw(Hex.SELECTED2, uis.debugSelect.getPixelPos().x, uis.debugSelect.getPixelPos().y);
            }

            if (DebugClass.landPatch.size > 0)
                for (Array<Hex> a: DebugClass.landPatch)
                    for (Hex h: a)
                        batch.draw(Hex.SELECTED2, h.getPixelPos().x, h.getPixelPos().y);

            for (Point p: DebugClass.startingPoints){
                Point pPos = MathCalc.getPixelPos(p);
                batch.draw(Hex.SELECTED, pPos.x, pPos.y);
            }


            int loc = 64;

            //batch.draw(Hex.ICONS[1], loc -Hex.ICONS[1].getWidth()/2, loc -Hex.ICONS[1].getHeight()/2);
            //batch.draw(Hex.ICONS[0], loc -Hex.ICONS[0].getWidth()/2, loc);
            //batch.draw(Hex.ICONS[0], loc -Hex.ICONS[0].getWidth()/2, loc -Hex.ICONS[0].getHeight());
            batch.draw(Hex.ICONS[2], 128 - Hex.ICONS[2].getWidth()/2, 128 - Hex.ICONS[2].getHeight()/2);
        }

        batch.end();
    }

}
