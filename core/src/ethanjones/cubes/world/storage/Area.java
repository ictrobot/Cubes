package ethanjones.cubes.world.storage;

import ethanjones.cubes.block.Block;
import ethanjones.cubes.core.event.world.block.BlockChangedEvent;
import ethanjones.cubes.core.system.Pools;
import ethanjones.cubes.graphics.world.AreaRenderer;
import ethanjones.cubes.side.Sided;
import ethanjones.cubes.world.reference.BlockReference;

public class Area {

  public static final int SIZE_BLOCKS = 16;
  public static final int SIZE_BLOCKS_SQUARED = SIZE_BLOCKS * SIZE_BLOCKS;
  public static final int SIZE_BLOCKS_CUBED = SIZE_BLOCKS_SQUARED * SIZE_BLOCKS;
  public static final int HALF_SIZE_BLOCKS = SIZE_BLOCKS / 2;
  public static final int MAX_BLOCK_INDEX = SIZE_BLOCKS - 1;

  public static final int MAX_X_OFFSET = 1;
  public static final int MIN_X_OFFSET = -MAX_X_OFFSET;
  public static final int MAX_Y_OFFSET = SIZE_BLOCKS_SQUARED;
  public static final int MIN_Y_OFFSET = -MAX_Y_OFFSET;
  public static final int MAX_Z_OFFSET = SIZE_BLOCKS;
  public static final int MIN_Z_OFFSET = -MAX_Z_OFFSET;

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

  public AreaRenderer areaRenderer; //Always null on server

  public int[] blocks; //0 = null, positive = visible, negative = invisible

  public Area(int x, int y, int z) {
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
  }

  public Block getBlock(int x, int y, int z) {
    if (isBlank()) return null;
    synchronized (this) {
      return Sided.getBlockManager().toBlock(Math.abs(blocks[getRef(x, y, z)]));
    }
  }

  public boolean isBlank() {
    return blocks == null;
  }

  public int getRef(int x, int y, int z) {
    return x + z * SIZE_BLOCKS + y * SIZE_BLOCKS_SQUARED;
  }

  public void unload() {
    if (areaRenderer != null) Pools.free(AreaRenderer.class, areaRenderer);
    removeArrays();
    //blocks = null;
  }

  private void removeArrays() {
    synchronized (this) {
      blocks = null;
    }
  }

  public int[] toIntArray() {
    synchronized (this) {
      return blocks == null ? new int[0] : blocks;
    }
  }

  public void fromIntArray(int[] data) {
    synchronized (this) {
      if (data.length == 0) {
        removeArrays();
      } else if (data.length == SIZE_BLOCKS_CUBED) {
        this.blocks = data;
      } else {
        throw new IllegalArgumentException();
      }
    }
  }

  public void checkArrays() {
    synchronized (this) {
      if (isBlank()) setupArrays();
    }
  }

  private void update(int x, int y, int z, int i) {
    synchronized (this) {
      if (blocks[i] == 0) {
        return;
      }

      int block = Math.abs(blocks[i]);
      if (x < SIZE_BLOCKS - 1) {
        if (blocks[i + MAX_X_OFFSET] == 0) {
          blocks[i] = block;
          return;
        }
      } else {
        blocks[i] = block;
        return;
      }
      if (x > 0) {
        if (blocks[i + MIN_X_OFFSET] == 0) {
          blocks[i] = block;
          return;
        }
      } else {
        blocks[i] = block;
        return;
      }
      if (y < SIZE_BLOCKS - 1) {
        if (blocks[i + MAX_Y_OFFSET] == 0) {
          blocks[i] = block;
          return;
        }
      } else {
        blocks[i] = block;
        return;
      }
      if (y > 0) {
        if (blocks[i + MIN_Y_OFFSET] == 0) {
          blocks[i] = block;
          return;
        }
      } else {
        blocks[i] = block;
        return;
      }
      if (z < SIZE_BLOCKS - 1) {
        if (blocks[i + MAX_Z_OFFSET] == 0) {
          blocks[i] = block;
          return;
        }
      } else {
        blocks[i] = block;
        return;
      }
      if (z > 0) {
        if (blocks[i + MIN_Z_OFFSET] == 0) {
          blocks[i] = block;
          return;
        }
      } else {
        blocks[i] = block;
        return;
      }
      blocks[i] = -block;
    }
  }

  private void setupArrays() {
    synchronized (this) {
      blocks = new int[SIZE_BLOCKS_CUBED];
    }
  }

  public void setBlock(Block block, int x, int y, int z) {
    checkArrays();
    int ref = getRef(x, y, z);
    int b;
    synchronized (this) {
      b = blocks[ref];
      blocks[ref] = Sided.getBlockManager().toInt(block);
    }

    updateSurrounding(x, y, z, ref);
    if (areaRenderer != null) areaRenderer.refresh = true;

    new BlockChangedEvent(new BlockReference().setFromBlockCoordinates(x + minBlockX, y + minBlockY, z + minBlockZ), Sided.getBlockManager().toBlock(b)).post();
  }

  private void updateSurrounding(int x, int y, int z, int ref) {
    update(x, y, z, ref);
    if (y < MAX_BLOCK_INDEX) update(x + 1, y, z, ref + MAX_X_OFFSET);
    if (x > 0) update(x - 1, y, z, ref + MIN_X_OFFSET);
    if (y < MAX_BLOCK_INDEX) update(x, y + 1, z, ref + MAX_Y_OFFSET);
    if (y > 0) update(x, y - 1, z, ref + MIN_Y_OFFSET);
    if (y < MAX_BLOCK_INDEX) update(x, y, z + 1, ref + MAX_Z_OFFSET);
    if (z > 0) update(x, y, z - 1, ref + MIN_Z_OFFSET);
  }

  public void updateAll() {
    if (isBlank()) return;
    int i = 0;
    for (int y = 0; y < SIZE_BLOCKS; y++) {
      for (int z = 0; z < SIZE_BLOCKS; z++) {
        for (int x = 0; x < SIZE_BLOCKS; x++, i++) {
          update(x, y, z, i);
        }
      }
    }
  }
}
