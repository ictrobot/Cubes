package ethanjones.cubes.graphics.menu;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.platform.Compatibility;
import ethanjones.cubes.core.system.CubesException;
import ethanjones.cubes.core.system.Debug;
import ethanjones.cubes.graphics.assets.Assets;
import ethanjones.cubes.graphics.menu.BitmapFontWriter.FontInfo;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.PixmapPacker.Page;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeBitmapFontData;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.PixmapTextureData;
import com.badlogic.gdx.utils.Array;

public class Fonts {

  public static BitmapFont title;
  public static BitmapFont menu;
  public static BitmapFont hud;
  public static BitmapFont smallHUD;
  public static BitmapFont debug;

  private static FreeTypeFontGenerator generator;
  private static FileHandle fontFolder = Compatibility.get().getBaseFolder().child("fontcache");

  static {
    Log.debug("Generating font");
    long time = System.currentTimeMillis();
    fontFolder.mkdirs();
    Compatibility.get().nomedia(fontFolder);
    
    FreeTypeFontGenerator.setMaxTextureSize(2048);

    title = getFont(64);
    menu = getFont(40);
    hud = getFont(32);
    smallHUD = getFont(20);
    debug = getFont(32);

    // debug fixed width numbers
    debug.setFixedWidthGlyphs("0123456789");
  
    if (generator != null) {
      generator.dispose();
      Log.debug("Font generated [" + (System.currentTimeMillis() - time) + "ms]");
    } else {
      Log.debug("Font read [" + (System.currentTimeMillis() - time) + "ms]");
    }
  }

  public static BitmapFont getFont(int size) {
    BitmapFont f = readFont(size);
    if (f == null) f = writeFont(size);
    if (f == null) Debug.crash(new CubesException("Null font"));
    return f;
  }
  
  private static BitmapFont readFont(int size) {
    BitmapFont b = null;
    try {
      FileHandle fnt = fontFolder.child(size + ".fnt");
      if (!fnt.exists()) return null;
      b = new BitmapFont(fnt);
    } catch (Exception e) {
      Log.warning("Failed to read font cache (size " + size + ")", e);
    }
    return b;
  }
  
  private static BitmapFont writeFont(int size) {
    if (generator == null) generator = new FreeTypeFontGenerator(Assets.getAsset("core:font/font.ttf").getFileHandle());
    
    try {
      FreeTypeFontParameter parameter = new FreeTypeFontParameter();
      parameter.size = size;
      parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS;
      parameter.flip = false;
      parameter.packer = new PixmapPacker(512, 256, Pixmap.Format.RGBA8888, 2, false);
      FreeTypeBitmapFontData fontData = generator.generateData(parameter);
  
      Array<Page> pages = parameter.packer.getPages();
      Array<TextureRegion> textureRegions = new Array<TextureRegion>();
      Pixmap[] pixmaps = new Pixmap[pages.size];
  
      for (int i = 0; i < pages.size; i++) {
        Page page = pages.get(i);
        pixmaps[i] = page.getPixmap();
    
        Texture texture = new Texture(new PixmapTextureData(page.getPixmap(), page.getPixmap().getFormat(), false, false, true));
        texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        textureRegions.add(new TextureRegion(texture));
      }
  
      BitmapFont font = new BitmapFont(fontData, textureRegions, false);
      FileHandle fnt = fontFolder.child(size + ".fnt");
      BitmapFontWriter.writeFont(fontData, pixmaps, fnt, new FontInfo("", size));
      
      parameter.packer.dispose();
      return font;
    } catch (Exception e) {
      Log.warning("Failed to write font cache (size " + size + ")", e);
      return null;
    }
  }
}
