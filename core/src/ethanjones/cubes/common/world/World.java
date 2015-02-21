package ethanjones.cubes.common.world;

import com.badlogic.gdx.utils.Disposable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import ethanjones.cubes.common.block.Block;
import ethanjones.cubes.common.util.Pools;
import ethanjones.cubes.common.world.generator.TerrainGenerator;
import ethanjones.cubes.common.world.reference.AreaReference;
import ethanjones.cubes.common.world.storage.Area;

public abstract class World implements Disposable {

  public final HashMap<AreaReference, Area> map;
  public final TerrainGenerator terrainGenerator;
  public final ArrayList<AreaReference> requested;

  public World(TerrainGenerator terrainGenerator) {
    this.terrainGenerator = terrainGenerator;
    map = new HashMap<AreaReference, Area>(1024);
    requested = new ArrayList<AreaReference>();
  }

  public boolean setAreaInternal(AreaReference areaReference, Area area) {
    synchronized (map) {
      map.put(areaReference.clone(), area);
    }
    return true;
  }

  public Block getBlock(int x, int y, int z) {
    Area area = getArea(CoordinateConverter.area(x), CoordinateConverter.area(z));
    return area == null ? null : area.getBlock(x - area.minBlockX, y, z - area.minBlockZ);
  }

  public Area getArea(int areaX, int areaZ) {
    AreaReference areaReference = Pools.obtainAreaReference().setFromAreaCoordinates(areaX, areaZ);
    Area area = getArea(areaReference);
    Pools.free(AreaReference.class, areaReference);
    return area;
  }

  public Area getArea(AreaReference areaReference) {
    return getAreaInternal(areaReference, true);
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

  public void setBlock(Block block, int x, int y, int z) {
    Area area = getArea(CoordinateConverter.area(x), CoordinateConverter.area(z));
    if (area != null) area.setBlock(block, x - area.minBlockX, y, z - area.minBlockZ);
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
