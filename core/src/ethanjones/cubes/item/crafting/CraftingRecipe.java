package ethanjones.cubes.item.crafting;

import ethanjones.cubes.item.ItemStack;
import ethanjones.cubes.item.inv.CraftingInventory;

public class CraftingRecipe {

  public final CraftingInput[] input;
  public final ItemStack output;

  public CraftingRecipe(ItemStack output, Object... input) {
    this.output = output.copy();

    if (input == null) {
      this.input = null; // implement custom matching logic
    } else if (input.length == 9) {
      this.input = new CraftingInput[9];
      for (int i = 0; i < 9; i++) {
        this.input[i] = toCraftingInput(input[i]);
      }
    } else {
      throw new IllegalArgumentException(input.length + "");
    }
  }

  public ItemStack match(CraftingInventory inventory) {
    ItemStack[] itemStacks = inventory.itemStacks;
    for (int i = 0; i < 9; i++) {
      CraftingInput ci = input[i];
      ItemStack stack = itemStacks[i];
      if (ci == null) {
        if (stack == null) {
          continue;
        }
        return null;
      } else if (ci.matches(stack)) {
        continue;
      }
      return null;
    }
    return output;
  }

  public static CraftingInput toCraftingInput(Object o) {
    if (o == null) {
      return null;
    }
    if (o instanceof CraftingInput) {
      return (CraftingInput) o;
    } else if (o instanceof ItemStack) {
      return new ItemStackInput((ItemStack) o);
    }
    throw new IllegalArgumentException(String.valueOf(o));
  }

}
