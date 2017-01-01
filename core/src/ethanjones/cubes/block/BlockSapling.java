package ethanjones.cubes.block;

import ethanjones.cubes.core.util.ThreadRandom;
import ethanjones.cubes.item.ItemTool.ToolType;
import ethanjones.cubes.world.World;
import ethanjones.cubes.world.generator.smooth.TreeGenerator;
import ethanjones.cubes.world.storage.Area;

public class BlockSapling extends Block {
  public BlockSapling() {
    super("core:sapling");
    miningTime = 0.4f;
    miningTool = ToolType.none;
    miningOther = true;
  }
  
  @Override
  public BlockRenderType renderType(int meta) {
    return BlockRenderType.CROSS;
  }
  
  @Override
  public boolean alwaysTransparent() {
    return true;
  }
  
  @Override
  public int randomTick(World world, Area area, int x, int y, int z, int meta) {
    if ((world.isDay() && area.getSunlight(x, y + 1, z) >= 10) || area.getLight(x, y + 1, z) >= 10) {
      if (meta >= 7) {
        new TreeGenerator().generateTree(x + area.minBlockX, y, z + area.minBlockZ, 3 + ThreadRandom.get().nextInt(3), area);
      } else {
        meta++;
      }
    }
    return meta;
  }
}
