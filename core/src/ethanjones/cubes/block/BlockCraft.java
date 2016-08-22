package ethanjones.cubes.block;

import ethanjones.cubes.entity.living.player.Player;
import ethanjones.cubes.graphics.hud.inv.CraftingInventoryActor;
import ethanjones.cubes.graphics.hud.inv.DoubleInventory;
import ethanjones.cubes.graphics.hud.inv.InventoryManager;
import ethanjones.cubes.graphics.hud.inv.InventoryWindow;
import ethanjones.cubes.side.Side;
import ethanjones.cubes.side.Sided;
import ethanjones.cubes.side.common.Cubes;

import com.badlogic.gdx.Input.Buttons;

public class BlockCraft extends Block {

  public BlockCraft() {
    super("core:craft");
  }

  @Override
  public boolean onButtonPress(int button, Player player, int blockX, int blockY, int blockZ) {
    if (Sided.getSide() == Side.Client && button == Buttons.RIGHT)
      InventoryManager.showInventory(new InventoryWindow(new DoubleInventory(new CraftingInventoryActor(), Cubes.getClient().renderer.guiRenderer.playerInv)));
    return true;
  }
}
