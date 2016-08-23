package ethanjones.cubes.block;

import ethanjones.cubes.core.IDManager;
import ethanjones.cubes.core.IDManager.GetBlock;
import ethanjones.cubes.item.ItemStack;
import ethanjones.cubes.item.crafting.CraftingManager;
import ethanjones.cubes.item.crafting.CraftingRecipe;

public class Blocks {

  @GetBlock("core:bedrock")
  public static Block bedrock;
  @GetBlock("core:stone")
  public static Block stone;
  @GetBlock("core:dirt")
  public static Block dirt;
  @GetBlock("core:grass")
  public static Block grass;
  @GetBlock("core:log")
  public static Block log;
  @GetBlock("core:leaves")
  public static Block leaves;
  @GetBlock("core:glow")
  public static Block glow;
  @GetBlock("core:glass")
  public static Block glass;

  public static Block chest;

  public static void init() {
    chest = new BlockChest();
    IDManager.register(chest);

    ItemStack c = new ItemStack(chest.getItemBlock(), 2);
    ItemStack l = new ItemStack(log.getItemBlock());
    CraftingManager.addRecipe(new CraftingRecipe(c, l, l, l, l, null, l, l, l, l));
  }
}
