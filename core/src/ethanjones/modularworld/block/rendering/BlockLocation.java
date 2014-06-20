package ethanjones.modularworld.block.rendering;

import ethanjones.modularworld.block.Block;

import java.util.ArrayList;

public class BlockLocation {
  private ArrayList<Integer[]> list;
  private Block block;

  public BlockLocation() {
    list = new ArrayList<Integer[]>();
  }

  public void add(int x, int y, int z) {
    list.add(new Integer[]{x, y, z});
  }

  public boolean matches(Block block) {
    if (block == null) {
      return false;
    }
    if (this.block == null) {
      this.block = block;
      return true;
    }
    return this.block.equals(block);
  }

  public ArrayList<Integer[]> getList() {
    return list;
  }

  public void clear() {
    list.clear();
  }
}
