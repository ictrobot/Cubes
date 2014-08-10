package ethanjones.modularworld.world.reference;

public class BlockReference {

  public int blockX;
  public int blockY;
  public int blockZ;

  public void clear() {
    blockX = 0;
    blockY = 0;
    blockZ = 0;
  }

  public BlockReference set(int blockX, int blockY, int blockZ) {
    this.blockX = blockX;
    this.blockY = blockY;
    this.blockZ = blockZ;
    return this;
  }

}
