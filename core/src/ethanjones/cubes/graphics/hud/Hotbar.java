package ethanjones.cubes.graphics.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import ethanjones.cubes.block.Block;
import ethanjones.cubes.core.util.BlockFace;
import ethanjones.cubes.entity.living.player.Player;
import ethanjones.cubes.graphics.assets.Assets;
import ethanjones.cubes.graphics.gui.element.GuiElement;
import ethanjones.cubes.side.common.Cubes;

public class Hotbar extends GuiElement {

  private final Texture hotbarSelected;
  private final Texture hotbarSlot;

  public Hotbar() {
    hotbarSelected = Assets.getTexture("core:hud/HotbarSelected.png");
    hotbarSlot = Assets.getTexture("core:hud/HotbarSlot.png");
  }
  
  @Override
  public void render(Batch batch) {
    Player player = Cubes.getClient().player;
    int startWidth = (Gdx.graphics.getWidth() / 2) - (hotbarSlot.getWidth() * 5);
    for (int i = 0; i < 10; i++) {
      int minX = startWidth + (i * hotbarSlot.getWidth());
      if (i == player.getSelectedSlot()) {
        batch.draw(hotbarSelected, minX, 0);
      } else {
        batch.draw(hotbarSlot, minX, 0);
      }
      Block block = player.getHotbar(i);
      if (block != null) {
        TextureRegion side = block.getTextureHandler(null).getSide(BlockFace.posX);
        batch.draw(side, minX + 8, 8);
      }
    }
  }
}
