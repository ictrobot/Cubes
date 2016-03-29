package ethanjones.cubes.item.inv;

import ethanjones.cubes.item.ItemStack;

public class InventoryHelper {
  public static void reduceCount(Inventory inventory, int stack) {
    if (inventory.itemStacks[stack].count > 1) {
      inventory.itemStacks[stack].count--;
    } else {
      inventory.itemStacks[stack] = null;
    }
    inventory.sync();
  }

  public static void addItemstack(Inventory inventory, ItemStack itemstack) {
    for (int i = 0; i < inventory.itemStacks.length; i++) {
      if (inventory.itemStacks[i] == null) {
        inventory.itemStacks[i] = itemstack;
        break;
      } else if (inventory.itemStacks[i].item == itemstack.item) {
        if (inventory.itemStacks[i].count + itemstack.count < itemstack.item.getStackCountMax()) {
          inventory.itemStacks[i].count += itemstack.count;
          break;
        }
      }
    }
    inventory.sync();
  }
}
