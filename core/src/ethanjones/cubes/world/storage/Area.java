package ethanjones.cubes.world.storage;

import ethanjones.data.DataGroup;
import ethanjones.data.DataList;
import ethanjones.data.basic.DataByte;
import ethanjones.data.basic.DataInteger;
import ethanjones.data.other.DataParser;
import java.util.ArrayList;

import ethanjones.cubes.block.Block;
import ethanjones.cubes.block.data.BlockData;
import ethanjones.cubes.core.event.world.block.BlockChangedEvent;
import ethanjones.cubes.core.system.CubesException;
import ethanjones.cubes.core.system.Pools;
import ethanjones.cubes.graphics.world.AreaRenderer;
import ethanjones.cubes.side.Sided;
import ethanjones.cubes.world.reference.BlockReference;

public class Area implements DataParser<DataGroup> {

  public static final int SIZE_BLOCKS = 16;
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
  private final boolean render;
  public boolean generated = false;
  public AreaRenderer areaRenderer;
  public final int[] blockFactories; //Always sync on this
  public final BlockData[] blockData;

  /**
   * In area coords
   */
  public Area(int x, int y, int z) {
    this(x, y, z, true);
  }

  public Area(int x, int y, int z, boolean render) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.render = render;
    maxBlockX = ((x + 1) * SIZE_BLOCKS) - 1;
    maxBlockY = ((y + 1) * SIZE_BLOCKS) - 1;
    maxBlockZ = ((z + 1) * SIZE_BLOCKS) - 1;
    minBlockX = x * SIZE_BLOCKS;
    minBlockY = y * SIZE_BLOCKS;
    minBlockZ = z * SIZE_BLOCKS;
    cenBlockX = (float) (maxBlockX + minBlockX) / 2f;
    cenBlockY = (float) (maxBlockY + minBlockY) / 2f;
    cenBlockZ = (float) (maxBlockZ + minBlockZ) / 2f;

