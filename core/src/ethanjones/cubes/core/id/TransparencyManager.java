package ethanjones.cubes.core.id;

import ethanjones.cubes.block.Block;

import java.util.BitSet;
import java.util.Map;

public class TransparencyManager {
  private static BitSet bitSet;
  
  private TransparencyManager() {
  }

  protected static void setup() {
    if (bitSet ==  null) bitSet = new BitSet(IDManager.getBlocks().size() * 2);
    for (Map.Entry<Integer, Block> entry : IDManager.integerToBlock.entrySet()) {
      bitSet.set(entry.getKey() * 2, entry.getValue().canBeTransparent());
      bitSet.set((entry.getKey() * 2) + 1, entry.getValue().alwaysTransparent());
    }
  }

  public static boolean isTransparent(int idAndMeta) {
    int blockID = idAndMeta & 0xFFFFF;
    int blockMeta = (idAndMeta >> 20) & 0xFF;
    // air || (canBeTransparent && (alwaysTransparent || lookup))
    return blockID == 0 || (bitSet.get(blockID * 2) && (bitSet.get((blockID * 2) + 1) || IDManager.integerToBlock.get(blockID).isTransparent(blockMeta)));
  }

  public static boolean isTransparent(int blockID, int blockMeta) {
    blockID &= 0xFFFFF;
    // air || (canBeTransparent && (alwaysTransparent || lookup))
    return blockID == 0 || (bitSet.get(blockID * 2) && (bitSet.get((blockID * 2) + 1) || IDManager.integerToBlock.get(blockID).isTransparent(blockMeta)));
  }

  public static boolean isTransparent(Block block, int meta) {
    return block == null || block.isTransparent(meta);
  }
}
