package ethanjones.cubes.world.reference;

import com.badlogic.gdx.utils.Pool;

import ethanjones.cubes.world.CoordinateConverter;

public class BlockReference implements Pool.Poolable {

  public int blockX;
  public int blockY;
  public int blockZ;
  private int hashCode = 0;

  public BlockReference setFromBlockCoordinates(int blockX, int blockY, int blockZ) {
    this.blockX = blockX;
    this.blockY = blockY;
    this.blockZ = blockZ;
    this.hashCode = 0;
    return this;
  }

  public BlockReference setFromBlockReference(BlockReference blockReference) {
    this.blockX = blockReference.blockX;
    this.blockY = blockReference.blockY;
    this.blockZ = blockReference.blockZ;
    this.hashCode = blockReference.hashCode;
    return this;
  }

  public BlockReference setFromPosition(float x, float y, float z) {
    this.blockX = CoordinateConverter.block(x);
    this.blockY = CoordinateConverter.block(y);
    this.blockZ = CoordinateConverter.block(z);
    this.hashCode = 0;
    return this;
  }

  @Override
  public int hashCode() {
    if (hashCode == 0) updateHashCode();
    return hashCode;
  }

  public void updateHashCode() {
    hashCode = 7;
    hashCode = 31 * hashCode + blockX;
    hashCode = 31 * hashCode + blockY;
    hashCode = 31 * hashCode + blockZ;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof BlockReference) {
      BlockReference blockReference = (BlockReference) obj;
      return blockReference.blockX == blockX && blockReference.blockY == blockY && blockReference.blockZ == blockZ;
    }
    return false;
  }

  public BlockReference clone() {
    return new BlockReference().setFromBlockReference(this);
  }

  public String toString() {
    return blockX + "," + blockY + "," + blockZ;
  }

  @Override
  public void reset() {
    blockX = 0;
    blockY = 0;
    blockZ = 0;
    hashCode = 0;
  }

}
