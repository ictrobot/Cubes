package ethanjones.cubes.block;

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
  public int randomTick(World world, Area area, final int blockX, final int blockY, final int blockZ, int meta) {
    if (meta == 1) {
      HashSet<BlockReference> checked = new HashSet<BlockReference>();
      ArrayDeque<BlockReference> todo = new ArrayDeque<BlockReference>();
      BlockReference start = new BlockReference().setFromBlockCoordinates(blockX, blockY, blockZ);
      todo.add(start.copy().offset(-1, 0, 0));
      todo.add(start.copy().offset(1, 0, 0));
      todo.add(start.copy().offset(0, -1, 0));
      todo.add(start.copy().offset(0, 1, 0));
      todo.add(start.copy().offset(0, 0, -1));
      todo.add(start.copy().offset(0, 0, 1));
      while (!todo.isEmpty()) {
        BlockReference poll = todo.poll();
        int x = poll.blockX, y = poll.blockY, z = poll.blockZ;
        Area a = area;
        if (x < 0 || x >= Area.SIZE_BLOCKS || z < 0 || z >= Area.SIZE_BLOCKS) {
          a = area.neighbourBlockCoordinates(x + area.minBlockX, z + area.minBlockZ);
          if (a == null) return meta; //otherwise leaves may decay if area containing log is not loaded
          x = x + area.minBlockX - a.minBlockX;
          z = z + area.minBlockZ - a.minBlockZ;
        }
        if (y < 0 || y > a.maxY) continue;
        Block b = a.getBlock(x, y, z);
        if (b == Blocks.leaves) {
          add(checked, todo, start, poll, -1, 0, 0);
          add(checked, todo, start, poll, 1, 0, 0);
          add(checked, todo, start, poll, 0, -1, 0);
          add(checked, todo, start, poll, 0, 1, 0);
          add(checked, todo, start, poll, 0, 0, -1);
          add(checked, todo, start, poll, 0, 0, 1);
        } else if (b == Blocks.log) {
          return meta;
        }
      }
      area.setBlock(null, blockX, blockY, blockZ, 0);
      dropItems(Cubes.getServer().world, blockX + area.minBlockX, blockY, blockZ + area.minBlockZ, meta);
    }
    return meta;
  }
  
  private void add(HashSet<BlockReference> checked, ArrayDeque<BlockReference> todo, BlockReference start, BlockReference b, int x, int y, int z) {
    b = b.copy().offset(x, y, z);
    int dX = start.blockX - b.blockX;
    int dY = start.blockY - b.blockY;
    int dZ = start.blockZ - b.blockZ;
    int distance2 = dX * dX + dY * dY + dZ * dZ;
    if (distance2 <= 16 && checked.add(b)) todo.add(b);
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
