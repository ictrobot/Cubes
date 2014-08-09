package ethanjones.modularworld.world.reference;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool;
import ethanjones.modularworld.world.coordinates.AreaCoordinates;
import ethanjones.modularworld.world.coordinates.BlockCoordinates;
import ethanjones.modularworld.world.storage.Area;

public class AreaReference implements Pool.Poolable, Cloneable {

  public int areaX;
  public int areaY;
  public int areaZ;

  public int arrayX;
  public int arrayY;
  public int arrayZ;

  public int arrayPos;

  public AreaReference() {

  }

  public AreaReference clear() {
    areaX = 0;
    areaY = 0;
    areaZ = 0;

    arrayX = 0;
    arrayY = 0;
    arrayZ = 0;

    arrayPos = 0;

    return this;
  }

  public AreaReference setFromArea(Area area) {
    setFromArea(area.x, area.y, area.z);
    return this;
  }

  public AreaReference setFromAreaCoordinates(AreaCoordinates areaCoordinates) {
    setFromArea(areaCoordinates.areaX, areaCoordinates.areaY, areaCoordinates.areaZ);
    return this;
  }

  public AreaReference setFromArea(int areaX, int areaY, int areaZ) {
    this.areaX = areaX;
    this.areaY = areaY;
    this.areaZ = areaZ;
    return this;
  }

  public AreaCoordinates getAreaCoordinates() {
    return new AreaCoordinates(areaX, areaY, areaZ);
  }

  public AreaReference setFromBlock(int blockX, int blockY, int blockZ) {
    this.areaX = BlockCoordinates.area(blockX);
    this.areaY = BlockCoordinates.area(blockY);
    this.areaZ = BlockCoordinates.area(blockZ);
    return this;
  }

  public AreaReference setFromPosition(float x, float y, float z) {
    setFromBlock((int) Math.ceil(x), (int) Math.ceil(y), (int) Math.ceil(z));
    return this;
  }

  public AreaReference setFromVector3(Vector3 vector3) {
    setFromPosition(vector3.x, vector3.y, vector3.z);
    return this;
  }

  /**
   * Uses areaX, areaY and areaZ to create a new Area and return it
   */
  public Area newArea() {
    return new Area(areaX, areaY, areaZ);
  }

  @Override
  public void reset() {
    clear();
  }

  public AreaReference clone() {
    return new AreaReference().setFromArea(areaX, areaY, areaZ);
  }

  @Override
  public String toString() {
    return areaX + " " + areaY + " " + areaZ;
  }
}
