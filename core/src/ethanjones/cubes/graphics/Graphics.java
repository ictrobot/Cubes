package ethanjones.cubes.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import ethanjones.cubes.graphics.menu.Fonts;

public class Graphics {

  public static SpriteBatch spriteBatch;
  public static ModelBatch modelBatch;
  public static ScreenViewport screenViewport;

  public static void init() {
    spriteBatch = new SpriteBatch();
    modelBatch = new ModelBatch();
    screenViewport = new ScreenViewport();
  }

  public static void resize() {
    screenViewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    Fonts.resize();
  }
}
