package ethanjones.cubes.graphics.menu;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.system.CubesException;
import ethanjones.cubes.core.system.Debug;
import ethanjones.cubes.graphics.assets.Assets;

public class Fonts {

  public static BitmapFont title;
  public static BitmapFont menu;
  public static BitmapFont hud;
  public static BitmapFont smallHUD;
  public static BitmapFont debug;

  static {
    Log.debug("Reading font");
    long time = System.currentTimeMillis();

    title = getFont(64);
    menu = getFont(40);
    hud = getFont(32);
    smallHUD = getFont(20);
    debug = getFont(32);

    // debug fixed width numbers
    debug.setFixedWidthGlyphs("0123456789");
    
    Log.debug("Font read [" + (System.currentTimeMillis() - time) + "ms]");
  }

  public static BitmapFont getFont(int size) {
    BitmapFont f = readFont(size);
    if (f == null) Debug.crash(new CubesException("Null font"));
    return f;
  }
  
  private static BitmapFont readFont(int size) {
    BitmapFont b = null;
    try {
      FileHandle fnt = Assets.getAsset("core:fonts/" +size + ".fnt").getFileHandle();
      if (!fnt.exists()) return null;
      b = new BitmapFont(fnt);
    } catch (Exception e) {
      Log.warning("Failed to read font (size " + size + ")", e);
    }
    return b;
  }
}
