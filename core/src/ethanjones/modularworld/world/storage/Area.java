package ethanjones.modularworld.world.storage;

import ethanjones.modularworld.ModularWorld;
import ethanjones.modularworld.block.Block;
import ethanjones.modularworld.core.events.world.block.SetBlockEvent;
import ethanjones.modularworld.graphics.world.AreaRenderer;
import ethanjones.modularworld.world.coordinates.BlockCoordinates;

import static ethanjones.modularworld.core.util.Maths.fastPositive;

public class Area {

  public static final int SIZE_BLOCKS = 32;
  public static final int SIZE_BLOCKS_SQUARED = SIZE_BLOCKS * SIZE_BLOCKS;
  public static final int SIZE_BLOCKS_CUBED = SIZE_BLOCKS_SQUARED * SIZE_BLOCKS;
  public static final int HALF_SIZE_BLOCKS = SIZE_BLOCKS / 2;

  public final int x;
  public final int y;
  public final int z;
  public final int maxBlockX;
  public final int maxBlockY;
  public final int maxBlockZ;
  public final float cenBlockX;
  public final float cenBlockY;
  public final float cenBlockZ;
  public final int minBlockX;
  public final int minBlockY;
  public final int minBlockZ;

  public boolean generated = false;
  public AreaRenderer areaRenderer;
  public Block[] blocks;

  /**
   * In area coords
   */
  public Area(int x, int y, int z) {
    this(x, y, z, true);
  }

  protected Area(int x, int y, int z, boolean render) {
    this.x = x;
    this.y = y;
    this.z = z;
    maxBlockX = ((x + 1) * SIZE_BLOCKS) - 1;
    maxBlockY = ((y + 1) * SIZE_BLOCKS) - 1;
    maxBlockZ = ((z + 1) * SIZE_BLOCKS) - 1;
    minBlockX = x * SIZE_BLOCKS;
    minBlockY = y * SIZE_BLOCKS;
    minBlockZ = z * SIZE_BLOCKS;
    cenBlockX = (float) (maxBlockX + minBlockX) / 2f;
    cenBlockY = (float) (maxBlockY + minBlockY) / 2f;
    cenBlockZ = (float) (maxBlockZ + minBlockZ) / 2f;

    blocks = new Block[SIZE_BLOCKS_CUBED];
    if (!ModularWorld.instance.compatibility.isHeadless() && render) {
      areaRenderer = new AreaRenderer(this);
    }
  }

  public Block getBlock(int x, int y, int z) {
    return blocks[fastPositive(x % SIZE_BLOCKS) + fastPositive(z % SIZE_BLOCKS) * SIZE_BLOCKS + fastPositive(y % SIZE_BLOCKS) * SIZE_BLOCKS_SQUARED];
  }

  public void setBlock(Block block, int x, int y, int z) {
    if (new SetBlockEvent(new BlockCoordinates(x, y, z), block).post()) {
      if (areaRenderer != null) areaRenderer.setDirty();
      blocks[fastPositive(x % SIZE_BLOCKS) + fastPositive(z % SIZE_BLOCKS) * SIZE_BLOCKS + fastPositive(y % SIZE_BLOCKS) * SIZE_BLOCKS_SQUARED] = block;
    }
  }

  public void unload() {
    areaRenderer.dispose();
    blocks = null;
  }
}
