package ethanjones.cubes.block.blocks;

import ethanjones.cubes.block.Block;
import ethanjones.cubes.block.Blocks;
import ethanjones.cubes.item.ItemTool.ToolType;
import ethanjones.cubes.world.World;
import ethanjones.cubes.world.storage.Area;

public class BlockDirt extends Block {

  public BlockDirt() {
    super("core:dirt");

    miningTime = 0.5f;
    miningTool = ToolType.shovel;
    miningOther = true;
  }

  @Override
  public int randomTick(World world, Area area, int x, int y, int z, int meta) {
    if (area.getLight(x, y+1, z) > BlockGrass.MIN_LIGHT || (area.getSunlight(x, y+1, z) > BlockGrass.MIN_LIGHT && world.isDay())) {
      for (int i = y - 1; i <= y + 1; i++) {
        if (checkForGrass(world, area, x + 1, i, z + 1) || checkForGrass(world, area, x + 1, i, z) ||  checkForGrass(world, area, x + 1, i, z - 1) ||
            checkForGrass(world, area, x, i, z + 1) || checkForGrass(world, area, x, i, z - 1) ||
            checkForGrass(world, area, x - 1, i, z + 1) || checkForGrass(world, area, x - 1, i, z) || checkForGrass(world, area, x - 1, i, z - 1)) {
          area.setBlock(Blocks.grass, x, y, z, 0);
          return 0;
        }
      }
    }
    return meta;
  }

  private boolean checkForGrass(World world, Area area, int x, int y, int z) {
    if (x < 0 || x >= Area.SIZE_BLOCKS || z < 0 || z >= Area.SIZE_BLOCKS) {
      Area a = area.neighbourBlockCoordinates(x + area.minBlockX, z + area.minBlockZ);
      if (a != null) {
        int bX = (x + area.minBlockX) - a.minBlockX;
        int bZ = (z + area.minBlockZ) - a.minBlockZ;
        return a.getBlock(bX, y, bZ) == Blocks.grass;
      }
      return false;
    } else {
      return area.getBlock(x, y, z) == Blocks.grass;
    }
  }
}
