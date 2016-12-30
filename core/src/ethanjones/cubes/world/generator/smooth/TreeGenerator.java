package ethanjones.cubes.world.generator.smooth;

import ethanjones.cubes.block.Block;
import ethanjones.cubes.block.Blocks;
import ethanjones.cubes.world.storage.Area;

public class TreeGenerator {
  
  public void generateTree(int x, int y, int z, int h, Area area) {
    //setBlock(area, Blocks.leaves, x - 2, y + h, z - 2);
    setBlock(area, Blocks.leaves, x - 1, y + h, z - 2);
    setBlock(area, Blocks.leaves, x + 0, y + h, z - 2);
    setBlock(area, Blocks.leaves, x + 1, y + h, z - 2);
    //setBlock(area, Blocks.leaves, x + 2, y + h, z - 2);
  
    setBlock(area, Blocks.leaves, x - 2, y + h, z - 1);
    setBlock(area, Blocks.leaves, x - 1, y + h, z - 1);
    setBlock(area, Blocks.leaves, x + 0, y + h, z - 1);
    setBlock(area, Blocks.leaves, x + 1, y + h, z - 1);
    setBlock(area, Blocks.leaves, x + 2, y + h, z - 1);
  
    setBlock(area, Blocks.leaves, x - 2, y + h, z + 0);
    setBlock(area, Blocks.leaves, x - 1, y + h, z + 0);
    setBlock(area, Blocks.leaves, x + 0, y + h, z + 0);
    setBlock(area, Blocks.leaves, x + 1, y + h, z + 0);
    setBlock(area, Blocks.leaves, x + 2, y + h, z + 0);
  
    setBlock(area, Blocks.leaves, x - 2, y + h, z + 1);
    setBlock(area, Blocks.leaves, x - 1, y + h, z + 1);
    setBlock(area, Blocks.leaves, x + 0, y + h, z + 1);
    setBlock(area, Blocks.leaves, x + 1, y + h, z + 1);
    setBlock(area, Blocks.leaves, x + 2, y + h, z + 1);
  
    //setBlock(area, Blocks.leaves, x - 2, y + h, z + 2);
    setBlock(area, Blocks.leaves, x - 1, y + h, z + 2);
    setBlock(area, Blocks.leaves, x + 0, y + h, z + 2);
    setBlock(area, Blocks.leaves, x + 1, y + h, z + 2);
    //setBlock(area, Blocks.leaves, x + 2, y + h, z + 2);
  
    //Second layer
  
    setBlock(area, Blocks.leaves, x - 2, y + h + 1, z + 0);
    setBlock(area, Blocks.leaves, x - 1, y + h + 1, z + 0);
    setBlock(area, Blocks.leaves, x + 0, y + h + 1, z + 0);
    setBlock(area, Blocks.leaves, x + 1, y + h + 1, z + 0);
    setBlock(area, Blocks.leaves, x + 2, y + h + 1, z + 0);
  
    setBlock(area, Blocks.leaves, x + 0, y + h + 1, z - 2);
    setBlock(area, Blocks.leaves, x + 0, y + h + 1, z - 1);
    setBlock(area, Blocks.leaves, x + 0, y + h + 1, z + 0);
    setBlock(area, Blocks.leaves, x + 0, y + h + 1, z + 1);
    setBlock(area, Blocks.leaves, x + 0, y + h + 1, z + 2);
  
    setBlock(area, Blocks.leaves, x + 1, y + h + 1, z + 1);
    setBlock(area, Blocks.leaves, x + 1, y + h + 1, z - 1);
    setBlock(area, Blocks.leaves, x - 1, y + h + 1, z + 1);
    setBlock(area, Blocks.leaves, x - 1, y + h + 1, z - 1);
  
    // Third layer
  
    setBlock(area, Blocks.leaves, x + 0, y + h + 2, z + 0);
    setBlock(area, Blocks.leaves, x + 0, y + h + 2, z + 1);
    setBlock(area, Blocks.leaves, x + 0, y + h + 2, z - 1);
    setBlock(area, Blocks.leaves, x + 1, y + h + 2, z + 0);
    setBlock(area, Blocks.leaves, x - 1, y + h + 2, z + 0);
  
    for (int i = 0; i < h + 2; i++) {
      setBlock(area, Blocks.log, x, y + i, z);
    }
  }
  
  protected void setBlock(Area area, Block block, int x, int y, int z) {
    if (area.minBlockX > x || area.minBlockX + Area.SIZE_BLOCKS <= x || area.minBlockZ > z || area.minBlockZ + Area.SIZE_BLOCKS <= z) {
      area = area.neighbourBlockCoordinates(x, z);
    }
    area.setBlock(block, x - area.minBlockX, y, z - area.minBlockZ, 0);
  }
}
