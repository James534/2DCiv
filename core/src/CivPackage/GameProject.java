package CivPackage;

import CivPackage.Screens.SplashScreen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GameProject extends com.badlogic.gdx.Game {

    public static final int WIDTH = 1080;
    public static final int HEIGHT = 720;
    public static final String fileName = "assets/";

	@Override
	public void create () {
        setScreen (new SplashScreen());
	}
}
