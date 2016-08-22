package ethanjones.cubes.item.crafting;

import ethanjones.cubes.item.ItemStack;
import ethanjones.cubes.item.inv.InventoryHelper;

public class ItemStackInput implements CraftingInput {
  public final ItemStack itemStack;
  private final ItemStack[] itemStackArray;

  public ItemStackInput(ItemStack itemStack) {
    ItemStack i = itemStack.copy();
    i.count = 1;
    this.itemStack = i;
    this.itemStackArray = new ItemStack[]{itemStack};
  }

  @Override
  public boolean matches(ItemStack itemStack) {
    return InventoryHelper.sameItem(this.itemStack, itemStack);
  }

  @Override
  public ItemStack[] getMatching() {
    return itemStackArray;
  }
}
