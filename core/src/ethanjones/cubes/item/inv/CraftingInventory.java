package ethanjones.cubes.item.inv;

import ethanjones.cubes.item.ItemStack;
import ethanjones.cubes.item.crafting.CraftingManager;

public class CraftingInventory extends Inventory {

  public final CraftingOutputInventory output;

  public CraftingInventory() {
    super("core:craft", 9);
    output = new CraftingOutputInventory();
  }

  @Override
  public void sync() {
    updateCrafting(false);
  }

  public void updateCrafting(boolean reduce) {
    if (reduce) {
      for (int i = 0; i < 9; i++) {
        if (itemStacks[i] != null) InventoryHelper.reduceCount(this, i);
      }
    }

    ItemStack stack = CraftingManager.match(this);
    if (stack != null) {
      output.itemStacks[0] = stack;
      output.stack = true;
    } else {
      output.itemStacks[0] = null;
      output.stack = false;
    }
  }

  private class CraftingOutputInventory extends Inventory {
    boolean stack = false;

    public CraftingOutputInventory() {
      super("core:craftOutput", 1);
      cancelInputItems = true;
      outputOnly = true;
    }

    public void sync() {
      boolean s = stack;
      ItemStack stack = itemStacks[0];
      if (s) {
        updateCrafting(stack == null);
      }
    }
  }

}
