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
            //screen.select (pixelX, pixelY);
            float SectX = pixelX / 52;
            float SectY  = pixelY/45;

            float sectPxlX = pixelX % 52;
            float sectPixY = pixelY % 45;

            float m = 15/26f;

            float selX = SectX ;
            float selY = SectY ;

            //http://www.gamedev.net/page/resources/_/technical/game-programming/coordinates-in-hexagon-based-tile-maps-r1800
            if ((int)SectY  % 2 == 0){ //A TYPE
                //left
                if (sectPixY < (15 - sectPxlX*m)){
                    selX = SectX  -1;
                    selY = SectY  -1;
                }
                //right
                if (sectPixY < (-15 + sectPxlX* m)){
                    selY = SectY  -1;
                }
            }else{              //B TYPE
                //right side
                if (sectPxlX >= 26){
                    if (sectPixY < (2*15 - sectPxlX*m)){
                        selY = SectY  -1;
                    }
                }
                //left side
                if (sectPxlX < (26)){
                    if (sectPixY < (sectPxlX*m)){
                        selY = SectY  -1;
                    }else{
                        selX = SectX  -1;
                    }
                }
            }

            //System.out.println (sectPxlX + " " + (int)SectX  + " " + (int)selX);
            //System.out.println (sectPixY + " " + SectY + " " + selY);
            System.out.println ((int)selX + " " + (int)selY);
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
