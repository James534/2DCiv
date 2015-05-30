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
    private boolean pressY;         //if the x key is pressed
    private boolean pressX;
    private boolean moveTo;         //whether the camera is moving to a spot
    private Vector2 friction;

    private float x0, y0, x1, y1;   //coordinates of (0,0) and (max,max) hexes, for the max viewpoint limit

    public CameraMovementSystem(OrthographicCamera cam, float x0, float y0, float x1, float y1){
        this.cam = cam;
        cam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(0, 0, 0);
        camZoom = 1;
        cam.zoom = camZoom;
        camVel = new Vector2();
        camSpeed = new Vector2();
        friction = new Vector2();

        this.x0 = x0;
        this.x1 = x1;
        this.y0 = y0;
        this.y1 = y1;
    }

    public void update(){
        updateCamera();
    }

    public void moveCamTo (float x, float y){
        cam.position.set(x,y,0);
        /*float dX = x - cam.position.x;
        float dY = y - cam.position.y;

        moveTo = true;
        camVel.x = dX/5f;
        camVel.y = dY/5f;
        camSpeed.x = Math.abs(camVel.x);
        camSpeed.y = Math.abs(camVel.y);
        friction.x = dX / 20f;
        friction.y = dY / 20f;*/
    }

    public void moveCam(int x, int y){
        if (x != 0) {
            camVel.x = x * camZoom;
            camSpeed.x = x;
            moveTo = false;
        }
        if (y != 0){
            camVel.y = y * camZoom;
            camSpeed.y = y;
            moveTo = false;
        }
    }
    //File names of the hex tiles
    private static String[] hexNames = {"Hex0",
            "Ocean",    "OceanAtoll",       "OceanIce",
            "Shore",    "ShoreAtoll",       "ShoreIce",
            "Desert",   "DesertHills",      "DesertMountain",   "DesertFallout",    "DesertOasis",      "DesertFloodplains",
            "Grassland","GrasslandHills",   "GrasslandMountain","GrasslandFallout", "GrasslandForest",  "GrasslandHillForest","GrasslandHillJungle","GrasslandJungle","GrasslandMarsh",
            "Plains",   "PlainsHills",      "PlainsMountain",   "PlainsFallout",    "PlainsForest",     "PlainsHillForest",
            "Snow",     "SnowHills",        "SnowMountain",
            "Tundra",   "TundraHills",      "TundraMountain",   "TundraFallout",    "TundraForest",     "TundraHillForest"
    };
    public void zoomCam(int amount){
        camZoom += amount * 1f;
        camZoom *= 5;
        camZoom = Math.round(camZoom)/5f;
        if (camZoom < 0.4)
            camZoom = 0.4f;
        else if (camZoom > 18){
            camZoom = 18;
        }
        //if the zoom is above 8, stop moving the camera after change in zoom, since it takes forever to stop
        if (camZoom > 2){
            camVel.x = 0;
            camVel.y = 0;
        }else {
            //below 8 seems fine
            camVel.x = camSpeed.x * camZoom;
            camVel.y = camSpeed.y * camZoom;
        }
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

        //if the camera is moving to a hex, slowly slow it down
        if (moveTo){
            if (camSpeed.x > 0){
                camSpeed.x -= (friction.x * camZoom * camZoom);
                if (camSpeed.x <= 0){
                    moveTo = false;
                    camSpeed.x = 0;
                }
                camVel.x = Math.signum(camVel.x) * camSpeed.x;
            }
            if (camSpeed.y > 0){
                camSpeed.y -= friction.y;
                if (camSpeed.y <= 0){
                    moveTo = false;
                    camSpeed.y = 0;
                }
                camVel.y = Math.signum(camVel.y) * camSpeed.y;
            }
        }else
        //slows down the camera if theres no buttons being pressed
        if (!pressX) {
            if (camVel.x > 0) {
                camVel.x -= 1 * camZoom/2;
                if (camVel.x < 0){   //resets the speed if it goes below 0
                    camVel.x = 0;
                }
            } else if (camVel.x < 0) {
                camVel.x += 1 * camZoom/2;
                if (camVel.x > 0){
                    camVel.x = 0;
                }
            }
            if (camVel.x == 0){
                camSpeed.x = 0;
            }
        }
        if (!pressY) {
            if (camVel.y > 0) {
                camVel.y -= 1 * camZoom/2;
                if (camVel.y < 0){   //resets the speed if it goes below 0
                    camVel.y = 0;
                }
            } else if (camVel.y < 0) {
                camVel.y += 1 * camZoom/2;
                if (camVel.y > 0){
                    camVel.y = 0;
                }
            }
            if (camVel.y == 0) {
                camSpeed.y = 0;
            }
        }


        //makes sure cam position dosnt go out of bounds
        if (cam.position.x > x1 + 150){
            cam.position.x = x1 + 150;
        }else if (cam.position.x < x0 - 150){
            cam.position.x = x0 - 150;
        }

        if (cam.position.y > y1 + 150){
            cam.position.y = y1 + 150;
        }else if (cam.position.y < y0 - 150){
            cam.position.y = y0 - 150;
        }

        cam.update();
    }
}
