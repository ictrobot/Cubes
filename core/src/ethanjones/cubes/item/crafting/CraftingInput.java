package ethanjones.cubes.item.crafting;

import ethanjones.cubes.item.ItemStack;

public interface CraftingInput {

  public boolean matches(ItemStack itemStack);

  public ItemStack[] getMatching();

}
