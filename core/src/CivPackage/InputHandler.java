package CivPackage;

import CivPackage.Screens.GameScreen;
import CivPackage.Systems.CameraMovementSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

/**
 * Created by james on 6/30/2014.
 */
public class InputHandler implements InputProcessor{

    private CameraMovementSystem cms;
    private GameScreen screen;

    public InputHandler(CameraMovementSystem cameraMovementSystem, GameScreen screen){
        cms = cameraMovementSystem;
        this.screen = screen;
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode){
            case Input.Keys.W:
                cms.moveCam(0,10);
                cms.setPressY(true);
                break;
            case Input.Keys.S:
                cms.moveCam(0,-10);
                cms.setPressY(true);
                break;
            case Input.Keys.A:
                cms.moveCam(-10,0);
                cms.setPressX(true);
                break;
            case Input.Keys.D:
                cms.moveCam(10,0);
                cms.setPressX(true);
                break;
        }
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        cms.zoomCam(amount);
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT){
            float x = cms.getPos().x - GameProject.WIDTH/2;
            float y = cms.getPos().y + GameProject.HEIGHT/2;

            float pixelX = screenX + x ;
            float pixelY = y - screenY;
            screen.selectHex (pixelX, pixelY);
        }
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode){
            case Input.Keys.W:
                if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                    cms.moveCam(0,-10);
                }else{
                    cms.setPressY(false);
                }
                break;
            case Input.Keys.S:
                if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                    cms.moveCam(0,10);
                }else{
                    cms.setPressY(false);
                }
                break;
            case Input.Keys.A:
                if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                    cms.moveCam(10, 0);
                }else{
                    cms.setPressX(false);
                }
                break;
            case Input.Keys.D:
                if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                    cms.moveCam(-10, 0);
                }else{
                    cms.setPressX(false);
                }
                break;
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }
}
