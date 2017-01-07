package ethanjones.cubes.graphics;

import ethanjones.cubes.block.Block;
import ethanjones.cubes.core.id.IDManager;
import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.platform.Compatibility;
import ethanjones.cubes.core.system.CubesException;
import ethanjones.cubes.graphics.hud.inv.BlockIcons;
import ethanjones.cubes.graphics.world.CubesModelBatch;
import ethanjones.cubes.graphics.world.WorldGraphicsPools;
import ethanjones.cubes.item.Item;
import ethanjones.cubes.item.ItemBlock;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.io.IOException;

public class Graphics {

  public static SpriteBatch spriteBatch;
  public static ModelBatch modelBatch;
  public static ScreenViewport screenViewport;

  public static float GUI_WIDTH = 0f;
  public static float GUI_HEIGHT = 0f;

  public static void init() {
    spriteBatch = new SpriteBatch();
    modelBatch = new CubesModelBatch();
    screenViewport = new ScreenViewport();

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
  }

  public static void resize() {
    int width = Gdx.graphics.getWidth();
    int height = Gdx.graphics.getHeight();
    float scaleFactor = scaleFactor();
    GUI_WIDTH = width / scaleFactor;
    GUI_HEIGHT = height / scaleFactor;

    screenViewport.setUnitsPerPixel(1 / scaleFactor);
    screenViewport.update(width, height, true);
  }

  public static void takeScreenshot() {
    Pixmap pixmap = ScreenUtils.getFrameBufferPixmap(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight());
    FileHandle dir = Compatibility.get().getBaseFolder().child("screenshots");
    dir.mkdirs();
    FileHandle f = dir.child(System.currentTimeMillis() + ".png");
    try {
      PixmapIO.PNG writer = new PixmapIO.PNG((int) (pixmap.getWidth() * pixmap.getHeight() * 1.5f));
      try {
        writer.setFlipY(true);
        writer.write(f, pixmap);
      } finally {
        writer.dispose();
      }
    } catch (IOException ex) {
      throw new CubesException("Error writing PNG: " + f, ex);
    } finally {
      pixmap.dispose();
    }
    Log.info("Took screenshot '" + f.file().getAbsolutePath() + "'");
  }

  public static float scaleFactor() {
    float f = Gdx.graphics.getPpiX() / 96;
    if (f > 1) f -= (f - 1) * 0.4f;
    return f;
  }
}
