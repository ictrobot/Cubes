package ethanjones.modularworld.block;

import ethanjones.modularworld.block.factory.BlockFactory;
import ethanjones.modularworld.core.data.Data;
import ethanjones.modularworld.core.data.DataGroup;
import ethanjones.modularworld.core.data.basic.DataString;
import ethanjones.modularworld.core.data.other.DataParser;

import java.util.HashMap;
import java.util.Map;

public class BlockManager implements DataParser<DataGroup> {

  HashMap<Integer, BlockFactory> ids;
  HashMap<BlockFactory, Integer> blockFactories;
  HashMap<Class<? extends BlockFactory>, BlockFactory> classes;
  int unused = 1;

  public BlockManager() { //TODO: Sync from server to client
    ids = new HashMap<Integer, BlockFactory>();
    blockFactories = new HashMap<BlockFactory, Integer>();
    classes = new HashMap<Class<? extends BlockFactory>, BlockFactory>();
  }

  public int toInt(BlockFactory blockFactory) {
    return blockFactories.get(blockFactory);
  }

  public BlockFactory toFactory(int i) {
    return ids.get(i);
  }

  @Override
  public DataGroup write() {
    DataGroup dataGroup = new DataGroup();
    for (Map.Entry<BlockFactory, Integer> entry : blockFactories.entrySet()) {
      dataGroup.setString(entry.getValue().toString(), entry.getKey().getClass().getName());
    }
    return dataGroup;
  }

  @Override
  public void read(DataGroup data) {
    ids.clear();
    blockFactories.clear();
    for (Map.Entry<String, Data> entry : data.getEntrySet()) {
      int i = Integer.parseInt(entry.getKey());
      Class<? extends BlockFactory> c;
      try {
        c = Class.forName(((DataString) entry.getValue()).get()).asSubclass(BlockFactory.class);
      } catch (ClassNotFoundException e) {
        continue;
      }
      if (c == null) continue;
      BlockFactory blockFactory = classes.get(c);
      ids.put(i, blockFactory);
      blockFactories.put(blockFactory, i);
    }
  }

  public void register(BlockFactory blockFactory) {
    int i = findFree();
    ids.put(i, blockFactory);
    blockFactories.put(blockFactory, i);
    classes.put(blockFactory.getClass(), blockFactory);
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
