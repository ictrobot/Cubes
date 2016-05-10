package ethanjones.cubes.world.reference;

import ethanjones.cubes.world.CoordinateConverter;
import ethanjones.cubes.world.storage.Area;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool;

public class AreaReference implements Pool.Poolable, Cloneable {

  public int areaX;
  public int areaZ;
  private int hashCode = 0;

  public AreaReference setFromArea(Area area) {
    this.areaX = area.areaX;
    this.areaZ = area.areaZ;
    this.hashCode = 0;
    return this;
  }

  public AreaReference setFromAreaCoordinates(int areaX, int areaZ) {
    this.areaX = areaX;
    this.areaZ = areaZ;
    this.hashCode = 0;
    return this;
  }

  public AreaReference setFromBlockCoordinates(int blockX, int blockZ) {
    this.areaX = CoordinateConverter.area(blockX);
    this.areaZ = CoordinateConverter.area(blockZ);
    this.hashCode = 0;
    return this;
  }

  public AreaReference setFromBlockReference(BlockReference blockReference) {
    this.areaX = CoordinateConverter.area(blockReference.blockX);
    this.areaZ = CoordinateConverter.area(blockReference.blockZ);
    this.hashCode = 0;
    return this;
  }

  public AreaReference setFromPositionVector3(Vector3 vector3) {
    setFromPosition(vector3.x, vector3.z);
    return this;
  }

  public AreaReference setFromPosition(float x, float z) {
    this.areaX = CoordinateConverter.area(CoordinateConverter.block(x));
    this.areaZ = CoordinateConverter.area(CoordinateConverter.block(z));
    this.hashCode = 0;
    return this;
  }

  public AreaReference offset(int areaX, int areaZ) {
    this.areaX += areaX;
    this.areaZ += areaZ;
    this.hashCode = 0;
    return this;
  }

  public int minBlockX() {
    return areaX * Area.SIZE_BLOCKS;
  }

  public int minBlockZ() {
    return areaZ * Area.SIZE_BLOCKS;
  }

  public AreaReference modified() {
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
    hashCode = 31 * hashCode + areaX;
    hashCode = 31 * hashCode + areaZ;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof AreaReference) {
      AreaReference areaReference = (AreaReference) obj;
      return areaReference.areaX == areaX && areaReference.areaZ == areaZ;
    }
    return false;
  }

  public AreaReference clone() {
    return new AreaReference().setFromAreaReference(this);
  }

  public AreaReference setFromAreaReference(AreaReference areaReference) {
    this.areaX = areaReference.areaX;
    this.areaZ = areaReference.areaZ;
    this.hashCode = 0; //areaReference.hashCode; don't copy hashcode in case its invalid
    return this;
  }

  @Override
  public String toString() {
    return areaX + "," + areaZ;
  }

  @Override
  public void reset() {
    areaX = 0;
    areaZ = 0;
    hashCode = 0;
  }
}
