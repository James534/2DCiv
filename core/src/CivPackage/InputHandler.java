package CivPackage;

import CivPackage.Map.GameMap;
import CivPackage.Screens.GameScreen;
import CivPackage.Systems.CameraMovementSystem;
import CivPackage.Systems.UISystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by james on 6/30/2014.
 */
public class InputHandler implements InputProcessor{

    private CameraMovementSystem cms;
    private UISystem uiSystem;
    private GameMap map;

    public InputHandler(CameraMovementSystem cameraMovementSystem, UISystem uiSystem, GameMap map){
        cms = cameraMovementSystem;
        this.uiSystem = uiSystem;
        this.map = map;
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
            case Input.Keys.ESCAPE:
                uiSystem.cancelSelection();
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

    private float getMapX(int screenX){
        float x = cms.getPos().x - GameProject.WIDTH/2 * cms.getCamZoom();
        return screenX * cms.getCamZoom() + x;
    }

    private float getMapY(int screenY){
        float y = cms.getPos().y + GameProject.HEIGHT/2 * cms.getCamZoom();
        return y - screenY * cms.getCamZoom();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT){
            /*float x = cms.getPos().x - GameProject.WIDTH/2 * cms.getCamZoom();         //gets camera position and
            float y = cms.getPos().y + GameProject.HEIGHT/2* cms.getCamZoom();         //uses it to get top left corner of the screen

            float pixelX = screenX * cms.getCamZoom() + x;
            float pixelY = y - screenY * cms.getCamZoom();*/

            uiSystem.selectHex(map.getPixelHex(getMapX(screenX), getMapY(screenY)));
        }
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        if (uiSystem.getSelectedHex() != null){
            uiSystem.setPath(map.getPixelHex(getMapX(screenX), getMapY(screenY)));
        }
        return true;
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
