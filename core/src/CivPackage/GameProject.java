package CivPackage;

import CivPackage.Screens.SplashScreen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GameProject extends com.badlogic.gdx.Game {

    public static int WIDTH = 1080;
    public static int HEIGHT = 720;

	@Override
	public void create () {
        setScreen (new SplashScreen());
	}
}
