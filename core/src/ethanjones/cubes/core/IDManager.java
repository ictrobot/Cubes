package ethanjones.cubes.core;

import ethanjones.cubes.block.Block;
import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.lua.LuaMappingCubes;
import ethanjones.cubes.core.mod.ModInstance;
import ethanjones.cubes.core.mod.ModManager;
import ethanjones.cubes.core.system.CubesException;
import ethanjones.cubes.core.system.Debug;
import ethanjones.cubes.item.Item;
import ethanjones.cubes.item.ItemBlock;
import ethanjones.data.DataGroup;
import ethanjones.data.DataParser;

import org.luaj.vm2.LuaUserdata;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.*;
import java.util.Map.Entry;

public class IDManager implements DataParser {

  public static final int MAX_BLOCK_ID = 1048575;

  private static List<Block> blockList = new ArrayList<Block>();
  private static Map<String, Block> idToBlock = new HashMap<String, Block>();
  private static List<ItemBlock> itemBlockList = new ArrayList<ItemBlock>();
  private static List<Item> itemList = new ArrayList<Item>();
  private static Map<String, Item> idToItem = new HashMap<String, Item>();
  private static Map<String, ModInstance> idToMod = new HashMap<String, ModInstance>();

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

    for (Entry<String, Block> entry : idToBlock.entrySet()) {
      LuaMappingCubes.blocks.set(entry.getKey(), new LuaUserdata(entry.getValue()));
    }
    for (Entry<String, Item> entry : idToItem.entrySet()) {
      LuaMappingCubes.items.set(entry.getKey(), new LuaUserdata(entry.getValue()));
    }
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
      if (i > MAX_BLOCK_ID) {
        Debug.crash(new CubesException("No more block ids"));
      }

      ItemBlock itemBlock = block.getItemBlock();
      integerToBlock.put(i, block);
      blockToInteger.put(block, i);
      integerToItem.put(i, itemBlock);
      itemToInteger.put(itemBlock, i);
      i++;
    }
    for (Item item : itemList) {
      if (i < 0) {
        Debug.crash(new CubesException("No more item ids"));
      }

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
      block &= 0xFFFFF;
      return block == 0 || (block < 0 ? bitSet.get(-block) : bitSet.get(block));
    }

    public boolean isTransparent(Block block) {
      return block == null || block.isTransparent();
    }
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  public @interface GetBlock {
    public String value();
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  public @interface GetItem {
    public String value();
  }

  public static void getInstances(Class<?> c) {
    for (java.lang.reflect.Field field : c.getDeclaredFields()) {
      if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
        if (field.isAnnotationPresent(GetBlock.class)) {
          GetBlock g = field.getAnnotation(GetBlock.class);
          Block block = IDManager.idToBlock.get(g.value());
          try {
            field.set(null, block);
          } catch (IllegalAccessException e) {
            throw new CubesException("Failed to getInstances()", e);
          }
        } else if (field.isAnnotationPresent(GetItem.class)) {
          GetItem g = field.getAnnotation(GetItem.class);
          Item item = IDManager.idToItem.get(g.value());
          try {
            field.set(null, item);
          } catch (IllegalAccessException e) {
            throw new CubesException("Failed to getInstances()", e);
          }
        }
      }
    }
  }
}
