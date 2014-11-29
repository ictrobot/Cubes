package ethanjones.cubes.world.client;

import java.util.Iterator;
import java.util.Map.Entry;

import ethanjones.cubes.core.system.Pools;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.world.reference.AreaReference;
import ethanjones.cubes.world.storage.Area;

public class WorldClientMaintenance implements Runnable {

  private final WorldClient worldClient;
  private static final int AREA_LOAD_RADIUS = 10;

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
        int x = Math.abs(area.x - playerArea.areaX);
        int y = Math.abs(area.y - playerArea.areaY);
        int z = Math.abs(area.z - playerArea.areaZ);
        if (x < 0 || x > AREA_LOAD_RADIUS || y < 0 || y > AREA_LOAD_RADIUS || z < 0 || z > AREA_LOAD_RADIUS) {
          iterator.remove();
        }
      }
      //Log.info("After: " + System.currentTimeMillis() + " " + worldClient.map.size());
    }
    Pools.free(playerArea);
  }
}