    blockFactories = new int[SIZE_BLOCKS_CUBED];
    blockData = new BlockData[SIZE_BLOCKS_CUBED];
  }

  public Block getBlock(int x, int y, int z) {
    synchronized (this) {
      return Sided.getBlockManager().toBlock(blockFactories[getRef(x, y, z)]);
    }
  }

  public BlockData getBlockData(int x, int y, int z) {
    synchronized (this) {
      return blockData[getRef(x, y, z)];
    }
  }

  public int getRef(int x, int y, int z) {
    return (0 <= x && x < SIZE_BLOCKS ? x : x - minBlockX) + (0 <= z && z < SIZE_BLOCKS ? z : z - minBlockZ) * SIZE_BLOCKS + (0 <= y && y < SIZE_BLOCKS ? y : y - minBlockY) * SIZE_BLOCKS_SQUARED;
  }

  public void unload() {
    if (areaRenderer != null) Pools.free(AreaRenderer.class, areaRenderer);
    //blockFactories = null;
  }

  @Override
  public DataGroup write() {
    DataGroup dataGroup = new DataGroup();

    dataGroup.setInteger("x", x);
    dataGroup.setInteger("y", y);
    dataGroup.setInteger("z", z);
    dataGroup.setBoolean("generated", generated);

    DataGroup world = new DataGroup();
    DataList<DataGroup> blockDataList = new DataList<DataGroup>();
    world.setList("data", blockDataList);
    ArrayList<BlockReference> blocks = new ArrayList<BlockReference>();
    int i = 0;
    synchronized (this) {
      for (int y = 0; y < SIZE_BLOCKS; y++) {
        DataList<DataInteger> factories = new DataList<DataInteger>();
        DataList<DataGroup> partial = new DataList<DataGroup>();
        for (int z = 0; z < SIZE_BLOCKS; z++) {
          for (int x = 0; x < SIZE_BLOCKS; x++, i++) {
            int b = blockFactories[i];
            if (b != 0) {
              blocks.add(new BlockReference().setFromBlockCoordinates(x, y, z));
              if (blocks.size() < SIZE_BLOCKS_SQUARED / 8) {
                DataGroup d = new DataGroup();
                d.setByte("x", (byte) x);
                d.setByte("y", (byte) y);
                d.setByte("z", (byte) z);
                d.setInteger("b", b);
                partial.add(d);
              }
              BlockData data = blockData[i];
              if (data != null) {
                DataGroup d = new DataGroup();
                d.setByte("x", (byte) x);
                d.setByte("y", (byte) y);
                d.setByte("z", (byte) z);
                byte[] bytes = data.getData();
                DataList<DataByte> dataBytes = new DataList<DataByte>();
                for (int e = 0; e < bytes.length; e++) {
                  dataBytes.add(new DataByte(bytes[e]));
                }
                d.setList("d", dataBytes);
                blockDataList.add(d);
              }
            }
            factories.add(new DataInteger(b));
          }
        }
        if (blocks.size() == 0) { //Blank Y
          continue;
        }
        if (blocks.size() < SIZE_BLOCKS_SQUARED / 8) {
          DataGroup d = new DataGroup();
          d.setList("part", partial);
          world.setValue(y + "", d);
        } else {
          DataGroup d = new DataGroup();
          d.setList("factories", factories);
          world.setValue(y + "", d);
        }
      }
    }
    dataGroup.setGroup("world", world);
    return dataGroup;
  }

  @Override
  public void read(DataGroup data) {
    int aX = data.getInteger("x");
    int aY = data.getInteger("y");
    int aZ = data.getInteger("z");
    if (aX != x || aY != y || aZ != z) {
      throw new CubesException("Wrong coordinates, " + aX + " " + aY + " " + aZ + " expected " + x + " " + y + " " + z);
    }
    generated = data.getBoolean("generated");
    DataGroup world = data.getGroup("world");
    synchronized (this) {
      for (int y = 0; y < SIZE_BLOCKS; y++) {
        if (!world.contains(y + "")) {
          continue;
        }
        DataGroup d = world.getGroup(y + "");
        if (d.contains("part")) {
          DataList<DataGroup> partial = d.getList("part");
          for (DataGroup b : partial) {
            blockFactories[b.getByte("x") + (b.getByte("y") * SIZE_BLOCKS_SQUARED) + (b.getByte("z") * SIZE_BLOCKS)] = b.getInteger("b");
          }
        } else {
          DataList<DataInteger> list = d.getList("factories");
          int i = 0;
          int base = y * SIZE_BLOCKS_SQUARED;
          for (int z = 0; z < SIZE_BLOCKS; z++) {
            for (int x = 0; x < SIZE_BLOCKS; x++, i++) {
              blockFactories[base + i] = list.get(i).get();
            }
          }
        }
      }
      DataList<DataGroup> list = world.getList("data");
      for (DataGroup dataGroup : list) {
        int x = dataGroup.getByte("x");
        int y = dataGroup.getByte("y");
        int z = dataGroup.getByte("z");
        int ref = getRef(x, y, z);
        DataList<DataByte> dataBytes = dataGroup.getList("d");
        byte[] bytes = new byte[dataBytes.size()];
        for (int d = 0; d < bytes.length; d++) {
          bytes[d] = dataBytes.get(d).get();
        }
        blockData[ref] = Sided.getBlockManager().toBlock(blockFactories[ref]).getBlockData();
        blockData[ref].setData(bytes);
      }
    }
    if (areaRenderer != null) areaRenderer.refresh = true;
  }

  public void setBlock(Block block, int x, int y, int z) {
    int ref = getRef(x, y, z);
    int b;
    synchronized (this) {
      b = blockFactories[ref];
      blockFactories[ref] = Sided.getBlockManager().toInt(block);
    }
    blockData[ref] = block == null ? null : block.getBlockData();
    if (areaRenderer != null) areaRenderer.refresh = true;
    new BlockChangedEvent(new BlockReference().setFromBlockCoordinates(x, y, z), Sided.getBlockManager().toBlock(b)).post();
  }
}
