package ethanjones.cubes.world.storage;

import ethanjones.cubes.block.Block;
import ethanjones.cubes.block.data.BlockData;
import ethanjones.cubes.core.event.world.block.BlockChangedEvent;
import ethanjones.cubes.core.id.IDManager;
import ethanjones.cubes.core.id.TransparencyManager;
import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.system.CubesException;
import ethanjones.cubes.core.system.Executor;
import ethanjones.cubes.core.util.Lock;
import ethanjones.cubes.core.util.ThreadRandom;
import ethanjones.cubes.graphics.world.AreaRenderStatus;
import ethanjones.cubes.graphics.world.AreaRenderer;
import ethanjones.cubes.networking.NetworkingManager;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.side.common.Side;
import ethanjones.cubes.world.CoordinateConverter;
import ethanjones.cubes.world.light.SunLight;
import ethanjones.cubes.world.reference.BlockReference;
import ethanjones.data.Data;
import ethanjones.data.DataGroup;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;

public class Area implements Lock.HasLock {

  public static final int BLOCK_VISIBLE = 1 << 28;

  public static final int SIZE_BLOCKS = 32;
  public static final int SIZE_BLOCKS_POW2 = 5;
  public static final int SIZE_BLOCKS_SQUARED = SIZE_BLOCKS * SIZE_BLOCKS;
  public static final int SIZE_BLOCKS_CUBED = SIZE_BLOCKS * SIZE_BLOCKS * SIZE_BLOCKS;
  public static final int HALF_SIZE_BLOCKS = SIZE_BLOCKS / 2;
  public static final int MAX_Y = ((Integer.MAX_VALUE - 8) / SIZE_BLOCKS_CUBED) * SIZE_BLOCKS;

  // update every block about once a minute
  public static final int NUM_RANDOM_UPDATES = SIZE_BLOCKS_CUBED / (1000 / Cubes.tickMS) / 60;

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
  public final int hashCode;

  // Least significant
  // ID (20 bits, 0- 1,048,575)
  // Meta (8 bits, 0-255)
  // Visible (1 bit)
  //
  // blank == 0, as id=0 meta=0 visible=0
  //
  //int blockID = blocks[i] & 0xFFFFF;
  //int blockMeta = (blocks[i] >> 20) & 0xFF;
  //boolean blockVisible = (blocks[i] & BLOCK_VISIBLE) == BLOCK_VISIBLE;
  public volatile int[] blocks;
  public volatile byte[] light; // most significant 4 bits are sunlight. the least significant are lights
  public volatile int[] heightmap = new int[SIZE_BLOCKS_SQUARED];
  public volatile AreaRenderer[] areaRenderer; //Always null on server, unless shared
  public volatile int maxY;
  public volatile int height;
  public volatile ArrayList<BlockData> blockDataList = new ArrayList<BlockData>(0);
  private volatile int modCount = 0, saveModCount = -1, saveEntities = 0;

  public int[] renderStatus = new int[0];

  private volatile boolean unloaded;
  
  private volatile Area[] neighboursClient = new Area[9];
  private volatile Area[] neighboursServer = new Area[9];
  private AreaMap areaMapClient;
  private AreaMap areaMapServer;
  public final boolean shared;

  public Area(int areaX, int areaZ) {
    this.areaX = areaX;
    this.areaZ = areaZ;
    minBlockX = areaX * SIZE_BLOCKS;
    minBlockZ = areaZ * SIZE_BLOCKS;

    int hashCode = 7;
    hashCode = 31 * hashCode + areaX;
    this.hashCode = 31 * hashCode + areaZ;

    areaRenderer = null;
    blocks = null;
    maxY = 0;
    height = 0;
    unloaded = false;
    features = new AtomicReference<Object>();

    this.shared = isShared();
  }

  public Area(Area toCopy) {
    this(toCopy.areaX, toCopy.areaZ);
    toCopy.lock.readLock();
    if (toCopy.isReady()) {
      this.setupArrays(toCopy.maxY);
      System.arraycopy(toCopy.blocks, 0, this.blocks, 0, this.blocks.length);
      System.arraycopy(toCopy.light, 0, this.light, 0, this.light.length);
      System.arraycopy(toCopy.heightmap, 0, this.heightmap, 0, this.heightmap.length);
      if (toCopy.features.get() != null) this.features.set(Boolean.TRUE);
    }
    toCopy.lock.readUnlock();
  }

