package ethanjones.cubes.graphics;

import ethanjones.cubes.block.Block;
import ethanjones.cubes.core.id.IDManager;
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

  public static SpriteBatch spriteBatch = new SpriteBatch(); // needed for splash screen
  public static ScreenViewport screenViewport = new ScreenViewport();
  public static ModelBatch modelBatch;
  public static GLProfiler glProfiler;

  public static float GUI_WIDTH = 0f;
  public static float GUI_HEIGHT = 0f;

  public static int RENDER_WIDTH = Gdx.graphics.getWidth();
  public static int RENDER_HEIGHT = Gdx.graphics.getHeight();

  private static boolean init = false;

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

  public static void resize(int width, int height) {
    RENDER_WIDTH = width;
    RENDER_HEIGHT = height;

    float scaleFactor = scaleFactor();
    GUI_WIDTH = RENDER_WIDTH / scaleFactor;
    GUI_HEIGHT = RENDER_HEIGHT / scaleFactor;

    screenViewport.setUnitsPerPixel(1 / scaleFactor);
    screenViewport.update(RENDER_WIDTH, RENDER_HEIGHT, true);
  }

  public static float scaleFactor() {
    float f = Gdx.graphics.getPpiX() / 96;
    if (f > 1) f -= (f - 1) * 0.4f;
    f *= Math.min(RENDER_WIDTH, RENDER_HEIGHT) / Math.min(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    return f * (Settings.isSetup() ? Settings.getFloatSettingValue(Settings.GRAPHICS_SCALE) : 1f);
  }
}
