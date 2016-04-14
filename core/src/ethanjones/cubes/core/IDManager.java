package ethanjones.cubes.core;

import ethanjones.cubes.block.Block;
import ethanjones.cubes.block.Blocks;
import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.system.CubesException;
import ethanjones.cubes.item.Item;
import ethanjones.cubes.item.ItemBlock;
import ethanjones.data.DataGroup;
import ethanjones.data.DataParser;

import java.util.*;

public class IDManager implements DataParser {

  private static List<Block> blockList = new ArrayList<Block>();
  private static Map<String, Block> idToBlock = new HashMap<String, Block>();
  private static List<ItemBlock> itemBlockList = new ArrayList<ItemBlock>();
  private static List<Item> itemList = new ArrayList<Item>();
  private static Map<String, Item> idToItem = new HashMap<String, Item>();

  public static void register(Block block) {
    if (block == null) return;
    blockList.add(block);
    idToBlock.put(block.id, block);
    ItemBlock itemBlock = block.getItemBlock();
    itemBlockList.add(itemBlock);
    idToItem.put(itemBlock.id, itemBlock);
    if (!itemBlock.id.equals(block.id)) throw new IllegalArgumentException(itemBlock.id);
  }

  public static Block toBlock(String id) {
    if (id == null || id.isEmpty()) return null;
    return idToBlock.get(id);
  }

  public static Item toItem(String id) {
    if (id == null || id.isEmpty()) return null;
    return idToItem.get(id);
  }

  public static List<Block> getBlocks() {
    return blockList;
  }

  public static List<ItemBlock> getItemBlocks() {
    return itemBlockList;
  }

  public static List<Item> getItems() {
    return itemList;
  }

  public static void loaded() {
    idToBlock = Collections.unmodifiableMap(idToBlock);
    idToItem = Collections.unmodifiableMap(idToItem);
    blockList = Collections.unmodifiableList(blockList);
    itemBlockList = Collections.unmodifiableList(itemBlockList);
    itemList = Collections.unmodifiableList(itemList);
  }

  private Map<Integer, Block> integerToBlock;
  private Map<Block, Integer> blockToInteger;
  private Map<Integer, Item> integerToItem;
  private Map<Item, Integer> itemToInteger;
  public TransparencyManager transparencyManager;
  private int nextFree;

  public IDManager() {
    integerToBlock = new HashMap<Integer, Block>();
    blockToInteger = new HashMap<Block, Integer>();
    integerToItem = new HashMap<Integer, Item>();
    itemToInteger = new HashMap<Item, Integer>();
    transparencyManager = new TransparencyManager();
  }

  public void generateDefault() {
    if (integerToBlock.size() > 0) return;

    int i = 1;
    for (Block block : blockList) {
      ItemBlock itemBlock = block.getItemBlock();
      integerToBlock.put(i, block);
      blockToInteger.put(block, i);
      integerToItem.put(i, itemBlock);
      itemToInteger.put(itemBlock, i);
      i++;
    }
    for (Item item : itemList) {
      integerToItem.put(i, item);
      itemToInteger.put(item, i);
      i++;
    }
    this.nextFree = i;

    unmodifiable();
  }

  public int toInt(Block block) {
    if (block == null) return 0;
    return blockToInteger.get(block);
  }

  public int toInt(Item item) {
    if (item == null) return 0;
    return itemToInteger.get(item);
  }

  public Block toBlock(int i) {
    if (i == 0) return null;
    return integerToBlock.get(i);
  }

  public Item toItem(int i) {
    if (i == 0) return null;
    return integerToItem.get(i);
  }

  @Override
  public DataGroup write() {
    DataGroup blocks = new DataGroup();
    for (Map.Entry<Block, Integer> entry : blockToInteger.entrySet()) {
      blocks.put(entry.getKey().id, entry.getValue());
    }
    DataGroup items = new DataGroup();
    for (Map.Entry<Item, Integer> entry : itemToInteger.entrySet()) {
      items.put(entry.getKey().id, entry.getValue());
    }
    DataGroup dataGroup = new DataGroup();
    dataGroup.put("blocks", blocks);
    dataGroup.put("items", items);
    dataGroup.put("next", nextFree);
    return dataGroup;
  }

  @Override
  public void read(DataGroup data) {
    if (integerToBlock.size() > 0) return;

    DataGroup blocks = data.getGroup("blocks");
    for (Map.Entry<String, Object> entry : blocks.entrySet()) {
      Block block = idToBlock.get(entry.getKey());
      if (block == null) throw new CubesException("No such block: " + entry.getKey());
      int i = (Integer) entry.getValue();
      integerToBlock.put(i, block);
      blockToInteger.put(block, i);
    }

    DataGroup items = data.getGroup("items");
    for (Map.Entry<String, Object> entry : items.entrySet()) {
      Item item = idToItem.get(entry.getKey());
      if (item == null) throw new CubesException("No such item: " + entry.getKey());
      int i = (Integer) entry.getValue();
      integerToItem.put(i, item);
      itemToInteger.put(item, i);
    }

    nextFree = data.getInteger("next");

    unmodifiable();
  }

  private void unmodifiable() {
    integerToBlock = Collections.unmodifiableMap(integerToBlock);
    blockToInteger = Collections.unmodifiableMap(blockToInteger);
    integerToItem = Collections.unmodifiableMap(integerToItem);
    itemToInteger = Collections.unmodifiableMap(itemToInteger);
    transparencyManager.setup(this);
  }

  public static class TransparencyManager {
    private BitSet bitSet;
    private boolean setup = false;

    public TransparencyManager() {
      bitSet = new BitSet(getBlocks().size());
    }

    public void setup(IDManager idManager) {
      if (setup) return;
      for (Map.Entry<Integer, Block> entry : idManager.integerToBlock.entrySet()) {
        bitSet.set(entry.getKey(), entry.getValue().isTransparent());
      }
      setup = true;
    }

    public boolean isTransparent(int block) {
      return block == 0 || (block < 0 ? bitSet.get(-block) : bitSet.get(block));
    }

    public boolean isTransparent(Block block) {
      return block == null || block.isTransparent();
    }
  }
}
