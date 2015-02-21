package ethanjones.cubes.common.world.client;

import java.util.Iterator;
import java.util.Map.Entry;

import ethanjones.cubes.common.util.Pools;
import ethanjones.cubes.Cubes;
import ethanjones.cubes.common.world.reference.AreaReference;
import ethanjones.cubes.common.world.storage.Area;

public class WorldClientMaintenance implements Runnable {

  private static final int AREA_LOAD_RADIUS = 10;
  private final WorldClient worldClient;

  public WorldClientMaintenance(WorldClient worldClient) {
    this.worldClient = worldClient;
  }

  @Override
  public void run() {
    AreaReference playerArea = Pools.obtainAreaReference().setFromPositionVector3(Cubes.getClient().player.position);
    synchronized (worldClient.map) {
      //Log.info("Before: " + System.currentTimeMillis() +  " " + worldClient.map.size());
      Iterator<Entry<AreaReference, Area>> iterator = worldClient.map.entrySet().iterator();
      while (iterator.hasNext()) {
        Entry<AreaReference, Area> entry = iterator.next();
        Area area = entry.getValue();
        int x = Math.abs(area.areaX - playerArea.areaX);
        int z = Math.abs(area.areaZ - playerArea.areaZ);
        if (x < 0 || x > AREA_LOAD_RADIUS || z < 0 || z > AREA_LOAD_RADIUS) {
          area.unload();
          iterator.remove();
        }
      }
      //Log.info("After: " + System.currentTimeMillis() + " " + worldClient.map.size());
    }
    Pools.free(playerArea);
  }
}
