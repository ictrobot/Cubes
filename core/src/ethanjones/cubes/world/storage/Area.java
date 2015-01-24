package ethanjones.cubes.world.storage;

import ethanjones.cubes.block.Block;
import ethanjones.cubes.block.data.BlockData;
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
  public BlockData[] blockData;
  public boolean[] visible;

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
      return Sided.getBlockManager().toBlock(blocks[getRef(x, y, z)]);
    }
  }

  public boolean isBlank() {
    return blocks == null;
  }

  public int getRef(int x, int y, int z) {
    return x + z * SIZE_BLOCKS + y * SIZE_BLOCKS_SQUARED;
  }

  public BlockData getBlockData(int x, int y, int z) {
    if (isBlank()) return null;
    synchronized (this) {
      return blockData[getRef(x, y, z)];
    }
  }

  public void unload() {
    if (areaRenderer != null) Pools.free(AreaRenderer.class, areaRenderer);
    removeArrays();
    //blocks = null;
  }

  private void removeArrays() {
    synchronized (this) {
      blocks = null;
      blockData = null;
      visible = null;
    }
  }

  public int[] toIntArray() {
    synchronized (this) {
      if (isBlank()) {
        return new int[0];
      }
      int size = 0;
      for (int i = 0; i < SIZE_BLOCKS_CUBED; i++) {
        size++;
        if (blockData[i] != null) size += blockData[i].data.length;
      }
      int[] data = new int[size];
      int offset = 0;
      for (int i = 0; i < SIZE_BLOCKS_CUBED; i++) { //Includes blocks and blockData
        BlockData d = blockData[i];
        if (d == null) {
          data[offset++] = blocks[i];
        } else {
          data[offset++] = -blocks[i];
          for (int z : d.data) {
            data[offset++] = z;
          }
        }
      }
      return data;
    }
  }

  public void fromIntArray(int[] data) {
    synchronized (this) {
      if (data.length == 0) {
        removeArrays();
        return;
      }
      checkArrays();
      int offset = 0;
      for (int i = 0; i < SIZE_BLOCKS_CUBED; i++) {
        int v = data[offset++];
        if (v == 0) {
          blocks[i] = 0;
          blockData[i] = null;
        } else if (v > 0) {
          blocks[i] = v;
          blockData[i] = null;
        } else {
          blocks[i] = -v;
          BlockData d = Sided.getBlockManager().toBlock(-v).getBlockData();
          for (int z = 0; z < d.data.length; z++) {
            d.data[z] = data[offset++];
          }
        }
      }

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

  public void checkArrays() {
    synchronized (this) {
      if (isBlank()) setupArrays();
    }
  }

  private void update(int x, int y, int z, int i) {
    synchronized (this) {
      if (blocks[i] == 0) {
        visible[i] = false;
        return;
      }

      if (x < SIZE_BLOCKS - 1) {
        if (blocks[i + MAX_X_OFFSET] == 0) {
          visible[i] = true;
          return;
        }
      } else {
        visible[i] = true;
        return;
      }
      if (x > 0) {
        if (blocks[i + MIN_X_OFFSET] == 0) {
          visible[i] = true;
          return;
        }
      } else {
        visible[i] = true;
        return;
      }
      if (y < SIZE_BLOCKS - 1) {
        if (blocks[i + MAX_Y_OFFSET] == 0) {
          visible[i] = true;
          return;
        }
      } else {
        visible[i] = true;
        return;
      }
      if (y > 0) {
        if (blocks[i + MIN_Y_OFFSET] == 0) {
          visible[i] = true;
          return;
        }
      } else {
        visible[i] = true;
        return;
      }
      if (z < SIZE_BLOCKS - 1) {
        if (blocks[i + MAX_Z_OFFSET] == 0) {
          visible[i] = true;
          return;
        }
      } else {
        visible[i] = true;
        return;
      }
      if (z > 0) {
        if (blocks[i + MIN_Z_OFFSET] == 0) {
          visible[i] = true;
          //return;
        }
      } else {
        visible[i] = true;
        //return;
      }
    }
  }

  private void setupArrays() {
    synchronized (this) {
      blocks = new int[SIZE_BLOCKS_CUBED];
      blockData = new BlockData[SIZE_BLOCKS_CUBED];
      visible = new boolean[SIZE_BLOCKS_CUBED];
    }
  }

  public void setBlock(Block block, int x, int y, int z) {
    checkArrays();
    int ref = getRef(x, y, z);
    int b;
    synchronized (this) {
      b = blocks[ref];
      blocks[ref] = Sided.getBlockManager().toInt(block);
      blockData[ref] = block == null ? null : block.getBlockData();
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
}
