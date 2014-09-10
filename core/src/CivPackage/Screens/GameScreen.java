package CivPackage.Screens;

import CivPackage.Systems.*;
import CivPackage.Map.GameMap;
import CivPackage.Renderers.MapRenderer;
import CivPackage.Renderers.UiRenderer;
import CivPackage.Renderers.UnitRenderer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
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
    private UiRenderer uiRenderer;

    private InputMultiplexer multiplexer;
    private InputHandleSystem inputHandleSystem;
    private CameraMovementSystem cameraMovementSystem;
    private UnitManagementSystem unitManagementSystem;
    private UISystem uiSystem;
    private PlayerSystem playerSystem;


    public GameScreen(){
        gameMap = new GameMap(65,65);
        camera = new OrthographicCamera();
        batch = new SpriteBatch();
        stage = new Stage();

        //systems
        playerSystem = new PlayerSystem();
        unitManagementSystem = new UnitManagementSystem(0, gameMap);     //creates a new unit management system for the human player
        uiSystem = new UISystem(gameMap, unitManagementSystem,playerSystem, stage);
        cameraMovementSystem = new CameraMovementSystem(camera,
                gameMap.getHex(0,0).getPixelPos().x,gameMap.getHex(0,0).getPixelPos().y,
                gameMap.getHex(gameMap.xSize-1,0).getPixelPos().x, gameMap.getHex(0,gameMap.ySize-1).getPixelPos().y);

        //renderers
        mapRenderer = new MapRenderer(camera, gameMap, batch);
        unitRenderer = new UnitRenderer(camera, unitManagementSystem, batch);
        uiRenderer = new UiRenderer(camera,uiSystem,batch, stage);

        //other stuff
        multiplexer = new InputMultiplexer();
        inputHandleSystem = new InputHandleSystem(cameraMovementSystem, uiSystem, gameMap);
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(inputHandleSystem);
        Gdx.input.setInputProcessor(multiplexer);


        //testing
        //unitManagementSystem.createUnit(1, 5, 5);
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
        uiRenderer.render();
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