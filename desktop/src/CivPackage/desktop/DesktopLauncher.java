package CivPackage.desktop;

import CivPackage.GameProject;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = GameProject.WIDTH;
        config.height = GameProject.HEIGHT;
		new LwjglApplication(new GameProject(), config);
	}
}
