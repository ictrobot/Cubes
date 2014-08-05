package ethanjones.modularworld.world;

import com.badlogic.gdx.utils.Disposable;
import ethanjones.modularworld.block.Block;
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

  protected abstract Area getAreaInternal(AreaReference areaReference, boolean request, boolean generatedCheck);

  public abstract boolean setAreaInternal(AreaReference areaReference, Area area);

  public abstract void requestArea(AreaReference areaReference);

  public Area getAreaPlain(AreaReference areaReference) {
    return getAreaInternal(areaReference, false, false);
  }

  public Area getArea(AreaReference areaReference) {
    return getAreaInternal(areaReference, true, true);
  }

  public Area getArea(int areaX, int areaY, int areaZ) {
    AreaReference areaReference = areaReferencePool.obtain().setFromArea(areaX, areaY, areaZ);
    Area area = getArea(areaReference);
    areaReferencePool.free(areaReference);
    return area;
  }

  public Block getBlock(int x, int y, int z) {
    return getArea(BlockCoordinates.area(x), BlockCoordinates.area(y), BlockCoordinates.area(z)).getBlock(x, y, z);
  }

  public void setBlock(Block block, int x, int y, int z) {
    getArea(BlockCoordinates.area(x), BlockCoordinates.area(y), BlockCoordinates.area(z)).setBlock(block, x, y, z);
  }

}
