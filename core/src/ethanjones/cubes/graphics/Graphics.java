package ethanjones.cubes.graphics;

import ethanjones.cubes.block.Block;
import ethanjones.cubes.core.IDManager;
import ethanjones.cubes.graphics.world.WorldShaderProvider;
import ethanjones.cubes.item.Item;
import ethanjones.cubes.item.ItemBlock;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

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
}
