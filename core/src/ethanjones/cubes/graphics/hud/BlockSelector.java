package ethanjones.cubes.graphics.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.List;

import ethanjones.cubes.block.Block;
import ethanjones.cubes.block.BlockManager;
import ethanjones.cubes.core.util.BlockFace;
import ethanjones.cubes.graphics.assets.Assets;
import ethanjones.cubes.graphics.gui.element.GuiElement;
import ethanjones.cubes.graphics.gui.element.event.TypeGuiEventListener;
import ethanjones.cubes.graphics.gui.element.event.mouse.MouseDownEvent;
import ethanjones.cubes.side.common.Cubes;

public class BlockSelector extends GuiElement {

  private final Texture hotbarSlot;
  private final Block[][] blocks;

  public BlockSelector() {
    hotbarSlot = Assets.getTexture("core:hud/HotbarSlot.png");

    blocks = new Block[10][6];
    int i = 0;
    List<Block> list = BlockManager.getBlocks();
    for (int y = 0; y < 6; y++) {
      for (int x = 0; x < 10; x++, i++) {
        if (i >= list.size()) break;
        blocks[x][y] = list.get(i);
      }
      if (i >= list.size()) break;
    }

    addEventListener(new TypeGuiEventListener<MouseDownEvent>(MouseDownEvent.class) {

      @Override
      public boolean onTypeEvent(MouseDownEvent event) {
        int startWidth = (Gdx.graphics.getWidth() / 2) - (hotbarSlot.getWidth() * 5);
        int startHeight = (Gdx.graphics.getHeight() / 2) - (hotbarSlot.getHeight() * 3);
        int x = event.x - startWidth;
        int y = event.y - startHeight;
        if (x >= 0 || y >= 0) {
          int remX = x % hotbarSlot.getWidth();
          int remY = y % hotbarSlot.getHeight();
          if (remX >= 8 && remX <= 40 && remY >= 8 && remY <= 40) {
            int slotX = x / hotbarSlot.getWidth();
            int slotY = y / hotbarSlot.getHeight();
            if (slotX >= blocks.length || slotY >= blocks[0].length) {
              Cubes.getClient().player.setHotbar(blocks[slotX][slotY]);
              return true;
            }
          }
        }
        return false;
      }
    });
  }
  
  @Override
  public void render(Batch batch) {
    int i = 0;
    int startWidth = (Gdx.graphics.getWidth() / 2) - (hotbarSlot.getWidth() * 5);
    int startHeight = (Gdx.graphics.getHeight() / 2) - (hotbarSlot.getHeight() * 3);
    for (int y = 0; y < 6; y++) {
      int minY = startHeight + ((5 - y) * hotbarSlot.getHeight());
      for (int x = 0; x < 10; x++, i++) {
        int minX = startWidth + (x * hotbarSlot.getWidth());
        batch.draw(hotbarSlot, minX, minY);
        Block block = blocks[x][y];
        if (block == null) continue;
        TextureRegion side = block.getTextureHandler(null).getSide(BlockFace.posX);
        batch.draw(side, minX + 8, minY + 8);
      }
    }
  }
}
