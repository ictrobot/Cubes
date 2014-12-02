package ethanjones.cubes.world.reference;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool;

import ethanjones.cubes.world.CoordinateConverter;
import ethanjones.cubes.world.storage.Area;

public class AreaReference implements Pool.Poolable, Cloneable {

  public int areaX;
  public int areaY;
  public int areaZ;

  public AreaReference setFromArea(Area area) {
    this.areaX = area.x;
    this.areaY = area.y;
    this.areaZ = area.z;
    return this;
  }

  public AreaReference setFromAreaCoordinates(int areaX, int areaY, int areaZ) {
    this.areaX = areaX;
    this.areaY = areaY;
    this.areaZ = areaZ;
    return this;
  }

  public AreaReference setFromAreaReference(AreaReference areaReference) {
    this.areaX = areaReference.areaX;
    this.areaY = areaReference.areaY;
    this.areaZ = areaReference.areaZ;
    return this;
  }

  public AreaReference setFromBlockCoordinates(int blockX, int blockY, int blockZ) {
    this.areaX = CoordinateConverter.area(blockX);
    this.areaY = CoordinateConverter.area(blockY);
    this.areaZ = CoordinateConverter.area(blockZ);
    return this;
  }

  public AreaReference setFromBlockReference(BlockReference blockReference) {
    this.areaX = CoordinateConverter.area(blockReference.blockX);
    this.areaY = CoordinateConverter.area(blockReference.blockY);
    this.areaZ = CoordinateConverter.area(blockReference.blockZ);
    return this;
  }

  public AreaReference setFromPositionVector3(Vector3 vector3) {
    setFromPosition(vector3.x, vector3.y, vector3.z);
    return this;
  }

  public AreaReference setFromPosition(float x, float y, float z) {
    this.areaX = CoordinateConverter.area(CoordinateConverter.block(x));
    this.areaY = CoordinateConverter.area(CoordinateConverter.block(y));
    this.areaZ = CoordinateConverter.area(CoordinateConverter.block(z));
    return this;
  }

  @Override
  public int hashCode() {
    return areaX ^ areaY ^ areaZ;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof AreaReference) {
      AreaReference areaReference = (AreaReference) obj;
      return areaReference.areaX == areaX && areaReference.areaY == areaY && areaReference.areaZ == areaZ;
    }
    return false;
  }

  public AreaReference clone() {
    return new AreaReference().setFromAreaReference(this);
  }

  @Override
  public String toString() {
    return areaX + "," + areaY + "," + areaZ;
  }

  @Override
  public void reset() {
    areaX = 0;
    areaY = 0;
    areaZ = 0;
  }
}
