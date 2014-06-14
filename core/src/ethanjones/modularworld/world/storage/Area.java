package ethanjones.modularworld.world.storage;

import ethanjones.modularworld.block.Block;

public class Area {
  
  public static final int S = 32;
  
  public final int x;
  public final int y;
  public final int z;
  
  public final int maxNormalX;
  public final int maxNormalY;
  public final int maxNormalZ;
  public final int minNormalX;
  public final int minNormalY;
  public final int minNormalZ;
  
  public boolean generated = false;
  
  private Block[][][] blocks = new Block[S][S][S];
  
  /**
   * In area coords
   */
  public Area(int x, int y, int z) {
    this.x = x;
    this.y = y;
    this.z = z;
    
    maxNormalX = ((x + 1) * S) - 1;
    maxNormalY = ((y + 1) * S) - 1;
    maxNormalZ = ((z + 1) * S) - 1;
    minNormalX = x * S;
    minNormalY = y * S;
    minNormalZ = z * S;
  }
  
  public Block getBlock(int x, int y, int z) {
    return blocks[Math.abs(x % S)][Math.abs(y % S)][Math.abs(z % S)];
  }
  
  public Block[][][] getBlocks() {
    return blocks;
  }
  
  public void setBlock(Block block, int x, int y, int z) {
    blocks[Math.abs(x % S)][Math.abs(y % S)][Math.abs(z % S)] = block;
  }
}
