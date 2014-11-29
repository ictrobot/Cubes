package ethanjones.cubes.world;

import com.badlogic.gdx.utils.Disposable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import ethanjones.cubes.block.Block;
import ethanjones.cubes.core.system.Pools;
import ethanjones.cubes.world.generator.TerrainGenerator;
import ethanjones.cubes.world.reference.AreaReference;
import ethanjones.cubes.world.storage.Area;

public abstract class World implements Disposable {

  public final HashMap<AreaReference, Area> map;
  public final TerrainGenerator terrainGenerator;
  public final ArrayList<AreaReference> requested;

  public World(TerrainGenerator terrainGenerator) {
    this.terrainGenerator = terrainGenerator;
    map = new HashMap<AreaReference, Area>(1024);
    requested = new ArrayList<AreaReference>();
  }

  public Area getAreaInternal(AreaReference areaReference, boolean request) {
    Area area;
    synchronized (map) {
      area = map.get(areaReference);
    }
    if (area != null) {
      return area;
    } else if (request) {
      requestArea(areaReference);
    }
    return null;
  }

  public boolean setAreaInternal(AreaReference areaReference, Area area) {
    synchronized (map) {
      map.put(areaReference.clone(), area);
    }
    return true;
  }

  public void requestArea(AreaReference areaReference) {
    synchronized (requested) {
      if (requested.contains(areaReference)) {
        return;
      } else {
        requested.add(areaReference.clone());
      }
    }
    requestAreaInternal(areaReference.clone());
  }

  protected abstract void requestAreaInternal(AreaReference areaReference);

  public Block getBlock(int x, int y, int z) {
    Area area = getArea(CoordinateConverter.area(x), CoordinateConverter.area(y), CoordinateConverter.area(z));
    return area == null ? null : area.getBlock(x, y, z);
  }

  public void setBlock(Block block, int x, int y, int z) {
    Area area = getArea(CoordinateConverter.area(x), CoordinateConverter.area(y), CoordinateConverter.area(z));
    if (area != null) area.setBlock(block, x, y, z);
  }

  public Area getArea(int areaX, int areaY, int areaZ) {
    AreaReference areaReference = Pools.obtainAreaReference().setFromAreaCoordinates(areaX, areaY, areaZ);
    Area area = getArea(areaReference);
    Pools.free(AreaReference.class, areaReference);
    return area;
  }

  public Area getArea(AreaReference areaReference) {
    return getAreaInternal(areaReference, true);
  }

  public TerrainGenerator getTerrainGenerator() {
    return terrainGenerator;
  }

  @Override
  public void dispose() {
    synchronized (map) {
      for (Entry<AreaReference, Area> entry : map.entrySet()) {
        entry.getValue().unload();
      }
      map.clear();
    }
  }

}
