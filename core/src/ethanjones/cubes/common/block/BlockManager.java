package ethanjones.cubes.common.block;

import ethanjones.data.Data;
import ethanjones.data.DataGroup;
import ethanjones.data.basic.DataInteger;
import ethanjones.data.other.DataParser;
import java.util.*;

import ethanjones.cubes.common.logging.Log;

public class BlockManager implements DataParser<DataGroup> {

  private static final ArrayList<Block> blockList = new ArrayList<Block>();
  private static final List<Block> unmodifiableBlockList = Collections.unmodifiableList(blockList);
  private static final HashMap<String, Block> idToBlock = new HashMap<String, Block>();
  private static volatile boolean canRegister = false;

  public static void register(Block block) {
    if (block == null) return;
    if (!canRegister) {
      Log.error("Tried to register \"" + block.id + "\" after postInit");
      return;
    }

    synchronized (BlockManager.class) {
      blockList.add(block);
      idToBlock.put(block.id, block);
    }
  }

  public static Block toBlock(String id) {
    if (id == null || id.isEmpty()) return null;
    synchronized (BlockManager.class) {
      return idToBlock.get(id);
    }
  }

  public static List<Block> getBlocks() {
    return unmodifiableBlockList;
  }

  public static void preInit() {
    if (blockList.size() == 0) canRegister = true;
  }

  public static void postInit() {
    canRegister = false;
  }

  private volatile HashMap<Integer, Block> integerToBlock;
  private volatile HashMap<Block, Integer> blockToInteger;

  public BlockManager() {
    integerToBlock = new HashMap<Integer, Block>();
    blockToInteger = new HashMap<Block, Integer>();
  }

  public void generateDefault() {
    if (integerToBlock.size() > 0) return;
    int i = 1;
    for (Block block : blockList) {
      integerToBlock.put(i, block);
      blockToInteger.put(block, i);
      i++;
    }
  }

  public int toInt(Block block) {
    if (block == null) return 0;
    synchronized (this) {
      return blockToInteger.get(block);
    }
  }

  public Block toBlock(int i) {
    if (i == 0) return null;
    synchronized (this) {
      return integerToBlock.get(i);
    }
  }

  @Override
  public DataGroup write() {
    synchronized (this) {
      DataGroup dataGroup = new DataGroup();
      for (Map.Entry<Block, Integer> entry : blockToInteger.entrySet()) {
        dataGroup.setInteger(entry.getKey().id, entry.getValue());
      }
      return dataGroup;
    }
  }

  @Override
  public void read(DataGroup data) {
    if (integerToBlock.size() > 0) return;
    synchronized (this) {
      synchronized (BlockManager.class) {
        for (Map.Entry<String, Data> entry : data.getEntrySet()) {
          Block block = idToBlock.get(entry.getKey());
          int i = ((DataInteger) entry.getValue()).get();
          integerToBlock.put(i, block);
          blockToInteger.put(block, i);
        }
      }
    }
  }
}
