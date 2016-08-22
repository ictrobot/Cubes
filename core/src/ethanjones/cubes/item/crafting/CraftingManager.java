package ethanjones.cubes.item.crafting;

import ethanjones.cubes.item.ItemStack;
import ethanjones.cubes.item.inv.CraftingInventory;

import java.util.ArrayList;
import java.util.List;

public class CraftingManager {

  private static final List<CraftingRecipe> list = new ArrayList<CraftingRecipe>();

  public static void addRecipe(CraftingRecipe recipe) {
    if (recipe != null && !list.contains(recipe)) list.add(recipe);
  }

  public static ItemStack match(CraftingInventory inventory) {
    for (CraftingRecipe craftingRecipe : list) {
      ItemStack stack = craftingRecipe.match(inventory);
      if (stack != null) return stack.copy();
    }
    return null;
  }

}
