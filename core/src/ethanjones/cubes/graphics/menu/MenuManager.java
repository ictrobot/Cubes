package ethanjones.cubes.graphics.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import java.util.ArrayList;
import java.util.List;

import ethanjones.cubes.block.Block;
import ethanjones.cubes.block.BlockManager;
import ethanjones.cubes.core.util.BlockFace;
import ethanjones.cubes.graphics.world.BlockTextureHandler;

import static ethanjones.cubes.graphics.Graphics.spriteBatch;

public class MenuManager {

  private static Menu menu;
  private static Array<Menu> menus = new Array<Menu>();
  private static TextureRegion texture;
  private static boolean disableBackground = false;

  public static void setMenu(Menu menu) {
    int i = menus.indexOf(menu, true);
    if (i == -1) {
      menus.add(menu);
    } else if (i + 1 < menus.size) {
      for (int j = i + 1; j < menus.size; j++) {
        menus.get(j).save();
      }
      menus.removeRange(i + 1, menus.size - 1);
    }
    MenuManager.menu = menu;
  }

  public static Menu getPrevious(Menu menu) {
    int index = menus.indexOf(menu, true);
    if (index == -1 || index == 0) return null;
    return menus.get(index - 1);
  }

  public static void renderBackground() {
    if (disableBackground) return;
    if (texture == null) {
      List<Block> blocks = new ArrayList<Block>();
      blocks.addAll(BlockManager.getBlocks());
      while (texture == null && blocks.size() > 0) {
        int index = MathUtils.random(0, blocks.size() - 1);
        Block block = blocks.get(index);
        BlockTextureHandler textureHandler;
        try {
          textureHandler = block.getTextureHandler(null);
          if (textureHandler == null) throw new NullPointerException();
        } catch (Exception e) {
          blocks.remove(index);
          continue;
        }
        texture = textureHandler.getSide(BlockFace.posX);
      }
      if (texture == null) {
        disableBackground = true;
        return;
      }
    }
    spriteBatch.begin();
    int width = texture.getRegionWidth();
    int height = texture.getRegionHeight();
    int xTimes = (int) Math.floor((float) Gdx.graphics.getWidth() / width);
    int yTimes = (int) Math.floor((float) Gdx.graphics.getHeight() / height);
    for (int x = 0; x <= xTimes; x++) {
      for (int y = 0; y <= yTimes; y++) {
        spriteBatch.draw(texture, x * width, y * height, width, height);
      }
    }
    spriteBatch.end();
  }
}
