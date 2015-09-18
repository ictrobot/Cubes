package ethanjones.cubes.block;

import ethanjones.cubes.core.system.CubesException;
import ethanjones.data.DataGroup;
import ethanjones.data.DataParser;
import java.util.*;

public class BlockManager implements DataParser {

  private static List<Block> blockList = new ArrayList<Block>();
  private static Map<String, Block> idToBlock = new HashMap<String, Block>();

  public static void register(Block block) {
    if (block == null) return;
    blockList.add(block);
    idToBlock.put(block.id, block);
  }

  public static Block toBlock(String id) {
    if (id == null || id.isEmpty()) return null;
    return idToBlock.get(id);
  }

  public static List<Block> getBlocks() {
    return blockList;
  }

  public static void blocksLoaded() {
    idToBlock = Collections.unmodifiableMap(idToBlock);
    blockList = Collections.unmodifiableList(blockList);
  }

  private Map<Integer, Block> integerToBlock;
  private Map<Block, Integer> blockToInteger;

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

    unmodifiable();
  }

  public int toInt(Block block) {
    if (block == null) return 0;
    return blockToInteger.get(block);
  }

  public Block toBlock(int i) {
    if (i == 0) return null;
    return integerToBlock.get(i);
  }

  @Override
  public DataGroup write() {
      DataGroup dataGroup = new DataGroup();
      for (Map.Entry<Block, Integer> entry : blockToInteger.entrySet()) {
        dataGroup.put(entry.getKey().id, entry.getValue());
      }
      return dataGroup;
  }

  @Override
  public void read(DataGroup data) {
    if (integerToBlock.size() > 0) return;

    for (Map.Entry<String, Object> entry : data.entrySet()) {
      Block block = idToBlock.get(entry.getKey());
      if (block == null) throw new CubesException("No such block: " + entry.getKey());
      int i = (int) entry.getValue();
      integerToBlock.put(i, block);
      blockToInteger.put(block, i);
    }

    unmodifiable();
  }

  private void unmodifiable() {
    integerToBlock = Collections.unmodifiableMap(integerToBlock);
    blockToInteger = Collections.unmodifiableMap(blockToInteger);
  }
}
