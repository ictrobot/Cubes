package ethanjones.cubes.block;

import ethanjones.cubes.core.util.ThreadRandom;
import ethanjones.cubes.item.ItemStack;
import ethanjones.cubes.item.ItemTool.ToolType;
import ethanjones.cubes.world.World;

public class BlockLeaves extends Block {
  
  public BlockLeaves() {
    super("core:leaves");
    miningTime = 0.25f;
    miningTool = ToolType.none;
    miningOther = true;
  }
  
  @Override
  public boolean alwaysTransparent() {
    return true;
  }
  
  @Override
  public boolean isTransparent(int meta) {
    return true;
  }
  
  @Override
  public ItemStack[] drops(World world, int x, int y, int z, int meta) {
    boolean sapling = ThreadRandom.get().nextInt(24) == 0;
    boolean leaves = ThreadRandom.get().nextInt(6) == 0;
    if (sapling && leaves) {
      return new ItemStack[]{new ItemStack(Blocks.sapling.getItemBlock()), new ItemStack(Blocks.leaves.getItemBlock())};
    } else if (sapling) {
      return new ItemStack[]{new ItemStack(Blocks.sapling.getItemBlock())};
    } else if (leaves) {
      return new ItemStack[]{new ItemStack(Blocks.leaves.getItemBlock())};
    } else {
      return new ItemStack[0];
    }
  }
}
