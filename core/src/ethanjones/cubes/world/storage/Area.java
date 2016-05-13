package ethanjones.cubes.world.storage;

import ethanjones.cubes.block.Block;
import ethanjones.cubes.core.IDManager.TransparencyManager;
import ethanjones.cubes.core.event.world.block.BlockChangedEvent;
import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.system.CubesException;
import ethanjones.cubes.core.system.Executor;
import ethanjones.cubes.core.util.Lock;
import ethanjones.cubes.graphics.world.AreaRenderStatus;
import ethanjones.cubes.graphics.world.AreaRenderer;
import ethanjones.cubes.side.Side;
import ethanjones.cubes.side.Sided;
import ethanjones.cubes.world.World;
import ethanjones.cubes.world.light.SunLight;
import ethanjones.cubes.world.reference.AreaReference;
import ethanjones.cubes.world.reference.BlockReference;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

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

  public final Lock lock = new Lock();
  public final AtomicReference<Object> features;

  public final int areaX;
  public final int areaZ;
  public final int minBlockX;
  public final int minBlockZ;

  public volatile AreaRenderer[] areaRenderer; //Always null on server
  public volatile int[] blocks; //0 = null, positive = visible, negative = invisible
  public volatile byte[] light; // most significant 4 bits are sunlight. the least significant are lights
  public volatile int[] heightmap = new int[SIZE_BLOCKS_SQUARED];
  public volatile int maxY;
  public volatile int height;

  public int[] renderStatus = new int[0];

  private volatile boolean unloaded;

  public World world;

  private AreaReference tempReference = new AreaReference();
  private TransparencyManager transparency = Sided.getIDManager().transparencyManager;

  public Area(int areaX, int areaZ) {
    this.areaX = areaX;
    this.areaZ = areaZ;
    minBlockX = areaX * SIZE_BLOCKS;
    minBlockZ = areaZ * SIZE_BLOCKS;

    areaRenderer = null;
    blocks = null;
    maxY = 0;
    height = 0;
    unloaded = false;
    features = new AtomicReference<Object>();
  }

  public Block getBlock(int x, int y, int z) {
    lock.readLock();

    if (unreadyReadLock(y)) return null;
    int b = blocks[getRef(x, y, z)];

    return lock.readUnlock(Sided.getIDManager().toBlock(Math.abs(b)));
  }

  // Get the bits XXXX0000
  public int getSunlight(int x, int y, int z) {
    lock.readLock();
    if (unreadyReadLock(y)) return 15;
    int r = (light[getRef(x, y, z)] >> 4) & 0xF;
    lock.readUnlock();
    return r;
  }

  // Get the bits 0000XXXX
  public int getLight(int x, int y, int z) {
    lock.readLock();
    if (unreadyReadLock(y)) return 0;
    int r = (light[getRef(x, y, z)]) & 0xF;
    lock.readUnlock();
    return r;
  }

  // Set the bits XXXX0000
  public void setSunlight(int x, int y, int z, int l) {
    lock.writeLock();
    if (unreadyWriteLock(y)) return;
    int ref = getRef(x, y, z);
    light[ref] = (byte) ((light[ref] & 0xF) | (l << 4));
    lock.writeUnlock();
    updateRender(y / SIZE_BLOCKS);
  }

  // Set the bits 0000XXXX
  public void setLight(int x, int y, int z, int l) {
    lock.writeLock();
    if (unreadyWriteLock(y)) return;
    int ref = getRef(x, y, z);
    light[ref] = (byte) ((light[ref] & 0xF0) | l);
    lock.writeUnlock();
    updateRender(y / SIZE_BLOCKS);
  }

  public int getLightRaw(int x, int y, int z) {
    lock.readLock();
    if (y > maxY) return SunLight.MAX_SUNLIGHT;
    if (unreadyWriteLock(y)) return 0;
    int r = light[getRef(x, y, z)] & 0xFF; // byte is signed (-128 to 127) so & 0xFF to convert to 0-255
    lock.readUnlock();
    return r;
  }

  public boolean isBlank() {
    lock.readLock();
    return lock.readUnlock(blocks == null);
  }

  public boolean isUnloaded() {
    lock.readLock();
    return lock.readUnlock(unloaded);
  }

  // must be locked
  public boolean isReady() {
    return !unloaded && blocks != null;
  }

  public boolean unreadyWriteLock() {
    return isReady() ? false : lock.writeUnlock(true);
  }

  public boolean unreadyReadLock() {
    return isReady() ? false : lock.readUnlock(true);
  }

  public boolean unreadyWriteLock(int y) {
    return isReady() && y >= 0 && y <= maxY ? false : lock.writeUnlock(true);
  }

  public boolean unreadyReadLock(int y) {
    return isReady() && y >= 0 && y <= maxY ? false : lock.readUnlock(true);
  }

  public void unload() {
    lock.writeLock();

    removeArrays();
    unloaded = true;

    lock.writeUnlock();
  }

  private void removeArrays() {
    lock.writeLock();

    if (unloaded) {
      lock.writeUnlock();
      throw new CubesException("Area has been unloaded");
    }
    blocks = null;
    light = null;
    AreaRenderer.free(areaRenderer);
    areaRenderer = null;
    if (renderStatus.length > 0) renderStatus = new int[0];
    maxY = 0;
    height = 0;
    Arrays.fill(heightmap, 0);

    lock.writeUnlock();
  }

  //Should already be write locked
  private void update(int x, int y, int z, int i) {
    if (y > maxY || y < 0 || blocks[i] == 0) {
      return;
    }

    int block = Math.abs(blocks[i]);
    if (x < SIZE_BLOCKS - 1) {
      if (transparency.isTransparent(blocks[i + MAX_X_OFFSET])) {
        blocks[i] = block;
        return;
      }
    } else {
      blocks[i] = block;
      return;
    }
    if (x > 0) {
      if (transparency.isTransparent(blocks[i + MIN_X_OFFSET])) {
        blocks[i] = block;
        return;
      }
    } else {
      blocks[i] = block;
      return;
    }
    if (y < maxY) {
      if (transparency.isTransparent(blocks[i + MAX_Y_OFFSET])) {
        blocks[i] = block;
        return;
      }
    } else {
      blocks[i] = block;
      return;
    }
    if (y > 0) {
      if (transparency.isTransparent(blocks[i + MIN_Y_OFFSET])) {
        blocks[i] = block;
        return;
      }
    } else {
      blocks[i] = block;
      return;
    }
    if (z < SIZE_BLOCKS - 1) {
      if (transparency.isTransparent(blocks[i + MAX_Z_OFFSET])) {
        blocks[i] = block;
        return;
      }
    } else {
      blocks[i] = block;
      return;
    }
    if (z > 0) {
      if (transparency.isTransparent(blocks[i + MIN_Z_OFFSET])) {
        blocks[i] = block;
        return;
      }
    } else {
      blocks[i] = block;
      return;
    }
    blocks[i] = -block;
  }

  public void setBlock(Block block, int x, int y, int z) {
    if (y < 0) return;

    lock.writeLock();
    if (isUnloaded() && lock.writeUnlock(true)) return;
    setupArrays(y);

    int ref = getRef(x, y, z);
    int b;
    b = blocks[ref];
    blocks[ref] = Sided.getIDManager().toInt(block);

    doUpdates(x, y, z, ref);

    int hmRef = x + z * SIZE_BLOCKS;
    if (y > heightmap[hmRef] && block != null) heightmap[hmRef] = y;
    if (y == heightmap[hmRef] && block == null) calculateHeight(x, z);

    lock.writeUnlock();

    //Must be after lock released to prevent dead locks
    new BlockChangedEvent(new BlockReference().setFromBlockCoordinates(x + minBlockX, y, z + minBlockZ), Sided.getIDManager().toBlock(b), block).post();
  }

  public void doUpdates(int x, int y, int z, int ref) {
    updateSurrounding(x, y, z, ref);

    int section = y / SIZE_BLOCKS;

    Area negX = null;
    Area posX = null;
    Area negZ = null;
    Area posZ = null;

    if (world != null && (x == 0 || x == SIZE_BLOCKS - 1 || z == 0 || z == SIZE_BLOCKS - 1)) {
      world.lock.readLock();
      if (x == 0) {
        tempReference.setFromAreaCoordinates(areaX - 1, areaZ);
        negX = world.getArea(tempReference, false);
        if (negX != null) {
          negX.lock.writeLock();
          if (negX.isReady()) negX.update(SIZE_BLOCKS - 1, y, z, getRef(SIZE_BLOCKS - 1, y, z));
          negX.lock.writeUnlock();
        }
      } else if (x == SIZE_BLOCKS - 1) {
        tempReference.setFromAreaCoordinates(areaX + 1, areaZ);
        posX = world.getArea(tempReference, false);
        if (posX != null) {
          posX.lock.writeLock();
          if (posX.isReady()) posX.update(SIZE_BLOCKS + 1, y, z, getRef(SIZE_BLOCKS + 1, y, z));
          posX.lock.writeUnlock();
        }
      }
      if (z == 0) {
        tempReference.setFromAreaCoordinates(areaX, areaZ - 1);
        negZ = world.getArea(tempReference, false);
        if (negZ != null) {
          negZ.lock.writeLock();
          if (negZ.isReady()) negZ.update(x, y, SIZE_BLOCKS - 1, getRef(x, y, SIZE_BLOCKS - 1));
          negZ.lock.writeUnlock();
        }
      } else if (z == SIZE_BLOCKS - 1) {
        tempReference.setFromAreaCoordinates(areaX, areaZ + 1);
        posZ = world.getArea(tempReference, false);
        if (posZ != null) {
          posZ.lock.writeLock();
          if (posZ.isReady()) posZ.update(x, y, SIZE_BLOCKS + 1, getRef(x, y, SIZE_BLOCKS + 1));
          posZ.lock.writeUnlock();
        }
      }
      world.lock.readUnlock();
    }

    if (Sided.getSide() == Side.Client) {
      renderStatus[section] = AreaRenderStatus.UNKNOWN;
      if (areaRenderer[section] != null) areaRenderer[section].refresh = true;

      if (y > 0 && y % SIZE_BLOCKS == 0) {
        renderStatus[section - 1] = AreaRenderStatus.UNKNOWN;
        if (areaRenderer[section - 1] != null) areaRenderer[section - 1].refresh = true;
      }
      if (y < maxY && y % SIZE_BLOCKS == (SIZE_BLOCKS - 1)) {
        renderStatus[section + 1] = AreaRenderStatus.UNKNOWN;
        if (areaRenderer[section + 1] != null) areaRenderer[section + 1].refresh = true;
      }

      if (negX != null && y <= negX.maxY && negX.areaRenderer[section] != null) {
        negX.renderStatus[section] = AreaRenderStatus.UNKNOWN;
        negX.areaRenderer[section].refresh = true;
      }
      if (posX != null && y <= posX.maxY && posX.areaRenderer[section] != null) {
        posX.renderStatus[section] = AreaRenderStatus.UNKNOWN;
        posX.areaRenderer[section].refresh = true;
      }
      if (negZ != null && y <= negZ.maxY && negZ.areaRenderer[section] != null) {
        negZ.renderStatus[section] = AreaRenderStatus.UNKNOWN;
        negZ.areaRenderer[section].refresh = true;
      }
      if (posZ != null && y <= posZ.maxY && posZ.areaRenderer[section] != null) {
        posZ.renderStatus[section] = AreaRenderStatus.UNKNOWN;
        posZ.areaRenderer[section].refresh = true;
      }
    }
  }

  private void updateSurrounding(int x, int y, int z, int ref) {
    lock.writeLock();

    update(x, y, z, ref);
    if (x < SIZE_BLOCKS - 1) update(x + 1, y, z, ref + MAX_X_OFFSET);
    if (x > 0) update(x - 1, y, z, ref + MIN_X_OFFSET);
    if (y < maxY) update(x, y + 1, z, ref + MAX_Y_OFFSET);
    if (y > 0) update(x, y - 1, z, ref + MIN_Y_OFFSET);
    if (z < SIZE_BLOCKS - 1) update(x, y, z + 1, ref + MAX_Z_OFFSET);
    if (z > 0) update(x, y, z - 1, ref + MIN_Z_OFFSET);

    lock.writeUnlock();
  }

  public void updateRender(int section) {
    if (section < renderStatus.length) renderStatus[section] = AreaRenderStatus.UNKNOWN;
    if (areaRenderer != null && areaRenderer[section] != null) areaRenderer[section].refresh = true;
  }

  public void updateAll() {
    lock.writeLock();
    if (unreadyWriteLock()) return;

    if (!isBlank()) {
      int i = 0;
      for (int y = 0; y < maxY; y++) {
        for (int z = 0; z < SIZE_BLOCKS; z++) {
          for (int x = 0; x < SIZE_BLOCKS; x++, i++) {
            update(x, y, z, i);
          }
        }
      }
    }

    lock.writeUnlock();
  }

  public void setupArrays(int y) {
    lock.writeLock();

    if (unloaded) {
      lock.writeUnlock();
      throw new CubesException("Area has been unloaded");
    }
    if (isBlank()) {
      height = (int) Math.ceil((y + 1) / (float) SIZE_BLOCKS);
      blocks = new int[SIZE_BLOCKS_CUBED * height];
      light = new byte[SIZE_BLOCKS_CUBED * height];
      AreaRenderer.free(areaRenderer);
      if (Sided.getSide() == Side.Client) {
        areaRenderer = new AreaRenderer[height];
        renderStatus = AreaRenderStatus.create(height);
      }
      maxY = (height * SIZE_BLOCKS) - 1;
    } else if (y > maxY) {
      expand(y);
    }

    lock.writeUnlock();
  }

  public void rebuildHeightmap() {
    lock.writeLock();
    if (unreadyWriteLock()) return;

    for (int x = 0; x < SIZE_BLOCKS; x++) {
      forLoop:
      for (int z = 0; z < SIZE_BLOCKS; z++) {
        int column = x + z * SIZE_BLOCKS;
        int y = maxY;
        while (y >= 0) {
          if (blocks[column + y * SIZE_BLOCKS_SQUARED] != 0) {
            heightmap[column] = y;
            continue forLoop;
          }
          y--;
        }
        heightmap[column] = -1;
      }
    }
    lock.writeUnlock();
  }

  public void calculateHeight(int x, int z) {
    lock.writeLock();
    if (unreadyWriteLock()) return;

    int column = x + z * SIZE_BLOCKS;
    int y = maxY;
    while (y >= 0) {
      if (blocks[column + y * SIZE_BLOCKS_SQUARED] != 0) {
        heightmap[column] = y;
        lock.writeUnlock();
        return;
      }
      y--;
    }
    heightmap[column] = -1;
    lock.writeUnlock();
  }

  private void expand(int height) {
    lock.writeLock();

    if (unloaded) {
      lock.writeUnlock();
      throw new CubesException("Area has been unloaded");
    }
    if (isBlank() || height <= maxY || height > MAX_Y) {
      lock.writeUnlock();
      return;
    }

    height = (int) Math.ceil((height + 1) / (float) SIZE_BLOCKS); //Round up to multiple of SIZE_BLOCKS
    this.height = height;
    int oldMaxY = maxY;

    int[] oldBlocks = blocks;
    blocks = new int[SIZE_BLOCKS_CUBED * height];
    System.arraycopy(oldBlocks, 0, blocks, 0, oldBlocks.length);

    byte[] oldLight = light;
    light = new byte[SIZE_BLOCKS_CUBED * height];
    System.arraycopy(oldLight, 0, light, 0, oldLight.length);

    AreaRenderer.free(areaRenderer);
    if (Sided.getSide() == Side.Client) {
      areaRenderer = new AreaRenderer[height];
      if (renderStatus.length < height) {
        renderStatus = AreaRenderStatus.create(height);
      }
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

    lock.writeUnlock();
  }

  public boolean features() {
    return features.get() != null;
  }

  public void shrink() {
    lock.writeLock();

    if (unloaded) {
      lock.writeUnlock();
      throw new CubesException("Area has been unloaded");
    }
    if (isBlank()) {
      lock.writeUnlock();
      return;
    }

    int usedHeight = usedHeight();
    if (usedHeight == height) {
      lock.writeUnlock();
      return;
    }
    if (usedHeight == 0) {
      removeArrays();
      lock.writeUnlock();
      return;
    }

    int[] oldBlocks = blocks;
    blocks = new int[SIZE_BLOCKS_CUBED * usedHeight];
    System.arraycopy(oldBlocks, 0, blocks, 0, blocks.length);

    byte[] oldLight = light;
    light = new byte[SIZE_BLOCKS_CUBED * usedHeight];
    System.arraycopy(oldLight, 0, light, 0, light.length);

    AreaRenderer.free(areaRenderer);
    if (Sided.getSide() == Side.Client) {
      areaRenderer = new AreaRenderer[usedHeight];
      renderStatus = AreaRenderStatus.create(usedHeight);
    } else {
      areaRenderer = null;
    }

    maxY = (usedHeight * SIZE_BLOCKS) - 1;
    int i = maxY * SIZE_BLOCKS_SQUARED;
    for (int z = 0; z < SIZE_BLOCKS; z++) {
      for (int x = 0; x < SIZE_BLOCKS; x++, i++) {
        updateSurrounding(x, maxY, z, i); //update new top
      }
    }

    lock.writeUnlock();
  }

  //Should be read or write locked
  private int usedHeight() {
    int y = maxY;
    int i = blocks.length - 1;
    int maxUsedY = -1;
    while (y >= 0 && maxUsedY == -1) {
      for (int b = 0; b < SIZE_BLOCKS_SQUARED; b++) {
        if (blocks[i] != 0) {
          maxUsedY = y;
          break;
        }
        i--;
      }
      y--;
    }
    return (int) Math.ceil((maxUsedY + 1) / (float) SIZE_BLOCKS);
  }

  public void write(DataOutputStream dataOutputStream, boolean resize) throws IOException {
    lock.readLock();
    dataOutputStream.writeInt(areaX);
    dataOutputStream.writeInt(areaZ);
    if (isBlank()) {
      dataOutputStream.writeInt(0);
      lock.readUnlock();
      return;
    }

    int usedHeight = resize ? usedHeight() : height;
    dataOutputStream.writeInt(features() ? usedHeight : -usedHeight);

    for (int i = 0; i < SIZE_BLOCKS_SQUARED; i++) {
      dataOutputStream.writeInt(heightmap[i]);
    }

    int currentBlock = -1, num = 0;
    for (int i = 0; i < (SIZE_BLOCKS_CUBED * usedHeight); i++) {
      int block = blocks[i];
      block = (block < 0 ? -block : block) + 1; //shifted by 1 so blank is not 0, because -0 == 0
      if (block == currentBlock) {
        num++;
      } else {
        if (currentBlock != -1) {
          if (num == 1) {
            dataOutputStream.writeInt(currentBlock);
          } else {
            dataOutputStream.writeInt(-num);
            dataOutputStream.writeInt(currentBlock);
          }
        }
        currentBlock = block;
        num = 1;
      }
    }
    if (num == 1) {
      dataOutputStream.writeInt(currentBlock);
    } else {
      dataOutputStream.writeInt(-num);
      dataOutputStream.writeInt(currentBlock);
    }

    for (int i = 0; i < (SIZE_BLOCKS_CUBED * usedHeight); i++) {
      dataOutputStream.writeByte(light[i]);
    }

    if (usedHeight != height) {
      Executor.execute(new Runnable() {
        @Override
        public void run() {
          lock.writeLock();
          if (!unloaded)
            shrink();
          lock.writeUnlock();
        }
      });
    }

    lock.readUnlock();
  }

  public static Area read(DataInputStream dataInputStream) throws IOException {
    int areaX = dataInputStream.readInt();
    int areaZ = dataInputStream.readInt();
    Area area = new Area(areaX, areaZ);

    int height = dataInputStream.readInt();
    if (height == 0) return area;

    if (height > 0) { //if features
      area.features.set(Boolean.TRUE);
    } else {
      height = -height;
    }
    area.setupArrays((height * SIZE_BLOCKS) - 1);

    for (int i = 0; i < SIZE_BLOCKS_SQUARED; i++) {
      area.heightmap[i] = dataInputStream.readInt();
    }

    int counter = 0;
    while (counter < (SIZE_BLOCKS_CUBED * height)) {
      int a = dataInputStream.readInt();
      if (a > 0) {
        area.blocks[counter++] = a - 1;
      } else {
        int block = dataInputStream.readInt() - 1;
        for (int i = 0; i < -a; i++) {
          area.blocks[counter++] = block;
        }
      }
    }

    for (int i = 0; i < area.light.length; i++) {
      area.light[i] = dataInputStream.readByte();
    }

    area.updateAll();
    return area;
  }

  public static int getRef(int x, int y, int z) {
    return x + z * SIZE_BLOCKS + y * SIZE_BLOCKS_SQUARED;
  }

  public static int getHeightMapRef(int x, int z) {
    return x + z * SIZE_BLOCKS;
  }
}
