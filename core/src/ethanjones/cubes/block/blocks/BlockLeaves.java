package ethanjones.cubes.block.blocks;

import ethanjones.cubes.block.Block;
import ethanjones.cubes.block.Blocks;
import ethanjones.cubes.core.util.Lock;
import ethanjones.cubes.core.util.ThreadRandom;
import ethanjones.cubes.item.ItemStack;
import ethanjones.cubes.item.ItemTool.ToolType;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.world.World;
import ethanjones.cubes.world.reference.BlockReference;
import ethanjones.cubes.world.storage.Area;

import java.util.ArrayDeque;
import java.util.HashSet;

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
  public int randomTick(World world, Area area, final int x, final int y, final int z, int meta) {
    if (meta > 0) {
      // if meta == 0  then the leave block was placed
      // otherwise the meta represents the distance from the tree

      int distance = 1001;
      distance = Math.min(distance, getDistanceToTree(area, x + 1, y, z));
      distance = Math.min(distance, getDistanceToTree(area, x - 1, y, z));
      distance = Math.min(distance, getDistanceToTree(area, x, y + 1, z));
      distance = Math.min(distance, getDistanceToTree(area, x, y - 1, z));
      distance = Math.min(distance, getDistanceToTree(area, x, y, z + 1));
      distance = Math.min(distance, getDistanceToTree(area, x, y, z - 1));

      if (distance == 1001) throw new IllegalStateException();
      if (distance == -1) return meta; // area not loaded

      if (distance == meta - 1) {
        return meta;
      } else if (distance >= meta) {
        area.setBlock(null, x, y, z, 0);
        dropItems(Cubes.getServer().world, x + area.minBlockX, y, z + area.minBlockZ, meta);
      } else {
        return distance + 1;
      }
    }
    return meta;
  }
  
  private int getDistanceToTree(Area area, int x, int y, int z) {
    if (x < 0 || x >= Area.SIZE_BLOCKS || z < 0 || z >= Area.SIZE_BLOCKS) {
      Area a = area.neighbourBlockCoordinates(x + area.minBlockX, z + area.minBlockZ);
      if (a == null) return -1;
      x = (x + area.minBlockX) - a.minBlockX;
      z = (z + area.minBlockZ) - a.minBlockZ;
      area = a;
    }

    if (Lock.tryToLock(false, area)) {
      Block block = area.getBlock(x, y, z);
      if (block == Blocks.log) return area.lock.readUnlock(0);
      if (block != this) return area.lock.readUnlock(1000); // no leaf block
      int meta = area.getMeta(x, y, z);
      if (meta == 0) meta = 1000; // don't count place leaf blocks
      if (meta > 15) meta = 15; // this shouldn't happern
      return area.lock.readUnlock(meta);
    }
    return -1; // area not loaded / area locked
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
