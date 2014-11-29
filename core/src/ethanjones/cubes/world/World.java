package ethanjones.cubes.world;

import com.badlogic.gdx.utils.Disposable;

import ethanjones.cubes.block.Block;
import ethanjones.cubes.core.system.Pools;
import ethanjones.cubes.world.reference.AreaReference;
import ethanjones.cubes.world.storage.Area;
import ethanjones.cubes.world.storage.BlankArea;

public abstract class World implements Disposable {

  public final static BlankArea BLANK_AREA = new BlankArea();

  public World() {

  }

  public abstract boolean setAreaInternal(AreaReference areaReference, Area area);

  public Area getAreaPlain(AreaReference areaReference) {
    return getAreaInternal(areaReference, false, true);
  }

  /**
   * @param request     If the area should be requested
   * @param returnBlank If BLANK_AREA should be returned if null
   */
  public abstract Area getAreaInternal(AreaReference areaReference, boolean request, boolean returnBlank);

  public Block getBlock(int x, int y, int z) {
    return getArea(CoordinateConverter.area(x), CoordinateConverter.area(y), CoordinateConverter.area(z)).getBlock(x, y, z);
  }

  public Area getArea(int areaX, int areaY, int areaZ) {
    AreaReference areaReference = Pools.obtain(AreaReference.class).setFromAreaCoordinates(areaX, areaY, areaZ);
    Area area = getArea(areaReference);
    Pools.free(AreaReference.class, areaReference);
    return area;
  }

  public Area getArea(AreaReference areaReference) {
    return getAreaInternal(areaReference, true, true);
  }

  public void setBlock(Block block, int x, int y, int z) {
    getArea(CoordinateConverter.area(x), CoordinateConverter.area(y), CoordinateConverter.area(z)).setBlock(block, x, y, z);
  }

}
