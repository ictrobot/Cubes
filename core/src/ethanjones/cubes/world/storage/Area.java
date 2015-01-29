package ethanjones.cubes.world.storage;

import ethanjones.cubes.block.Block;
import ethanjones.cubes.core.event.world.block.BlockChangedEvent;
import ethanjones.cubes.core.system.CubesException;
import ethanjones.cubes.graphics.world.AreaRenderer;
import ethanjones.cubes.side.Side;
import ethanjones.cubes.side.Sided;
import ethanjones.cubes.world.reference.BlockReference;

public class Area {

  public static final int SIZE_BLOCKS = 32;
  public static final int SIZE_BLOCKS_SQUARED = SIZE_BLOCKS * SIZE_BLOCKS;
  public static final int SIZE_BLOCKS_CUBED = SIZE_BLOCKS * SIZE_BLOCKS * SIZE_BLOCKS;
  public static final int HALF_SIZE_BLOCKS = SIZE_BLOCKS / 2;
  public static final int MAX_Y = Integer.MAX_VALUE / SIZE_BLOCKS_SQUARED;

  public static final int MAX_X_OFFSET = 1;
  public static final int MIN_X_OFFSET = -MAX_X_OFFSET;
  public static final int MAX_Y_OFFSET = SIZE_BLOCKS_SQUARED;
  public static final int MIN_Y_OFFSET = -MAX_Y_OFFSET;
  public static final int MAX_Z_OFFSET = SIZE_BLOCKS;
  public static final int MIN_Z_OFFSET = -MAX_Z_OFFSET;

  public final int areaX;
  public final int areaZ;
  public final int minBlockX;
  public final int minBlockZ;

  public AreaRenderer[] areaRenderer; //Always null on server
  public int[] blocks; //0 = null, positive = visible, negative = invisible
  public int maxY;

  private boolean unloaded;

  public Area(int areaX, int areaZ) {
    this.areaX = areaX;
    this.areaZ = areaZ;
    minBlockX = areaX * SIZE_BLOCKS;
    minBlockZ = areaZ * SIZE_BLOCKS;

    areaRenderer = null;
    blocks = null;
    maxY = 0;
    unloaded = false;
  }

  public Block getBlock(int x, int y, int z) {
    synchronized (this) {
      if (isBlank()) return null;
      if (y > maxY || y < 0) {
        return null;
      }
      return Sided.getBlockManager().toBlock(Math.abs(blocks[getRef(x, y, z)]));
    }
  }

  public boolean isBlank() {
    synchronized (this) {
      return blocks == null;
    }
  }

  public int getRef(int x, int y, int z) {
    return x + z * SIZE_BLOCKS + y * SIZE_BLOCKS_SQUARED;
  }

  public void unload() {
    synchronized (this) {
      removeArrays();
      unloaded = true;
    }
  }

  private void removeArrays() {
    synchronized (this) {
      if (unloaded) throw new CubesException("Area has been unloaded");
      blocks = null;
      AreaRenderer.free(areaRenderer);
      areaRenderer = null;
      maxY = 0;
    }
  }

  public int[] toIntArray() {
    synchronized (this) {
      return blocks == null ? new int[0] : blocks;
    }
  }

  public void fromIntArray(int[] data) {
    synchronized (this) {
      if (unloaded) throw new CubesException("Area has been unloaded");
      if (data.length == 0) {
        removeArrays();
      } else if (data.length % SIZE_BLOCKS_SQUARED == 0) {
        this.blocks = data;
        this.maxY = (data.length / SIZE_BLOCKS_SQUARED);

        AreaRenderer.free(areaRenderer); //don't copy, free
        if (Sided.getSide() == Side.Client) {
          areaRenderer = new AreaRenderer[maxY / SIZE_BLOCKS];
        } else {
          areaRenderer = null;
        }

        this.maxY--;
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
      if (y < maxY) {
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
      if (unloaded) throw new CubesException("Area has been unloaded");
      blocks = new int[SIZE_BLOCKS_SQUARED * SIZE_BLOCKS];
      AreaRenderer.free(areaRenderer);
      if (Sided.getSide() == Side.Client) areaRenderer = new AreaRenderer[]{null};
      maxY = SIZE_BLOCKS - 1;
    }
  }

  public void setBlock(Block block, int x, int y, int z) {
    synchronized (this) {
      if (isBlank()) {
        if (block == null) {
          return;
        } else {
          checkArrays();
        }
      }
      if (y < 0) return;
      if (y > maxY) expand(y);

      int ref = getRef(x, y, z);
      int b;
      b = blocks[ref];
      blocks[ref] = Sided.getBlockManager().toInt(block);

      updateSurrounding(x, y, z, ref);
      if (Sided.getSide() == Side.Client && areaRenderer[y / SIZE_BLOCKS] != null) {
        areaRenderer[y / SIZE_BLOCKS].refresh = true;
      }

      new BlockChangedEvent(new BlockReference().setFromBlockCoordinates(x + minBlockX, y, z + minBlockZ), Sided.getBlockManager().toBlock(b)).post();
    }
  }

  private void updateSurrounding(int x, int y, int z, int ref) {
    update(x, y, z, ref);
    if (y < SIZE_BLOCKS - 1) update(x + 1, y, z, ref + MAX_X_OFFSET);
    if (x > 0) update(x - 1, y, z, ref + MIN_X_OFFSET);
    if (y < maxY) update(x, y + 1, z, ref + MAX_Y_OFFSET);
    if (y > 0) update(x, y - 1, z, ref + MIN_Y_OFFSET);
    if (y < SIZE_BLOCKS - 1) update(x, y, z + 1, ref + MAX_Z_OFFSET);
    if (z > 0) update(x, y, z - 1, ref + MIN_Z_OFFSET);
  }

  public void updateAll() {
    if (isBlank()) return;
    int i = 0;
    for (int y = 0; y < maxY; y++) {
      for (int z = 0; z < SIZE_BLOCKS; z++) {
        for (int x = 0; x < SIZE_BLOCKS; x++, i++) {
          update(x, y, z, i);
        }
      }
    }
  }

  public void expand(int height) {
    synchronized (this) {
      if (unloaded) throw new CubesException("Area has been unloaded");
      if (height <= maxY) return;
      if (height > MAX_Y) return;

      height = (int) Math.ceil((height + 1) / (float) SIZE_BLOCKS); //Round up to multiple of SIZE_BLOCKS
      int oldMaxY = maxY;

      int[] oldBlocks = blocks;
      blocks = new int[SIZE_BLOCKS_CUBED * height];
      System.arraycopy(oldBlocks, 0, blocks, 0, oldBlocks.length);

      AreaRenderer.free(areaRenderer);
      if (Sided.getSide() == Side.Client) {
        areaRenderer = new AreaRenderer[height];
      } else {
        areaRenderer = null;
      }

      maxY = (height * SIZE_BLOCKS) - 1;
      int i = oldMaxY * SIZE_BLOCKS_SQUARED;
      for (int z = 0; z < SIZE_BLOCKS; z++) { //update previous top
        for (int x = 0; x < SIZE_BLOCKS; x++, i++) {
          updateSurrounding(x, oldMaxY, z, i);
        }
      }
    }
  }
}
