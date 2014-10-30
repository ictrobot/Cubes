package ethanjones.cubes.world.reference;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool;

import ethanjones.cubes.core.util.MathHelper;
import ethanjones.cubes.world.storage.Area;

public class AreaReference implements Pool.Poolable, Cloneable {

  public int areaX;
  public int areaY;
  public int areaZ;

  public AreaReference() {

  }

  @Override
  public void reset() {
    areaX = 0;
    areaY = 0;
    areaZ = 0;
  }

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

  public AreaReference setFromBlockCoordinates(int blockX, int blockY, int blockZ) {
    this.areaX = MathHelper.area(blockX);
    this.areaY = MathHelper.area(blockY);
    this.areaZ = MathHelper.area(blockZ);
    return this;
  }

  public AreaReference setFromBlockReference(BlockReference blockReference) {
    this.areaX = MathHelper.area(blockReference.blockX);
    this.areaY = MathHelper.area(blockReference.blockY);
    this.areaZ = MathHelper.area(blockReference.blockZ);
    return this;
  }

  public AreaReference setFromPositionVector3(Vector3 vector3) {
    setFromPosition(vector3.x, vector3.y, vector3.z);
    return this;
  }

  public AreaReference setFromPosition(float x, float y, float z) {
    this.areaX = MathHelper.area(MathHelper.block(x));
    this.areaY = MathHelper.area(MathHelper.block(y));
    this.areaZ = MathHelper.area(MathHelper.block(z));
    return this;
  }

  @Override
  public int hashCode() {
    return (Math.abs(areaX) + 1) ^ (Math.abs(areaY) + 1) ^ (Math.abs(areaZ) + 1);
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

  public AreaReference setFromAreaReference(AreaReference areaReference) {
    this.areaX = areaReference.areaX;
    this.areaY = areaReference.areaY;
    this.areaZ = areaReference.areaZ;
    return this;
  }

  @Override
  public String toString() {
    return areaX + "," + areaY + "," + areaZ;
  }
}
