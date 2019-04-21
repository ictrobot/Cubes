package ethanjones.cubes.graphics;

import ethanjones.cubes.block.Block;
import ethanjones.cubes.core.id.IDManager;
import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.settings.Settings;
import ethanjones.cubes.graphics.hud.inv.BlockIcons;
import ethanjones.cubes.graphics.world.WorldGraphicsPools;
import ethanjones.cubes.item.Item;
import ethanjones.cubes.item.ItemBlock;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class Graphics {

  public static SpriteBatch spriteBatch = new SpriteBatch();
  public static ModelBatch modelBatch;
  public static ScreenViewport screenViewport = new ScreenViewport();
  public static GLProfiler glProfiler;

  public static float GUI_WIDTH = 0f;
  public static float GUI_HEIGHT = 0f;

  private static boolean init = false;
  private static float oldScaleFactor = 1f;

  public static void init() {
    if (init) return;
    init = true;

    modelBatch = new CubesModelBatch();

    for (Block block : IDManager.getBlocks()) {
      block.loadGraphics();
    }
    for (ItemBlock itemBlock : IDManager.getItemBlocks()) {
      itemBlock.loadGraphics();
    }
    for (Item item : IDManager.getItems()) {
      item.loadGraphics();
    }
    WorldGraphicsPools.init();
    BlockIcons.renderIcons();

    glProfiler = new GLProfiler(Gdx.graphics);
  }

  public static void resize() {
    int width = Gdx.graphics.getWidth();
    int height = Gdx.graphics.getHeight();
    float scaleFactor = scaleFactor();
    GUI_WIDTH = width / scaleFactor;
    GUI_HEIGHT = height / scaleFactor;

    screenViewport.setUnitsPerPixel(1 / scaleFactor);
    screenViewport.update(width, height, true);

    if (scaleFactor != oldScaleFactor) {
      Log.debug("Scale factor changing to " + scaleFactor);
      oldScaleFactor = scaleFactor;
    }
  }

  public static float scaleFactor() {
    float f = Gdx.graphics.getPpiX() / 96;
    if (f > 1) f -= (f - 1) * 0.4f;

    // add settings value
    if (Settings.isSetup()) f += Settings.getFloatSettingValue(Settings.GRAPHICS_SCALE);

    // round to nearest 0.25f
    if (f < 0.25f) f = 0.25f;
    return ((float) Math.round(f * 4f)) / 4f;
  }
}
