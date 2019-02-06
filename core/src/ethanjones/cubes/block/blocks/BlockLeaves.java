package ethanjones.cubes.block.blocks;

import ethanjones.cubes.block.Block;
import ethanjones.cubes.block.Blocks;
import ethanjones.cubes.core.util.IntQueue;
import ethanjones.cubes.core.util.ThreadRandom;
import ethanjones.cubes.item.ItemStack;
import ethanjones.cubes.item.ItemTool.ToolType;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.world.World;
import ethanjones.cubes.world.storage.Area;

import java.util.Arrays;

public class BlockLeaves extends Block {

  private static final boolean[] randomTickChecked = new boolean[9 * 9 * 9];
  private static final IntQueue randomTickTodo = new IntQueue(9 * 9 * 9);

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
  public void randomTick(World world, Area area, final int blockX, final int blockY, final int blockZ, int meta) {
    if (meta == 1) {
      Arrays.fill(randomTickChecked, false);
      randomTickTodo.clear();

      add(0, 0, 0);
      while (!randomTickTodo.isEmpty()) {
        int poll = randomTickTodo.poll();
        int cx = (poll / 9 / 9) - 4, cy = ((poll / 9) % 9) - 4, cz = (poll % 9) - 4;
        int x = cx + blockX, y = cy + blockY, z = cz + blockZ;
        Area a = area;
        if (x < 0 || x >= Area.SIZE_BLOCKS || z < 0 || z >= Area.SIZE_BLOCKS) {
          a = area.neighbourBlockCoordinates(x + area.minBlockX, z + area.minBlockZ);
          if (a == null) return; //otherwise leaves may decay if area containing log is not loaded
          x = x + area.minBlockX - a.minBlockX;
          z = z + area.minBlockZ - a.minBlockZ;
        }
        if (y < 0 || y > a.maxY) continue;
        Block b = a.getBlock(x, y, z);
        if (b == Blocks.leaves) {
          add(cx + -1, cy, cz);
          add(cx + 1, cy, cz);
          add(cx, cy + -1, cz);
          add(cx, cy + 1, cz);
          add(cx, cy, cz + -1);
          add(cx, cy, cz + 1);
        } else if (b == Blocks.log) {
          return;
        }
      }
      area.setBlock(null, blockX, blockY, blockZ, 0);
      dropItems(Cubes.getServer().world, blockX + area.minBlockX, blockY, blockZ + area.minBlockZ, meta);
    }
  }

  private int getPos(int x, int y, int z) {
    return ((x + 4) * 9 * 9) + ((y + 4) * 9) + (z + 4);
  }

  private void add(int cx, int cy, int cz) {
    if (cx < -4 || cx > 4 || cy < -4 || cy > 4 || cz < -4 || cz > 4) return;
    int pos = getPos(cx, cy, cz);
    if (!randomTickChecked[pos]) {
      randomTickChecked[pos] = true;
      randomTickTodo.enqueue(pos);
    }
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
