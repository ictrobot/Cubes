package ethanjones.modularworld.world;

import com.badlogic.gdx.utils.Disposable;
import ethanjones.modularworld.block.Block;
import ethanjones.modularworld.world.coordinates.BlockCoordinates;
import ethanjones.modularworld.world.reference.AreaReference;
import ethanjones.modularworld.world.reference.AreaReferencePool;
import ethanjones.modularworld.world.storage.Area;
import ethanjones.modularworld.world.storage.BlankArea;

public abstract class World implements Disposable {

  public final static BlankArea BLANK_AREA = new BlankArea();
  protected final AreaReferencePool areaReferencePool;

  public World() {
    this.areaReferencePool = new AreaReferencePool();
  }

  /**
   * @param request     If the area should be requested
   * @param returnBlank If BLANK_AREA should be returned if null
   */
  public abstract Area getAreaInternal(AreaReference areaReference, boolean request, boolean returnBlank);

  public abstract boolean setAreaInternal(AreaReference areaReference, Area area);

  public Area getAreaPlain(AreaReference areaReference) {
    return getAreaInternal(areaReference, false, true);
  }

  public Area getArea(AreaReference areaReference) {
    return getAreaInternal(areaReference, true, true);
  }

  public Area getArea(int areaX, int areaY, int areaZ) {
    AreaReference areaReference;
    synchronized (areaReferencePool) {
      areaReference = areaReferencePool.obtain().setFromArea(areaX, areaY, areaZ);
    }
    Area area = getArea(areaReference);
    synchronized (areaReferencePool) {
      areaReferencePool.free(areaReference);
    }
    return area;
  }

  public Block getBlockFactory(int x, int y, int z) {
    return getArea(BlockCoordinates.area(x), BlockCoordinates.area(y), BlockCoordinates.area(z)).getBlockFactory(x, y, z);
  }

  public void setBlockFactory(Block block, int x, int y, int z) {
    getArea(BlockCoordinates.area(x), BlockCoordinates.area(y), BlockCoordinates.area(z)).setBlockFactory(block, x, y, z);
  }

}
