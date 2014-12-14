package ethanjones.cubes.block;

import ethanjones.data.Data;
import ethanjones.data.DataGroup;
import ethanjones.data.basic.DataString;
import ethanjones.data.other.DataParser;
import java.util.*;

import ethanjones.cubes.core.logging.Log;

public class BlockManager implements DataParser<DataGroup> {

  volatile HashMap<Integer, Block> ints;
  volatile HashMap<Block, Integer> blocks;
  volatile HashMap<String, Block> ids;
  volatile HashMap<Class<? extends Block>, Block> classes;
  volatile ArrayList<Block> blockList;
  volatile List<Block> unmodifiableBlockList;
  volatile int unused = 1;

  public BlockManager() {
    ints = new HashMap<Integer, Block>();
    blocks = new HashMap<Block, Integer>();
    ids = new HashMap<String, Block>();
    classes = new HashMap<Class<? extends Block>, Block>();
    blockList = new ArrayList<Block>();
    unmodifiableBlockList = Collections.unmodifiableList(blockList);
  }

  public int toInt(Block block) {
    if (block == null) return 0;
    synchronized (this) {
      return blocks.get(block);
    }
  }

  public Block toBlock(int i) {
    if (i == 0) return null;
    synchronized (this) {
      return ints.get(i);
    }
  }

  public Block toBlock(String id) {
    if (id == null || id.isEmpty()) return null;
    synchronized (this) {
      return ids.get(id);
    }
  }

  @Override
  public DataGroup write() {
    synchronized (this) {
      DataGroup dataGroup = new DataGroup();
      for (Map.Entry<Block, Integer> entry : blocks.entrySet()) {
        dataGroup.setString(entry.getValue().toString(), entry.getKey().getClass().getName());
      }
      return dataGroup;
    }
  }

  @Override
  public void read(DataGroup data) {
    synchronized (this) {
      ints.clear();
      blocks.clear();
      for (Map.Entry<String, Data> entry : data.getEntrySet()) {
        int i = Integer.parseInt(entry.getKey());
        Class<? extends Block> c;
        try {
          c = Class.forName(((DataString) entry.getValue()).get()).asSubclass(Block.class);
        } catch (Exception e) {
          Log.error("Failed to read block", e);
          continue;
        }
        Block block = classes.get(c);
        ints.put(i, block);
        blocks.put(block, i);
        ids.put(block.id, block);
      }
    }
  }

  public void register(Block block) {
    synchronized (this) {
      int i = findFree();
      ints.put(i, block);
      blocks.put(block, i);
      ids.put(block.id, block);
      classes.put(block.getClass(), block);
      blockList.add(block);
    }
  }

  public List<Block> getBlocks() {
    return unmodifiableBlockList;
  }

  private int findFree() {
    int i = -1;
    if (ints.get(unused) == null) {
      i = unused;
    }
    unused++;
    return i != -1 ? i : findFree();
  }
}
