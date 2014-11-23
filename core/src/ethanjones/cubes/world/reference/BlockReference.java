package ethanjones.cubes.world.reference;

import com.badlogic.gdx.utils.Pool;

import ethanjones.cubes.world.CoordinateConverter;

public class BlockReference implements Pool.Poolable {

  public int blockX;
  public int blockY;
  public int blockZ;

  public BlockReference setFromBlockCoordinates(int blockX, int blockY, int blockZ) {
    this.blockX = blockX;
    this.blockY = blockY;
    this.blockZ = blockZ;
    return this;
  }

  public BlockReference setFromPosition(float x, float y, float z) {
    this.blockX = CoordinateConverter.block(x);
    this.blockY = CoordinateConverter.block(y);
    this.blockZ = CoordinateConverter.block(z);
    return this;
  }

  public String toString() {
    return blockX + "," + blockY + "," + blockZ;
  }

  @Override
  public void reset() {
    blockX = 0;
    blockY = 0;
    blockZ = 0;
  }
}
