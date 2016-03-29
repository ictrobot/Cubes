package ethanjones.cubes.item.inv;

import ethanjones.cubes.item.Item;
import ethanjones.cubes.item.ItemStack;

public class InventoryHelper {

  public static ItemStack reduceCount(Inventory inventory, int stack) {
    if (inventory.itemStacks[stack] == null) return null;
    Item item = inventory.itemStacks[stack].item;
    if (inventory.itemStacks[stack].count > 1) {
      inventory.itemStacks[stack].count--;
    } else {
      inventory.itemStacks[stack] = null;
    }
    inventory.sync();
    return new ItemStack(item, 1);
  }

  public static void addItemstack(Inventory inventory, ItemStack itemstack) {
    for (int i = 0; i < inventory.itemStacks.length; i++) {
      if (inventory.itemStacks[i] == null) {
        inventory.itemStacks[i] = itemstack;
        break;
      } else if (inventory.itemStacks[i].item == itemstack.item) {
        if (inventory.itemStacks[i].count + itemstack.count <= itemstack.item.getStackCountMax()) {
          inventory.itemStacks[i].count += itemstack.count;
          break;
        }
      }
    }
    inventory.sync();
  }
}
