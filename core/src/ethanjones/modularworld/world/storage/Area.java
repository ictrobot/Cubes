package ethanjones.modularworld.world.storage;

import ethanjones.modularworld.block.Block;
import ethanjones.modularworld.core.events.world.block.SetBlockEvent;
import ethanjones.modularworld.world.coordinates.BlockCoordinates;

public class Area {

  public static final int S = 32;
  private Block[][][] blocks = new Block[S][S][S];
  public final int x;
  public final int y;
  public final int z;
  public final int maxBlockX;
  public final int maxBlockY;
  public final int maxBlockZ;
  public final int minBlockX;
  public final int minBlockY;
  public final int minBlockZ;
  public boolean generated = false;

  /**
   * In area coords
   */
  public Area(int x, int y, int z) {
    this.x = x;
    this.y = y;
    this.z = z;

    maxBlockX = ((x + 1) * S) - 1;
    maxBlockY = ((y + 1) * S) - 1;
    maxBlockZ = ((z + 1) * S) - 1;
    minBlockX = x * S;
    minBlockY = y * S;
    minBlockZ = z * S;
  }

  public Block getBlock(int x, int y, int z) {
    return blocks[Math.abs(x % S)][Math.abs(y % S)][Math.abs(z % S)];
  }

  public Block[][][] getBlocks() {
    return blocks;
  }

  public void setBlock(Block block, int x, int y, int z) {
    if (new SetBlockEvent(new BlockCoordinates(x, y, z), block).post()) {
      blocks[Math.abs(x % S)][Math.abs(y % S)][Math.abs(z % S)] = block;
    }
  }
}