  public Block getBlock(int x, int y, int z) {
    lock.readLock();

    if (unreadyReadLock(y)) return null;
    int b = blocks[getRef(x, y, z)] & 0xFFFFF;

    return lock.readUnlock(IDManager.toBlock(b));
  }

  public int getMeta(int x, int y, int z) {
    lock.readLock();

    if (unreadyReadLock(y)) return 0;
    int b = (blocks[getRef(x, y, z)] >> 20) & 0xFF;

    return lock.readUnlock(b);
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

  public int getMaxLight(int x, int y, int z) {
    lock.readLock();
    if (unreadyReadLock(y)) return 15;
    int sunlight = (light[getRef(x, y, z)] >> 4) & 0xF;
    int blocklight = (light[getRef(x, y, z)]) & 0xF;
    lock.readUnlock();
    return sunlight > blocklight ? sunlight : blocklight;
  }

  // Set the bits XXXX0000
  public void setSunlight(int x, int y, int z, int l) {
    lock.writeLock();
    if (unreadyWriteLock(y)) return;
    int ref = getRef(x, y, z);
    light[ref] = (byte) ((light[ref] & 0xF) | (l << 4));
    modify();
    lock.writeUnlock();
    updateRender(y / SIZE_BLOCKS);
  }

  // Set the bits 0000XXXX
  public void setLight(int x, int y, int z, int l) {
    lock.writeLock();
    if (unreadyWriteLock(y)) return;
    int ref = getRef(x, y, z);
    light[ref] = (byte) ((light[ref] & 0xF0) | l);
    modify();
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
    if (shared && Side.isClient()) return;
    lock.writeLock();
  
    if (!unloaded) removeArrays();

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
    blockDataList.clear();
    AreaRenderer.free(areaRenderer);
    areaRenderer = null;
    if (renderStatus.length > 0) renderStatus = new int[0];
    maxY = 0;
    height = 0;
    Arrays.fill(heightmap, 0);
    Arrays.fill(neighboursClient, null);
    Arrays.fill(neighboursServer, null);
    
    unloaded = true;
    lock.writeUnlock();
  }

  //Should already be write locked
  private void update(int x, int y, int z, int i) {
    if (y > maxY || y < 0) {
      return;
    }
    if (blocks[i] == 0) return; // air cannot be visible

    blocks[i] &= 0xFFFFFFF; // keep block id and meta

    if (x < SIZE_BLOCKS - 1) {
      if (TransparencyManager.isTransparent(blocks[i + MAX_X_OFFSET])) {
        blocks[i] |= BLOCK_VISIBLE;
        return;
      }
    } else {
      blocks[i] |= BLOCK_VISIBLE;
      return;
    }
    if (x > 0) {
      if (TransparencyManager.isTransparent(blocks[i + MIN_X_OFFSET])) {
        blocks[i] |= BLOCK_VISIBLE;
        return;
      }
    } else {
      blocks[i] |= BLOCK_VISIBLE;
      return;
    }
    if (y < maxY) {
      if (TransparencyManager.isTransparent(blocks[i + MAX_Y_OFFSET])) {
        blocks[i] |= BLOCK_VISIBLE;
        return;
      }
    } else {
      blocks[i] |= BLOCK_VISIBLE;
      return;
    }
    if (y > 0) {
      if (TransparencyManager.isTransparent(blocks[i + MIN_Y_OFFSET])) {
        blocks[i] |= BLOCK_VISIBLE;
        return;
      }
    } else {
      blocks[i] |= BLOCK_VISIBLE;
      return;
    }
    if (z < SIZE_BLOCKS - 1) {
      if (TransparencyManager.isTransparent(blocks[i + MAX_Z_OFFSET])) {
        blocks[i] |= BLOCK_VISIBLE;
        return;
      }
    } else {
      blocks[i] |= BLOCK_VISIBLE;
      return;
    }
    if (z > 0) {
      if (TransparencyManager.isTransparent(blocks[i + MIN_Z_OFFSET])) {
        blocks[i] |= BLOCK_VISIBLE;
        return;
      }
    } else {
      blocks[i] |= BLOCK_VISIBLE;
      return;
    }
  }

  public void setBlock(Block block, int x, int y, int z, int meta) {
    if (y < 0) return;
  
    int n = block == null ? 0 : block.intID;
    n += (meta & 0xFF) << 20;

    lock.writeLock();
    if (isUnloaded() && lock.writeUnlock(true)) return;
    setupArrays(y);

    int ref = getRef(x, y, z);
    int b = blocks[ref];
    blocks[ref] = n;

    Block old = IDManager.toBlock(b & 0xFFFFF);

    if (old != null && old.blockData() && old != block) {
      removeBlockData(x, y, z);
    }
    if (block != null && block.blockData()) {
      addBlockData(block, x, y, z, meta);
    }

    doUpdatesThisArea(x, y, z, ref);

    int hmRef = x + z * SIZE_BLOCKS;
    if (y > heightmap[hmRef] && block != null) heightmap[hmRef] = y;
    if (y == heightmap[hmRef] && block == null) calculateHeight(x, z);

    modify();
    lock.writeUnlock();

    //Must be after lock released to prevent dead locks
    if (TransparencyManager.isTransparent(b) != TransparencyManager.isTransparent(n))
      doUpdatesOtherAreas(x, y, z, ref);
    new BlockChangedEvent(new BlockReference().setFromBlockCoordinates(x + minBlockX, y, z + minBlockZ), old, (b >> 20) & 0xFF, block, meta, this).post();
  }

  public BlockData removeBlockData(int x, int y, int z) {
    lock.writeLock();

    Iterator<BlockData> iterator = blockDataList.iterator();
    while (iterator.hasNext()) {
      BlockData blockData = iterator.next();
      if (blockData.getX() == x && blockData.getY() == y && blockData.getZ() == z) {
        iterator.remove();
        return lock.writeUnlock(blockData);
      }
    }

    lock.writeUnlock();
    return null;
  }


  public BlockData getBlockData(int x, int y, int z) {
    lock.writeLock();

    for (BlockData blockData : blockDataList) {
      if (blockData.getX() == x && blockData.getY() == y && blockData.getZ() == z) {
        return lock.writeUnlock(blockData);
      }
    }

    lock.writeUnlock();
    return null;
  }

  public BlockData addBlockData(Block block, int x, int y, int z, int meta) {
    BlockData data = block.createBlockData(this, x, y, z, meta, null);
    addBlockData(data);
    return data;
  }

  public void addBlockData(BlockData blockData) {
    lock.writeLock();

    blockDataList.add(blockData);

    lock.writeUnlock();
  }
  
  public Area neighbourBlockCoordinates(int blockX, int blockZ) {
    return neighbour(CoordinateConverter.area(blockX), CoordinateConverter.area(blockZ));
  }
  
  public Area neighbour(final int areaX, final int areaZ) {
    if (areaX == this.areaX && areaZ == this.areaZ) return this;
    Side side = Side.getSide();
    if (side == Side.Client) {
      if (areaMapClient == null) return null;
      int aX = areaX - this.areaX;
      int aZ = areaZ - this.areaZ;
      if (aX < -1 || aX > 1 || aZ < -1 || aZ > 1) return areaMapClient.getArea(areaX, areaZ);
      int n = (aX + 1) + ((aZ + 1) * 3);
      Area a = neighboursClient[n];
      if (a != null && a.areaMapClient == areaMapClient) return a;
      neighboursClient[n] = a = areaMapClient.getArea(areaX, areaZ);
      if (a != null && a.areaMapClient == areaMapClient) return a;
    } else if (side == Side.Server) {
      if (areaMapServer == null) return null;
      int aX = areaX - this.areaX;
      int aZ = areaZ - this.areaZ;
      if (aX < -1 || aX > 1 || aZ < -1 || aZ > 1) return areaMapServer.getArea(areaX, areaZ);
      int n = (aX + 1) + ((aZ + 1) * 3);
      Area a = neighboursServer[n];
      if (a != null && a.areaMapServer == areaMapServer) return a;
      neighboursServer[n] = a = areaMapServer.getArea(areaX, areaZ);
      if (a != null && a.areaMapServer == areaMapServer) return a;
    }
    return null;
  }
  
  public AreaMap areaMap() {
    Side side = Side.getSide();
    if (side == Side.Client) return areaMapClient;
    else if (side == Side.Server) return areaMapServer;
    return null;
  }
  
  protected void setAreaMap(AreaMap areaMap) {
    Side side = Side.getSide();
    if (side == Side.Client) areaMapClient = areaMap;
    else if (side == Side.Server) areaMapServer = areaMap;
  }
  
  public void tick() {
    if (Side.isServer()) {
      AreaMap areaMap = areaMap();
      if (!featuresGenerated() || areaMap == null) return;
      ThreadRandom random = ThreadRandom.get();
      lock.writeLock();
      int updates = NUM_RANDOM_UPDATES * height;
      for (int i = 0; i < updates; i++) {
        int randomX = random.nextInt(SIZE_BLOCKS);
        int randomZ = random.nextInt(SIZE_BLOCKS);
        int randomY = random.nextInt(maxY + 1);
        randomTick(randomX, randomY, randomZ, areaMap);
      }
      for (BlockData blockData : blockDataList) {
        blockData.update();
      }
      lock.writeUnlock();
    }
  }

  private void randomTick(int x, int y, int z, AreaMap areaMap) {
    int b = blocks[getRef(x, y, z)];
    int blockID = b & 0xFFFFF;
    int blockMeta = (b >> 20) & 0xFF;
    Block block = IDManager.toBlock(blockID);
    if (block == null) return;
    int newMeta = block.randomTick(areaMap.world, this, x, y, z, blockMeta);
    if (newMeta != blockMeta) setBlock(block, x, y, z, newMeta);
  }

  public void doUpdatesThisArea(int x, int y, int z, int ref) {
    updateSurrounding(x, y, z, ref);

    boolean updateRender = Side.isClient() || shared;
    int section = y / SIZE_BLOCKS;

    if (updateRender) {
      updateRender(section);
      if (y % SIZE_BLOCKS == 0) updateRender(section - 1);
      if (y % SIZE_BLOCKS == SIZE_BLOCKS - 1) updateRender(section + 1);
    }
  }

  public void doUpdatesOtherAreas(int x, int y, int z, int ref) {
    boolean updateRender = Side.isClient() || shared;
    int section = y / SIZE_BLOCKS;

    AreaMap areaMap = areaMap();
    if (areaMap != null && (x == 0 || x == SIZE_BLOCKS - 1 || z == 0 || z == SIZE_BLOCKS - 1)) {
      Area area;
      areaMap.lock.readLock();
      if (x == 0) {
        area = neighbour(areaX - 1, areaZ);
        if (area != null) {
          Lock.waitToLock(true, area);
          if (area.isReady()) area.update(SIZE_BLOCKS - 1, y, z, getRef(SIZE_BLOCKS - 1, y, z));
          if (updateRender) area.updateRender(section);
          area.lock.writeUnlock();
        }
      } else if (x == SIZE_BLOCKS - 1) {
        area = neighbour(areaX + 1, areaZ);
        if (area != null) {
          Lock.waitToLock(true, area);
          if (area.isReady()) area.update(0, y, z, getRef(0, y, z));
          if (updateRender) area.updateRender(section);
          area.lock.writeUnlock();
        }
      }
      if (z == 0) {
        area = neighbour(areaX, areaZ - 1);
        if (area != null) {
          Lock.waitToLock(true, area);
          if (area.isReady()) area.update(x, y, SIZE_BLOCKS - 1, getRef(x, y, SIZE_BLOCKS - 1));
          if (updateRender) area.updateRender(section);
          area.lock.writeUnlock();
        }
      } else if (z == SIZE_BLOCKS - 1) {
        area = neighbour(areaX, areaZ + 1);
        if (area != null) {
          Lock.waitToLock(true, area);
          if (area.isReady()) area.update(x, y, 0, getRef(x, y, 0));
          if (updateRender) area.updateRender(section);
          area.lock.writeUnlock();
        }
      }
      areaMap.lock.readUnlock();
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
    if (section >= 0 && section < renderStatus.length) {
      renderStatus[section] = AreaRenderStatus.UNKNOWN;
      if (areaRenderer != null && areaRenderer[section] != null) areaRenderer[section].refresh = true;
    }
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

  // initial update and build of heightmap
  public void initialUpdate() {
    lock.writeLock();
    if (unreadyWriteLock()) return;

    // neg x side
    for (int z = 0; z < SIZE_BLOCKS; z++) {
      int height = -1;
      for (int y = 0; y <= maxY; y++) {
        int ref = (z * SIZE_BLOCKS) + (y * SIZE_BLOCKS_SQUARED);
        if (TransparencyManager.isTransparent(blocks[ref])) {
          if (blocks[ref + MAX_X_OFFSET] != 0) blocks[ref + MAX_X_OFFSET] |= BLOCK_VISIBLE;
        } else {
          blocks[ref] |= BLOCK_VISIBLE;
          height = y;
        }
      }
      heightmap[z * SIZE_BLOCKS] = height;
    }
    // pos x side
    for (int z = 0; z < SIZE_BLOCKS; z++) {
      int height = -1;
      for (int y = 0; y <= maxY; y++) {
        int ref = (SIZE_BLOCKS - 1) + (z * SIZE_BLOCKS) + (y * SIZE_BLOCKS_SQUARED);
        if (TransparencyManager.isTransparent(blocks[ref])) {
          if (blocks[ref + MIN_X_OFFSET] != 0) blocks[ref + MIN_X_OFFSET] |= BLOCK_VISIBLE;
        } else {
          blocks[ref] |= BLOCK_VISIBLE;
          height = y;
        }
      }
      heightmap[(SIZE_BLOCKS - 1) + (z * SIZE_BLOCKS)] = height;
    }
    // neg z side
    for (int x = 1; x < SIZE_BLOCKS - 1; x++) { //don't include x = 0 or x = SIZE_BLOCKS - 1
      int height = -1;
      for (int y = 0; y <= maxY; y++) {
        int ref = x + (y * SIZE_BLOCKS_SQUARED);
        if (TransparencyManager.isTransparent(blocks[ref])) {
          if (blocks[ref + MAX_Z_OFFSET] != 0) blocks[ref + MAX_Z_OFFSET] |= BLOCK_VISIBLE;
        } else {
          blocks[ref] |= BLOCK_VISIBLE;
          height = y;
        }
      }
      heightmap[x] = height;
    }
    // pos z side
    for (int x = 1; x < SIZE_BLOCKS - 1; x++) {
      int height = -1;
      for (int y = 0; y <= maxY; y++) {
        int ref = x + (SIZE_BLOCKS_SQUARED - SIZE_BLOCKS) + (y * SIZE_BLOCKS_SQUARED);
        if (TransparencyManager.isTransparent(blocks[ref])) {
          if (blocks[ref + MIN_Z_OFFSET] != 0) blocks[ref + MIN_Z_OFFSET] |= BLOCK_VISIBLE;
        } else {
          blocks[ref] |= BLOCK_VISIBLE;
          height = y;
        }
      }
      heightmap[x + (SIZE_BLOCKS_SQUARED - SIZE_BLOCKS)] = height;
    }
    // inner
    for (int x = 1; x < SIZE_BLOCKS - 1; x++) {
      for (int z = 1; z < SIZE_BLOCKS - 1; z++) {
        int height = -1;
        for (int y = 0; y <= maxY; y++) {
          int ref = x + z * SIZE_BLOCKS + y * SIZE_BLOCKS_SQUARED;
          if (TransparencyManager.isTransparent(blocks[ref])) {
            if (blocks[ref + MAX_X_OFFSET] != 0) blocks[ref + MAX_X_OFFSET] |= BLOCK_VISIBLE;
            if (blocks[ref + MIN_X_OFFSET] != 0) blocks[ref + MIN_X_OFFSET] |= BLOCK_VISIBLE;
            if (blocks[ref + MAX_Z_OFFSET] != 0) blocks[ref + MAX_Z_OFFSET] |= BLOCK_VISIBLE;
            if (blocks[ref + MIN_Z_OFFSET] != 0) blocks[ref + MIN_Z_OFFSET] |= BLOCK_VISIBLE;
            if (y < maxY && blocks[ref + MAX_Y_OFFSET] != 0) blocks[ref + MAX_Y_OFFSET] |= BLOCK_VISIBLE;
            if (y > 0 && blocks[ref + MIN_Y_OFFSET] != 0) blocks[ref + MIN_Y_OFFSET] |= BLOCK_VISIBLE;
          } else {
            height = y;
          }
        }
        heightmap[x + z * SIZE_BLOCKS] = height;
      }
    }
    // top and bottom
    for (int x = 0; x < SIZE_BLOCKS; x++) {
      for (int z = 0; z < SIZE_BLOCKS; z++) {
        int i = getRef(x, 0, z);
        if (blocks[i] != 0) blocks[i] |= BLOCK_VISIBLE;
        i = getRef(x, maxY, z);
        if (blocks[i] != 0) blocks[i] |= BLOCK_VISIBLE;
      }
    }

    modify();
    lock.writeUnlock();
  }

  public void setupArrays(int y) {
    lock.writeLock();

    if (unloaded) {
      lock.writeUnlock();
      throw new CubesException("Area has been unloaded");
    }
    if (isBlank()) {
      int h = (int) Math.ceil((y + 1) / (float) SIZE_BLOCKS);
      blocks = new int[SIZE_BLOCKS_CUBED * h];
      light = new byte[SIZE_BLOCKS_CUBED * h];
      AreaRenderer.free(areaRenderer);
      if (Side.isClient() || isShared()) {
        areaRenderer = new AreaRenderer[h];
        renderStatus = AreaRenderStatus.create(h);
      }
      height = h;
      maxY = (h * SIZE_BLOCKS) - 1;
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

  private void expand(int h) {
    lock.writeLock();

    if (unloaded) {
      lock.writeUnlock();
      throw new CubesException("Area has been unloaded");
    }
    if (isBlank() || h <= maxY || h > MAX_Y) {
      lock.writeUnlock();
      return;
    }
  
    int oldMaxY = maxY;
    int[] oldBlocks = blocks;
    byte[] oldLight = light;
    AreaRenderer[] oldAreaRenderer = areaRenderer;
    
    int newHeight = (int) Math.ceil((h + 1) / (float) SIZE_BLOCKS); //Round up to multiple of SIZE_BLOCKS
    
    int[] newBlocks = new int[SIZE_BLOCKS_CUBED * newHeight];
    System.arraycopy(oldBlocks, 0, newBlocks, 0, oldBlocks.length);
    
    byte[] newLight = new byte[SIZE_BLOCKS_CUBED * newHeight];
    System.arraycopy(oldLight, 0, newLight, 0, oldLight.length);
    if (featuresGenerated()) Arrays.fill(newLight, oldLight.length, newLight.length, (byte) SunLight.MAX_SUNLIGHT);
    
    
    if (Side.isClient() || shared) {
      this.areaRenderer = new AreaRenderer[newHeight];
      if (renderStatus.length < newHeight) renderStatus = AreaRenderStatus.create(newHeight);
    } else {
      this.areaRenderer = null;
    }
    
    this.blocks = newBlocks;
    this.light = newLight;
    this.height = newHeight;
    this.maxY = (newHeight * SIZE_BLOCKS) - 1;
    
    int i = oldMaxY * SIZE_BLOCKS_SQUARED;
    for (int z = 0; z < SIZE_BLOCKS; z++) { //update previous top
      for (int x = 0; x < SIZE_BLOCKS; x++, i++) {
        updateSurrounding(x, oldMaxY, z, i);
      }
    }

    lock.writeUnlock();
  
    AreaRenderer.free(oldAreaRenderer);
  }

  public boolean featuresGenerated() {
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
    if (Side.isClient() || isShared()) {
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

  @Override
  public int hashCode() {
    return hashCode;
  }
  
  public void writeNetworking(DataOutputStream dataOutputStream) throws IOException {
    write(dataOutputStream, false, true, false, null);
  }
  
  public void writeSave(DataOutputStream dataOutputStream, DataGroup[] entities) throws IOException {
    write(dataOutputStream, false, false, true, entities); //TODO resize when writing
  }

  private void write(DataOutputStream dataOutputStream, boolean resize, boolean writeCoordinates, boolean writeEntities, DataGroup[] entities) throws IOException {
    lock.readLock();
    if (writeCoordinates) {
      dataOutputStream.writeInt(areaX);
      dataOutputStream.writeInt(areaZ);
    }
    if (isBlank()) {
      dataOutputStream.writeInt(0);
      lock.readUnlock();
      return;
    }

    int usedHeight = resize ? usedHeight() : height;
    dataOutputStream.writeInt(featuresGenerated() ? usedHeight : -usedHeight);

    for (int i = 0; i < SIZE_BLOCKS_SQUARED; i++) {
      dataOutputStream.writeInt(heightmap[i]);
    }

    int currentBlock = -1, num = 0;
    for (int i = 0; i < (SIZE_BLOCKS_CUBED * usedHeight); i++) {
      int block = blocks[i]; // always positive
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
    
    if (writeEntities && entities != null) {
      dataOutputStream.writeShort(entities.length);
      dataOutputStream.writeShort(blockDataList.size());
  
      for (DataGroup entity : entities) {
        Data.output(entity, dataOutputStream);
      }
    } else {
      dataOutputStream.writeShort(0);
      dataOutputStream.writeShort(blockDataList.size());
    }
    
    for (BlockData blockData : blockDataList) {
      dataOutputStream.writeInt(getRef(blockData.getX(), blockData.getY(), blockData.getZ()));
      Data.output(blockData.write(), dataOutputStream);
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

  public void read(DataInputStream dataInputStream) throws IOException {
    int height = dataInputStream.readInt();
    if (height == 0) return;

    if (height > 0) { //if features
      features.set(Boolean.TRUE);
    } else {
      height = -height;
    }
    setupArrays((height * SIZE_BLOCKS) - 1);

    for (int i = 0; i < SIZE_BLOCKS_SQUARED; i++) {
      heightmap[i] = dataInputStream.readInt();
    }

    int counter = 0;
    boolean invalidBlocks = false;
    while (counter < (SIZE_BLOCKS_CUBED * height)) {
      int a = dataInputStream.readInt();
      if (a >= 0) {
        if (!IDManager.validBlock(a)) {
          invalidBlocks = true;
          a = 0;
        }
        blocks[counter++] = a;
      } else {
        int block = dataInputStream.readInt();
        if (!IDManager.validBlock(block)) {
          invalidBlocks = true;
          block = 0;
        }
        for (int i = 0; i < -a; i++) {
          blocks[counter++] = block;
        }
      }
    }

    for (int i = 0; i < light.length; i++) {
      light[i] = dataInputStream.readByte();
    }

    int entitiesSize = dataInputStream.readShort();
    int dataSize = dataInputStream.readShort();
    for (int i = 0; i < entitiesSize; i++) {
      DataGroup dataGroup = (DataGroup) Data.input(dataInputStream);
      Side.getCubes().world.addEntityFromSave(dataGroup);
    }
    saveEntities = entitiesSize;
    
    blockDataList.clear();
    blockDataList.ensureCapacity(dataSize);
    for (int i = 0; i < dataSize; i++) {
      int ref = dataInputStream.readInt();
      int x = getX(ref), y = getY(ref), z = getZ(ref);
      Block block = getBlock(x, y, z);
      if (block == null) continue;
      int meta = getMeta(x, y, z);
      DataGroup dataGroup = (DataGroup) Data.input(dataInputStream);
      BlockData data = block.createBlockData(this, x, y, z, meta, dataGroup);
      if (data == null) continue;
      data.read(dataGroup);
      blockDataList.add(data);
    }
    
    if (invalidBlocks) {
      Log.warning("Invalid blocks in " + toString());
      updateAll();
    }

    saveModCount();
  }

  public static Area readArea(DataInputStream dataInputStream) throws IOException {
    int areaX = dataInputStream.readInt();
    int areaZ = dataInputStream.readInt();
    Area area = new Area(areaX, areaZ);

    area.read(dataInputStream);
    return area;
  }

  public static int getRef(int x, int y, int z) {
    return x + z * SIZE_BLOCKS + y * SIZE_BLOCKS_SQUARED;
  }

  public static int getX(int ref) {
    return ref % SIZE_BLOCKS;
  }

  public static int getZ(int ref) {
    return (ref % SIZE_BLOCKS_SQUARED) / SIZE_BLOCKS;
  }

  public static int getY(int ref) {
    return ref / SIZE_BLOCKS_SQUARED;
  }

  public static int getHeightMapRef(int x, int z) {
    return x + z * SIZE_BLOCKS;
  }

  public static boolean isShared() {
    return NetworkingManager.isSingleplayer();
  }

  @Override
  public Lock getLock() {
    return lock;
  }
  
  @Override
  public String toString() {
    return areaX + "," + areaZ;
  }
  
  /**
   * Crucial to call this so changes are written to disk.
   * Area should be write locked
   */
  public void modify() {
    modCount++;
  }

  public boolean modifiedSinceSave(DataGroup[] entities) {
    return (entities != null && entities.length > 0) || saveEntities > 0 || saveModCount != modCount;
  }

  public void saveModCount() {
    lock.writeLock();
    saveModCount = modCount;
    lock.writeUnlock();
  }
}
