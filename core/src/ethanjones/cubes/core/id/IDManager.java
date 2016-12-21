package ethanjones.cubes.core.id;

import ethanjones.cubes.block.Block;
import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.mod.ModInstance;
import ethanjones.cubes.core.mod.ModManager;
import ethanjones.cubes.core.system.CubesException;
import ethanjones.cubes.core.system.Debug;
import ethanjones.cubes.item.Item;
import ethanjones.cubes.item.ItemBlock;
import ethanjones.data.DataGroup;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;

public class IDManager {

  public static final int MAX_BLOCK_ID = 1048575;

  private static List<Block> blockList = new ArrayList<Block>();
  private static Map<String, Block> idToBlock = new HashMap<String, Block>();
  private static List<ItemBlock> itemBlockList = new ArrayList<ItemBlock>();
  private static List<Item> itemList = new ArrayList<Item>();
  private static Map<String, Item> idToItem = new HashMap<String, Item>();
  private static Map<String, ModInstance> idToMod = new HashMap<String, ModInstance>();
  private static AtomicBoolean loaded = new AtomicBoolean(false);

  public static void register(Block block) {
    if (block == null) return;
    checkID(block.id);
    blockList.add(block);
    idToBlock.put(block.id, block);
    ItemBlock itemBlock = block.getItemBlock();
    itemBlockList.add(itemBlock);
    idToItem.put(itemBlock.id, itemBlock);
    idToMod.put(block.id, ModManager.getCurrentMod());
    if (!itemBlock.id.equals(block.id)) throw new IllegalArgumentException(itemBlock.id);
  }

  public static void register(Item item) {
    if (item == null) return;
    checkID(item.id);
    itemList.add(item);
    idToItem.put(item.id, item);
    idToMod.put(item.id, ModManager.getCurrentMod());
  }

  private static void checkID(String id) {
    if (!id.contains(":")) throw new IllegalArgumentException(id + " is not in the correct format");
    String i = id.substring(0, id.indexOf(":")).toLowerCase();
    String m = ModManager.getCurrentModName().toLowerCase();
    if (!i.equals(m)) throw new IllegalArgumentException("\"" + m + "\" cannot register id \"" + id + "\"");
  }

  public static Block toBlock(String id) {
    if (id == null || id.isEmpty()) return null;
    return idToBlock.get(id);
  }

  public static Item toItem(String id) {
    if (id == null || id.isEmpty()) return null;
    return idToItem.get(id);
  }

  public static ModInstance getMod(String id) {
    if (id == null || id.isEmpty()) return null;
    return idToMod.get(id);
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
    if (!loaded.compareAndSet(false, true)) return;

    idToBlock = Collections.unmodifiableMap(idToBlock);
    idToItem = Collections.unmodifiableMap(idToItem);
    idToMod = Collections.unmodifiableMap(idToMod);
    blockList = Collections.unmodifiableList(blockList);
    itemBlockList = Collections.unmodifiableList(itemBlockList);
    itemList = Collections.unmodifiableList(itemList);

    Log.debug("IDs:");
    for (Map.Entry<String, ModInstance> entry : idToMod.entrySet()) {
      ModInstance m = entry.getValue();
      Log.debug(String.format("%1$-" + 32 + "s", entry.getKey()) + "- " + (m != null ? m.getName() : "core"));
    }
  }

  public static boolean isLoaded() {
    return loaded.get();
  }
  
  // Mappings
  
  private static final Map<Block, Integer> blockToInteger = new HashMap<Block, Integer>();
  private static final Map<Item, Integer> itemToInteger = new HashMap<Item, Integer>();
  protected static Block[] integerToBlock;
  private static Item[] integerToItem;
  private static final AtomicBoolean setup = new AtomicBoolean(false);
  private static int nextFree = 0;
  
  public static void resetMapping() {
    if (!setup.compareAndSet(true, false)) return;
    Log.debug("Resetting ID Manager");
    blockToInteger.clear();
    itemToInteger.clear();
    integerToBlock = new Block[0];
    integerToItem = new Item[0];
    nextFree = 0;
  }

