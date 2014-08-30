package ethanjones.modularworld.graphics.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import ethanjones.modularworld.graphics.GraphicsHelper;

public class Fonts {

  public static final StaticBitmapFont Size1 = new StaticBitmapFont(1);
  public static final StaticBitmapFont Size2 = new StaticBitmapFont(2);
  public static final StaticBitmapFont Size3 = new StaticBitmapFont(3);
  public static final StaticBitmapFont Size4 = new StaticBitmapFont(4);
  public static final StaticBitmapFont Size5 = new StaticBitmapFont(5);
  public static final StaticBitmapFont Size6 = new StaticBitmapFont(6);
  public static final StaticBitmapFont Size7 = new StaticBitmapFont(7);
  public static final StaticBitmapFont Size8 = new StaticBitmapFont(8);

  public static void resize() {
    Size1.updateScale();
    Size2.updateScale();
    Size3.updateScale();
    Size4.updateScale();
    Size5.updateScale();
    Size6.updateScale();
    Size7.updateScale();
    Size8.updateScale();
  }

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
      float x = Gdx.graphics.getWidth() * scale / 12.5f;
      float y = Gdx.graphics.getHeight() * scale / 12.5f;
      TextBounds bounds = getBounds("ABCDEF");
      float v = Math.min(x / bounds.width, y / bounds.height);
      super.setScale(v, v);
    }
  }
}
