package ethanjones.cubes.graphics.menu;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.graphics.assets.Assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

public class Fonts {

  public static final BitmapFont title;
  public static final BitmapFont menu;
  public static final BitmapFont hud;
  public static final BitmapFont smallHUD;
  public static final BitmapFont debug;

  private static final FreeTypeFontGenerator generator;
  public static final float scaleFactor;
  private static final int base;

  static {
    Log.debug("Generating font");
    FreeTypeFontGenerator.setMaxTextureSize(2048);
    generator = new FreeTypeFontGenerator(Assets.getAsset("core:font/font.ttf").getFileHandle());
    float f = Gdx.graphics.getPpiX() / 96;
    if (f > 1) f -= (f - 1) * 0.4f;
    base = (int) f;
    scaleFactor = f;

    title = getFont(64);
    menu = getFont(42);
    hud = getFont(32);
    smallHUD = getFont(20);
    debug = getFont(32);

    // debug fixed width numbers
    debug.setFixedWidthGlyphs("0123456789");

    Log.debug("Font generated");
  }

  public static BitmapFont getFont(int size) {
    FreeTypeFontParameter parameter = new FreeTypeFontParameter();
    parameter.size = base * size;
    parameter.incremental = true;
    parameter.shadowOffsetX = -base;
    parameter.shadowOffsetY = -base;
    return generator.generateFont(parameter);
  }
}
