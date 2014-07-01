package CivPackage.Screens;

import CivPackage.InputHandler;
import CivPackage.Map.GameMap;
import CivPackage.Renderers.MapRenderer;
import CivPackage.Systems.CameraMovementSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;


/**
 * Created by james on 6/29/2014.
 */
public class GameScreen implements Screen{

    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Stage stage;

    private GameMap gameMap;
    private MapRenderer mapRenderer;
    private InputHandler inputHandler;
    private CameraMovementSystem cameraMovementSystem;


    public GameScreen(){

        gameMap = new GameMap(32,32);
        camera = new OrthographicCamera();
        batch = new SpriteBatch();
        stage = new Stage();

        cameraMovementSystem = new CameraMovementSystem(camera,
                gameMap.getHex(0,0).getPos().x,gameMap.getHex(0,0).getPos().y,
                gameMap.getHex(gameMap.xSize-1,0).getPos().x, gameMap.getHex(0,gameMap.ySize-1).getPos().y);
        mapRenderer = new MapRenderer(camera, gameMap, batch);
        inputHandler = new InputHandler(cameraMovementSystem, this);
        Gdx.input.setInputProcessor(inputHandler);
    }

    public void selectHex(float pixelX, float pixelY){
        gameMap.getPixelHex(pixelX, pixelY);
    }

    @Override
    public void hide() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);               //sets color to black
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);   //clear the batch
        cameraMovementSystem.update();
        mapRenderer.render();
    }


    @Override
    public void resume() {

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void dispose() {

    }
}