package CivPackage.Renderers;

import CivPackage.Map.GameMap;
import CivPackage.Models.Hex;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
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
                int x0 = x * Hex.HexD;
                int y0 = y * Hex.HexHS;
                batch.draw(gameMap.getHex(x,y).getTexture(), x0, y0);
                x0 += Hex.HexR;
                y0 += Hex.HexHS;
                batch.draw(gameMap.getHex(x,y+1).getTexture(), x0, y0);
            }
        }
        //because diamond square generates an odd valued array, i have to render the last row by itself
        for (int x = 0; x < gameMap.xSize; x++){
            batch.draw(gameMap.getHex(x,gameMap.ySize-1).getTexture(), x*Hex.HexD, (gameMap.ySize-1)*Hex.HexHS);
        }


        batch.end();
    }
}
