package ethanjones.cubes.world.generator.smooth;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.world.reference.AreaReference;
import ethanjones.cubes.world.storage.Area;

import java.util.HashMap;

public class CaveManager {

  private final SmoothWorld smoothWorld;
  private final HashMap<AreaReference, Cave> caves = new HashMap<AreaReference, Cave>();
  private final AreaReference current = new AreaReference();

  public CaveManager(SmoothWorld smoothWorld) {
    this.smoothWorld = smoothWorld;
  }

  public void apply(Area area) {
    int r = 3;
    for (int aX = area.areaX - r; aX <= area.areaX + r; aX++) {
      for (int aZ = area.areaZ - r; aZ <= area.areaZ + r; aZ++) {
        if (smoothWorld.pseudorandomInt(aX * 0xCAE, aZ * 0xCAE, 48) == 0) {
          Cave cave;
          synchronized (this) {
            current.setFromAreaCoordinates(aX, aZ);
            cave = caves.get(current);
            if (cave == null) {
              cave = loadCave();
              if (cave == null) cave = generateCave();
              caves.put(current.clone(), cave);
            }
          }
          cave.apply(area);
        }
      }
    }
  }

  private Cave loadCave() {
    return null; //TODO implement storing and loading of caves
  }

  private Cave generateCave() {
    int offsetX = smoothWorld.pseudorandomInt(current.areaX, current.areaZ, Area.SIZE_BLOCKS - 1);
    int offsetZ = smoothWorld.pseudorandomInt(current.areaZ, current.areaX, Area.SIZE_BLOCKS - 1);

    int x = current.minBlockX() + offsetX;
    int z = current.minBlockZ() + offsetZ;

    Log.debug("Generating new cave at " + x + "," + z);
    return new Cave(x, z, smoothWorld);
  }

}
