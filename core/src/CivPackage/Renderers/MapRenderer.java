package CivPackage.Renderers;

import CivPackage.Map.GameMap;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by james on 6/30/2014.
 */
public class MapRenderer {

    private OrthographicCamera camera;
    private GameMap gameMap;
    private SpriteBatch batch;

    public MapRenderer(OrthographicCamera cam, GameMap gameMap, SpriteBatch batch){
        camera = cam;
        this.gameMap = gameMap;
        this.batch = batch;
    }

    public void render(){
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        for (int x = 0; x < gameMap.xSize; x++){
            for (int y = 0; y < gameMap.ySize-1; y+=2){
                int x0 = 5 + x * 56;
                int y0 = y * 41;
                batch.draw(gameMap.getHex(x,y).getTexture(), x0, y0);
                x0 += 28;
                y0 += 41;
                batch.draw(gameMap.getHex(x,y+1).getTexture(), x0, y0);
            }
        }

        batch.end();
    }
}
