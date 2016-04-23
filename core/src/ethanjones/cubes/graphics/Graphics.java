package ethanjones.cubes.graphics;

import ethanjones.cubes.block.Block;
import ethanjones.cubes.core.IDManager;
import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.platform.Compatibility;
import ethanjones.cubes.core.system.CubesException;
import ethanjones.cubes.graphics.world.WorldShaderProvider;
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

  public static void init() {
    spriteBatch = new SpriteBatch();
    modelBatch = new ModelBatch(new WorldShaderProvider());
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
  }

  public static void resize() {
    screenViewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
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
}
