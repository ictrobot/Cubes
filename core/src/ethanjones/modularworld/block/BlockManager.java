package ethanjones.modularworld.block;

import ethanjones.data.Data;
import ethanjones.data.DataGroup;
import ethanjones.data.basic.DataString;
import ethanjones.data.other.DataParser;

import java.util.HashMap;
import java.util.Map;

public class BlockManager implements DataParser<DataGroup> {

  volatile HashMap<Integer, Block> ids;
  volatile HashMap<Block, Integer> blocks;
  volatile HashMap<Class<? extends Block>, Block> classes;
  volatile int unused = 1;

  public BlockManager() {
    ids = new HashMap<Integer, Block>();
    blocks = new HashMap<Block, Integer>();
    classes = new HashMap<Class<? extends Block>, Block>();
  }

  public int toInt(Block block) {
    if (block == null) return 0;
    synchronized (this) {
      return blocks.get(block);
    }
  }

  public Block toFactory(int i) {
    if (i == 0) return null;
    synchronized (this) {
      return ids.get(i);
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
      ids.clear();
      blocks.clear();
      for (Map.Entry<String, Data> entry : data.getEntrySet()) {
        int i = Integer.parseInt(entry.getKey());
        Class<? extends Block> c;
        try {
          c = Class.forName(((DataString) entry.getValue()).get()).asSubclass(Block.class);
        } catch (ClassNotFoundException e) {
          continue;
        }
        if (c == null) continue;
        Block block = classes.get(c);
        ids.put(i, block);
        blocks.put(block, i);
      }
    }
  }

  public void register(Block block) {
    synchronized (this) {
      int i = findFree();
      ids.put(i, block);
      blocks.put(block, i);
      classes.put(block.getClass(), block);
    }
  }

  private int findFree() {
    int i = -1;
    if (ids.get(unused) == null) {
      i = unused;
    }
    unused++;
    return i != -1 ? i : findFree();
  }
}
