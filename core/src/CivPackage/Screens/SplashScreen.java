package CivPackage.Screens;

import CivPackage.GameProject;
import CivPackage.Screens.GameScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/**
 * Created by james on 6/29/2014.
 */
public class SplashScreen implements Screen{

    private Texture texture = new Texture(Gdx.files.internal(GameProject.fileName + "Splash.png"));
    private Image splashImg = new Image(texture);
    private Stage stage = new Stage();



    @Override
    public void show() {
        splashImg.setWidth(GameProject.WIDTH);
        splashImg.setHeight(GameProject.HEIGHT);
        stage.addActor(splashImg);
        splashImg.addAction(Actions.sequence(Actions.alpha(0),Actions.fadeIn(0.1f),Actions.delay(0.1f),Actions.run(new Runnable() {
            @Override
            public void run() {
                ((Game)Gdx.app.getApplicationListener()).setScreen(new GameScreen());
            }
        })));

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1);               //sets color to black
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);   //clear the batch
        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        texture.dispose();
        stage.dispose();
    }
}
