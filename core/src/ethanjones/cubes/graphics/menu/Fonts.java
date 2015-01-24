package ethanjones.cubes.graphics.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.BitmapFontData;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeBitmapFontData;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.graphics.assets.Assets;

public class Fonts {

  protected static class CopyBitmapFontData extends BitmapFontData {

    public CopyBitmapFontData(BitmapFontData base) {
      super();
      this.imagePath = base.imagePath;
      this.imagePaths = base.imagePaths;
      this.fontFile = base.fontFile;
      this.flipped = base.flipped;
      this.lineHeight = base.lineHeight;
      this.capHeight = base.capHeight;
      this.ascent = base.ascent;
      this.descent = base.descent;
      this.down = base.down;
      this.scaleX = base.scaleX;
      this.scaleY = base.scaleY;
      System.arraycopy(base.glyphs, 0, this.glyphs, 0, base.glyphs.length);
      this.spaceWidth = base.spaceWidth;
      this.xHeight = base.xHeight;
    }

  }

  protected static class StaticBitmapFont extends BitmapFont {

    private final int scale;

    private StaticBitmapFont(int scale) {
      super(new CopyBitmapFontData(data), data.getTextureRegions(), false);
      this.scale = scale;
      updateScale();
    }

    public void updateScale() {
      super.setScale(1, 1);
      float x = Gdx.graphics.getWidth() * (scale + 1) / 20f;
      float y = Gdx.graphics.getHeight() * (scale + 1) / 20f;
      TextBounds bounds = getBounds("ABCDEF");
      float v = Math.min(x / bounds.width, y / bounds.height);
      super.setScale(v, v);
    }

    @Override
    public void setScale(float scaleX, float scaleY) {

    }
  }
  public static final StaticBitmapFont Size1 = new StaticBitmapFont(1);
  public static final StaticBitmapFont Size2 = new StaticBitmapFont(2);
  public static final StaticBitmapFont Size3 = new StaticBitmapFont(3);
  public static final StaticBitmapFont Size4 = new StaticBitmapFont(4);
  public static final StaticBitmapFont Size5 = new StaticBitmapFont(5);
  public static final StaticBitmapFont Size6 = new StaticBitmapFont(6);
  public static final StaticBitmapFont Size7 = new StaticBitmapFont(7);
  public static final StaticBitmapFont Size8 = new StaticBitmapFont(8);
  private static FreeTypeBitmapFontData data;
  static {
    Log.debug("Generating font");
    FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Assets.getAsset("core:font/font.ttf").getFileHandle());
    FreeTypeFontParameter parameter = new FreeTypeFontParameter();
    parameter.size = 10;
    data = generator.generateData(parameter);
    generator.dispose();
    Log.debug("Font generated");
  }

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
}
