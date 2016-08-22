package ethanjones.cubes.item.crafting;

import ethanjones.cubes.item.ItemStack;
import ethanjones.cubes.item.inv.CraftingInventory;

import static ethanjones.cubes.item.crafting.CraftingPatterns.patterns;

public class CraftingRecipe {

  public final CraftingInput[] input;
  public final ItemStack output;

  public CraftingRecipe(ItemStack output, Object... input) {
    this.output = output.copy();

    if (input == null) {
      this.input = null; // implement custom matching logic
    } else if (input.length == 9 || input.length == 4 || input.length == 1) {
      this.input = new CraftingInput[input.length];
      for (int i = 0; i < input.length; i++) {
        this.input[i] = toCraftingInput(input[i]);
      }
    } else {
      throw new IllegalArgumentException(input.length + "");
    }
  }

  public ItemStack match(CraftingInventory inventory) {
    ItemStack[] itemStacks = inventory.itemStacks;
    boolean[][] pattern = patterns[input.length];

    patternLoop:
    for (boolean[] p : pattern) {
      int j = 0;
      for (int i = 0; i < 9; i++) {
        ItemStack stack = itemStacks[i];
        if (p[i]) {
          CraftingInput ci = input[j++];
          if (ci == null) {
            if (stack == null) {
              continue;
            }
            continue patternLoop;
          } else if (ci.matches(stack)) {
            continue;
          }
          continue patternLoop;
        } else {
          if (stack != null) continue patternLoop;
        }
      }
      return output;
    }
    return null;
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