  public static void generateDefaultMappings() {
    if (!setup.compareAndSet(false, true)) return;
    Log.debug("Generating default ID manager");
    int i = 1;
    for (Block block : blockList) {
      if (i > MAX_BLOCK_ID) {
        Debug.crash(new CubesException("No more block ids"));
      }

      ItemBlock itemBlock = block.getItemBlock();
      blockToInteger.put(block, i);
      itemToInteger.put(itemBlock, i);
      i++;
    }
    for (Item item : itemList) {
      if (i < 0) {
        Debug.crash(new CubesException("No more item ids"));
      }

      itemToInteger.put(item, i);
      i++;
    }
    nextFree = i;
  
    finishMappingSetup();
  }
  
  public static void readMapping(DataGroup data) {
    if (!setup.compareAndSet(false, true)) return;
    Log.debug("Reading ID manager");
    HashMap<String, Block> blockMapCopy = new HashMap<String, Block>();
    HashMap<String, Item> itemMapCopy = new HashMap<String, Item>();
    blockMapCopy.putAll(idToBlock);
    itemMapCopy.putAll(idToItem);
    
    nextFree = data.getInteger("next");
    
    DataGroup blocks = data.getGroup("blocks");
    for (Map.Entry<String, Object> entry : blocks.entrySet()) {
      Block block = blockMapCopy.remove(entry.getKey());
      if (block == null) {
        Log.error("No such block: " + entry.getKey());
        continue;
      }
      int i = (Integer) entry.getValue();
      blockToInteger.put(block, i);
    }
    
    for (Block block : blockMapCopy.values()) {
      if (nextFree > MAX_BLOCK_ID) {
        Debug.crash(new CubesException("No more block ids"));
      }
      Log.debug("Adding block " + block.id + " " + nextFree);
      ItemBlock itemBlock = block.getItemBlock();
      blockToInteger.put(block, nextFree);
      itemToInteger.put(itemBlock, nextFree);
      nextFree++;
    }
    
    DataGroup items = data.getGroup("items");
    for (Map.Entry<String, Object> entry : items.entrySet()) {
      Item item = itemMapCopy.remove(entry.getKey());
      if (item == null) {
        Log.error("No such item: " + entry.getKey());
        continue;
      }
      int i = (Integer) entry.getValue();
      itemToInteger.put(item, i);
    }
    
    for (Item item : itemMapCopy.values()) {
      if (nextFree < 0) {
        Debug.crash(new CubesException("No more items ids"));
      } else if (item instanceof ItemBlock) {
        continue;
      }
      Log.debug("Adding item " + item.id + " " + nextFree);
      itemToInteger.put(item, nextFree);
      nextFree++;
    }
    
    finishMappingSetup();
  }
  
  public static DataGroup writeMapping() {
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
  
  
  private static void finishMappingSetup() {
    int maxBlock = 0;
    for (Entry<Block, Integer> entry : blockToInteger.entrySet()) {
      entry.getKey().intID = entry.getValue();
      if (entry.getValue() > maxBlock) maxBlock = entry.getValue();
    }
    integerToBlock = new Block[maxBlock + 1];
    for (Entry<Block, Integer> entry : blockToInteger.entrySet()) {
      integerToBlock[entry.getValue()] = entry.getKey();
    }
    
    int maxItem = 0;
    for (Entry<Item, Integer> entry : itemToInteger.entrySet()) {
      entry.getKey().intID = entry.getValue();
      if (entry.getValue() > maxItem) maxItem = entry.getValue();
    }
    integerToItem = new Item[maxItem + 1];
    for (Entry<Item, Integer> entry : itemToInteger.entrySet()) {
      integerToItem[entry.getValue()] = entry.getKey();
    }
    
    TransparencyManager.setup();
  }

  public static int toInt(Block block) {
    if (block == null) return 0;
    return blockToInteger.get(block);
  }

  public static int toInt(Item item) {
    if (item == null) return 0;
    return itemToInteger.get(item);
  }

  public static Block toBlock(int i) {
    if (i <= 0 || i >= integerToBlock.length) return null;
    return integerToBlock[i];
  }
  
  public static boolean validBlock(int i) {
    i &= 0xFFFFF;
    return (i == 0) || (i > 0 && i < integerToBlock.length && integerToBlock[i] != null);
  }

  public static Item toItem(int i) {
    if (i <= 0 || i >= integerToItem.length) return null;
    return integerToItem[i];
  }
  
  private IDManager() {
    
  }
  
}
