package ethanjones.cubes.block;

import ethanjones.cubes.core.util.BlockFace;
import ethanjones.cubes.core.util.Lock;
import ethanjones.cubes.graphics.world.BlockTextureHandler;
import ethanjones.cubes.item.ItemStack;
import ethanjones.cubes.item.ItemTool.ToolType;
import ethanjones.cubes.world.World;
import ethanjones.cubes.world.storage.Area;

public class BlockGrass extends Block {

  public BlockGrass() {
    super("core:grass");

    miningTime = 0.6f;
    miningTool = ToolType.shovel;
    miningOther = true;
  }

  @Override
  public void loadGraphics() {
    BlockTextureHandler handler = new BlockTextureHandler("core:grass_side");
    handler.setSide(BlockFace.posY, "core:grass_top");
    handler.setSide(BlockFace.negY, "core:dirt");
    textureHandlers = new BlockTextureHandler[]{handler};
  }
  
  @Override
  public ItemStack[] drops(World world, int x, int y, int z, int meta) {
    return new ItemStack[]{new ItemStack(Blocks.dirt.getItemBlock(), 1, 0)};
  }
  
  @Override
  public int randomTick(World world, Area area, int x, int y, int z, int meta) {
    if (y < area.maxY) {
      if (!validGrass(world, area, x, y, z, true)) {
        area.setBlock(Blocks.dirt, x, y, z, 0);
      }
    }
  
    for (int i = y - 1; i <= y + 1; i++) {
      checkDirt(world, area, x + 1, i, z + 1);
      checkDirt(world, area, x + 1, i, z);
      checkDirt(world, area, x + 1, i, z - 1);
      checkDirt(world, area, x, i, z + 1);
      checkDirt(world, area, x, i, z - 1);
      checkDirt(world, area, x - 1, i, z + 1);
      checkDirt(world, area, x - 1, i, z);
      checkDirt(world, area, x - 1, i, z - 1);
    }
    return meta;
  }
  
  private boolean validGrass(World world, Area area, int x, int y, int z, boolean skipTimeCheck) {
    Block above = area.getBlock(x, y + 1, z);
    int aboveMeta = area.getMeta(x, y + 1, z);
    if (above != null && !above.isTransparent(aboveMeta)) {
      return false;
    } else if (((skipTimeCheck || world.isDay()) && area.getSunlight(x, y + 1, z) >= 10) || area.getLight(x, y + 1, z) >= 10) {
      return true;
    }
    return false;
  }
  
  private void checkDirt(World world, Area area, int x, int y, int z) {
    if (x < 0 || x >= Area.SIZE_BLOCKS || z < 0 || z >= Area.SIZE_BLOCKS) {
      Area a = area.neighbourBlockCoordinates(x + area.minBlockX, z + area.minBlockZ);
      if (a == null) return;
      if (Lock.tryToLock(true, a)) {
        int bX = (x + area.minBlockX) - a.minBlockX;
        int bZ = (z + area.minBlockZ) - a.minBlockZ;
        if (a.getBlock(bX, y, bZ) == Blocks.dirt && validGrass(world, a, bX, y, bZ, false)) {
          a.setBlock(Blocks.grass, bX, y, bZ, 0);
        }
        a.lock.writeUnlock();
      }
    } else {
      if (area.getBlock(x, y, z) == Blocks.dirt && validGrass(world, area, x, y, z, false)) {
        area.setBlock(Blocks.grass, x, y, z, 0);
      }
    }
  }
}
