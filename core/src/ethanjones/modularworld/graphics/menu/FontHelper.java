package ethanjones.modularworld.graphics.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import ethanjones.modularworld.graphics.GraphicsHelper;

public class FontHelper {

  protected static class StaticBitmapFont extends BitmapFont {

    private final int scale;

    private StaticBitmapFont(int scale) {
      super(GraphicsHelper.assetManager.assets.folders.get("font").files.get("font.fnt").fileHandle);
      this.scale = scale;
    }

    @Override
    public void setScale(float scaleX, float scaleY) {

    }

    public void updateScale() {
      super.setScale(1, 1);
      float y = Gdx.graphics.getHeight() / 2 / scale;
      float x = Gdx.graphics.getWidth() / 2 / scale;
      TextBounds bounds = getBounds("ABCDEF");
      float v = Math.min(x / bounds.width, y / bounds.height);
      super.setScale(v, v);
    }
  }

  public static final StaticBitmapFont Scale1 = new StaticBitmapFont(1);
  public static final StaticBitmapFont Scale2 = new StaticBitmapFont(2);
  public static final StaticBitmapFont Scale3 = new StaticBitmapFont(3);
  public static final StaticBitmapFont Scale4 = new StaticBitmapFont(4);
  public static final StaticBitmapFont Scale5 = new StaticBitmapFont(5);

  protected static void update() {
    Scale1.updateScale();
    Scale2.updateScale();
    Scale3.updateScale();
    Scale4.updateScale();
    Scale5.updateScale();
  }
}
