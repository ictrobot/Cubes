package ethanjones.modularworld.world;

import com.badlogic.gdx.utils.Disposable;
import ethanjones.modularworld.block.factory.BlockFactory;
import ethanjones.modularworld.world.coordinates.BlockCoordinates;
import ethanjones.modularworld.world.reference.AreaReference;
import ethanjones.modularworld.world.reference.AreaReferencePool;
import ethanjones.modularworld.world.storage.Area;
import ethanjones.modularworld.world.storage.BlankArea;
import ethanjones.modularworld.world.storage.Zone;

public abstract class World implements Disposable {

  public final static int WORLD_RADIUS_ZONES = 1000;
  public final static int HEIGHT_LIMIT = Zone.SIZE_BLOCKS;
  public final static int AREA_LOAD_RADIUS = 10;
  public final static BlankArea BLANK_AREA = new BlankArea();
  protected AreaReferencePool areaReferencePool;

  public World() {
    this.areaReferencePool = new AreaReferencePool();
  }

  protected abstract Area getAreaInternal(AreaReference areaReference, boolean request);

  public abstract boolean setAreaInternal(AreaReference areaReference, Area area);

  public abstract void requestArea(AreaReference areaReference);

  public Area getAreaPlain(AreaReference areaReference) {
    return getAreaInternal(areaReference, false);
  }

  public Area getArea(AreaReference areaReference) {
    return getAreaInternal(areaReference, true);
  }

  public Area getArea(int areaX, int areaY, int areaZ) {
    AreaReference areaReference = areaReferencePool.obtain().setFromArea(areaX, areaY, areaZ);
    Area area = getArea(areaReference);
    areaReferencePool.free(areaReference);
    return area;
  }

  public BlockFactory getBlockFactory(int x, int y, int z) {
    return getArea(BlockCoordinates.area(x), BlockCoordinates.area(y), BlockCoordinates.area(z)).getBlockFactory(x, y, z);
  }

  public void setBlockFactory(BlockFactory blockFactory, int x, int y, int z) {
    getArea(BlockCoordinates.area(x), BlockCoordinates.area(y), BlockCoordinates.area(z)).setBlockFactory(blockFactory, x, y, z);
  }

}
