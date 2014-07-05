package CivPackage.Screens;

import CivPackage.InputHandler;
import CivPackage.Map.GameMap;
import CivPackage.Models.Hex;
import CivPackage.Renderers.MapRenderer;
import CivPackage.Renderers.UnitRenderer;
import CivPackage.Systems.CameraMovementSystem;
import CivPackage.Systems.UISystem;
import CivPackage.Systems.UnitManagementSystem;
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
    private UnitRenderer unitRenderer;
    private InputHandler inputHandler;
    private CameraMovementSystem cameraMovementSystem;
    private UnitManagementSystem unitManagementSystem;
    private UISystem uiSystem;


    public GameScreen(){
        gameMap = new GameMap(32,32);
        camera = new OrthographicCamera();
        batch = new SpriteBatch();
        stage = new Stage();

        //systems
        unitManagementSystem = new UnitManagementSystem(0, gameMap);     //creates a new unit management system for the human player
        uiSystem = new UISystem(gameMap, stage);
        cameraMovementSystem = new CameraMovementSystem(camera,
                gameMap.getHex(0,0).getPixelPos().x,gameMap.getHex(0,0).getPixelPos().y,
                gameMap.getHex(gameMap.xSize-1,0).getPixelPos().x, gameMap.getHex(0,gameMap.ySize-1).getPixelPos().y);

        //renderers
        mapRenderer = new MapRenderer(camera, gameMap, batch);
        unitRenderer = new UnitRenderer(camera, unitManagementSystem, batch);

        //other stuff
        inputHandler = new InputHandler(cameraMovementSystem, this);
        Gdx.input.setInputProcessor(inputHandler);


        //testing
        unitManagementSystem.createUnit(1, 5, 5);
    }

    public void selectHex(float pixelX, float pixelY){
        uiSystem.selectHex(gameMap.getPixelHex(pixelX, pixelY));
        if (uiSystem.getSelectedHex() != null) {
            cameraMovementSystem.moveCamTo(uiSystem.getSelectedHex().getPixelPos().x + Hex.HexR, uiSystem.getSelectedHex().getPixelPos().y + Hex.HexR);
        }
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
        unitRenderer.render();
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