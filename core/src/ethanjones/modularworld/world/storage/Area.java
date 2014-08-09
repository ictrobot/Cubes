package ethanjones.modularworld.world.storage;

import ethanjones.modularworld.block.factory.BlockFactory;
import ethanjones.modularworld.core.ModularWorldException;
import ethanjones.modularworld.core.data.DataGroup;
import ethanjones.modularworld.core.data.DataList;
import ethanjones.modularworld.core.data.basic.DataInteger;
import ethanjones.modularworld.core.data.other.DataParser;
import ethanjones.modularworld.core.events.world.block.SetBlockEvent;
import ethanjones.modularworld.graphics.world.AreaRenderer;
import ethanjones.modularworld.side.common.ModularWorld;
import ethanjones.modularworld.world.coordinates.BlockCoordinates;

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

  public boolean generated = false;
  public AreaRenderer areaRenderer;
  public int[] blockFactories;

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
    if (!ModularWorld.compatibility.isHeadless() && render) {
      areaRenderer = new AreaRenderer(this);
    }
  }

  public BlockFactory getBlockFactory(int x, int y, int z) {
    return ModularWorld.blockManager.toFactory(blockFactories[Math.abs(x % SIZE_BLOCKS) + Math.abs(z % SIZE_BLOCKS) * SIZE_BLOCKS + Math.abs(y % SIZE_BLOCKS) * SIZE_BLOCKS_SQUARED]);
  }

  public void setBlockFactory(BlockFactory blockFactory, int x, int y, int z) {
    if (new SetBlockEvent(new BlockCoordinates(x, y, z), blockFactory).post()) {
      if (areaRenderer != null) areaRenderer.dirty = true;
      blockFactories[Math.abs(x % SIZE_BLOCKS) + Math.abs(z % SIZE_BLOCKS) * SIZE_BLOCKS + Math.abs(y % SIZE_BLOCKS) * SIZE_BLOCKS_SQUARED] = ModularWorld.blockManager.toInt(blockFactory);
    }
  }

  public void unload() {
    areaRenderer.dispose();
    blockFactories = null;
  }

  @Override
  public DataGroup write() {
    DataGroup dataGroup = new DataGroup();
    dataGroup.setInteger("x", x);
    dataGroup.setInteger("y", y);
    dataGroup.setInteger("z", z);
    DataList<DataInteger> block = new DataList<DataInteger>();
    for (int i = 0; i < blockFactories.length; i++) {
      block.add(new DataInteger(blockFactories[i]));
    }
    dataGroup.setList("blockFactories", block);
    return dataGroup;
  }

  @Override
  public void read(DataGroup data) {
    int aX = data.getInteger("x");
    int aY = data.getInteger("y");
    int aZ = data.getInteger("z");
    if (aX != x || aY != y || aZ != z)
      throw new ModularWorldException("Wrong coordinates, " + aX + " " + aY + " " + aZ + " expected " + x + " " + y + " " + z);
    DataList<DataInteger> block = data.getDataList("blockFactories");
    for (int i = 0; i < blockFactories.length; i++) {
      blockFactories[i] = block.get(i).get();
    }
    if (areaRenderer != null) areaRenderer.dirty = true;
  }
}
