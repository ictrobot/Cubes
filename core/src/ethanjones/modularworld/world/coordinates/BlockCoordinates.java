package ethanjones.modularworld.world.coordinates;

import ethanjones.modularworld.world.storage.Area;

public class BlockCoordinates extends AreaCoordinates {

  public final int blockX;
  public final int blockY;
  public final int blockZ;

  public BlockCoordinates(double blockX, double blockY, double blockZ) {
    this((int) Math.ceil(blockX), (int) Math.ceil(blockY), (int) Math.ceil(blockZ));
  }

  public BlockCoordinates(int blockX, int blockY, int blockZ) {
    super(area(blockX), area(blockY), area(blockZ));
    this.blockX = blockX;
    this.blockY = blockY;
    this.blockZ = blockZ;
  }

  public static int area(int block) {
    if (block >= 0) {
      return block / Area.SIZE_BLOCKS;
    } else {
      return (block - Area.SIZE_BLOCKS) / Area.SIZE_BLOCKS;
    }
  }

}
