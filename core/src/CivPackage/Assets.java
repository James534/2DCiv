package CivPackage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * Created by james on 6/29/2014.
 */
public class Assets {
    public static AssetManager manager = new AssetManager();
    public static Skin skin;

    public static final Pixmap[] num = {new Pixmap(Gdx.files.internal(GameProject.fileName + "Text/0.png")),
            new Pixmap(Gdx.files.internal(GameProject.fileName + "Text/1.png")),
            new Pixmap(Gdx.files.internal(GameProject.fileName + "Text/2.png")),
            new Pixmap(Gdx.files.internal(GameProject.fileName + "Text/3.png")),
            new Pixmap(Gdx.files.internal(GameProject.fileName + "Text/4.png")),
            new Pixmap(Gdx.files.internal(GameProject.fileName + "Text/5.png")),
            new Pixmap(Gdx.files.internal(GameProject.fileName + "Text/6.png")),
            new Pixmap(Gdx.files.internal(GameProject.fileName + "Text/7.png")),
            new Pixmap(Gdx.files.internal(GameProject.fileName + "Text/8.png")),
            new Pixmap(Gdx.files.internal(GameProject.fileName + "Text/9.png"))};


    public static void queueLoading() {
    }

    public static Texture drawText(String text, Texture img) {
        int x = img.getWidth()/2 - (text.length()*num[0].getWidth())/2 ;
        int y = img.getHeight()/2 - num[0].getHeight();
        for (int i = 0; i < text.length(); i++) {
            img.draw(num[Character.getNumericValue(text.charAt(i))], x + num[0].getWidth()*i, y);
        }
        return img;
    }
}
