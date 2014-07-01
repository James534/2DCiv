package CivPackage.Systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by james on 6/30/2014.
 */
public class CameraMovementSystem {

    public OrthographicCamera cam;
    private Vector2 camVel;
    private Vector2 camSpeed;
    private float camZoom;
    private boolean pressY;
    private boolean pressX;

    private float x0, y0, x1, y1;   //coordinates of (0,0) and (max,max) hexes, for the max viewpoint limit

    public CameraMovementSystem(OrthographicCamera cam, float x0, float y0, float x1, float y1){
        this.cam = cam;
        cam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(0, 0, 0);
        camZoom = 1;
        cam.zoom = camZoom;
        camVel = new Vector2();
        camSpeed = new Vector2();

        this.x0 = x0;
        this.x1 = x1;
        this.y0 = y0;
        this.y1 = y1;
    }

    public void update(){
        updateCamera();
    }

    public void moveCam(int x, int y){
        if (x != 0) {
            camVel.x = x * camZoom;
            camSpeed.x = x;
        }
        if (y != 0){
            camVel.y = y * camZoom;
            camSpeed.y = y;
        }
    }

    public void zoomCam(int amount){
        camZoom += amount * 0.2f;
        camZoom *= 5;
        camZoom = Math.round(camZoom)/5f;
        if (camZoom < 0.4)
            camZoom = 0.4f;
        else if (camZoom > 2){
            camZoom = 2;
        }
        camVel.x = camSpeed.x * camZoom;
        camVel.y = camSpeed.y * camZoom;
        cam.zoom = camZoom;
    }

    public void setPressY(boolean press){
        pressY = press;
    }
    public void setPressX(boolean press){
        pressX = press;
    }

    public Vector3 getPos(){
        return cam.position;
    }
    public float getCamZoom(){return camZoom;}

    private void updateCamera(){
        cam.position.add(camVel.x, camVel.y, 0);
        if (pressX == false) {
            if (camVel.x > 0) {
                camVel.x -= 1;
                if (camVel.x < 0){   //resets the speed if it goes below 0
                    camVel.x = 0;
                }
            } else if (camVel.x < 0) {
                camVel.x += 1;
                if (camVel.x > 0){
                    camVel.x = 0;
                }
            }
            if (camVel.x == 0){
                camSpeed.x = 0;
            }
        }
        if (pressY == false) {
            if (camVel.y > 0) {
                camVel.y -= 1;
                if (camVel.y < 0){   //resets the speed if it goes below 0
                    camVel.y = 0;
                }
            } else if (camVel.y < 0) {
                camVel.y += 1;
                if (camVel.y > 0){
                    camVel.y = 0;
                }
            }
            if (camVel.y == 0) {
                camSpeed.y = 0;
            }
        }

        if (cam.position.x > x1 + 150){
            cam.position.x = x1 + 150;
        }else if (cam.position.x < x0 - 150){
            cam.position.x = x0 - 150;
        }

        if (cam.position.y > y1 + 150){
            //cam.position.y = y1 + 150;
        }else if (cam.position.y < y0 - 150){
            //cam.position.y = y0 - 150;
        }

        cam.update();
    }
}