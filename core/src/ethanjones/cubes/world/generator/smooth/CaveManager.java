package ethanjones.cubes.world.generator.smooth;

import ethanjones.cubes.world.reference.AreaReference;
import ethanjones.cubes.world.storage.Area;

import java.util.HashMap;

public class CaveManager {
  public static final int caveAreaRadius = 4;
  public static final int caveBlockRadius = caveAreaRadius * Area.SIZE_BLOCKS;
  public static final int caveSafeBlockRadius = caveBlockRadius - Area.SIZE_BLOCKS;

  private final SmoothWorld smoothWorld;
  private Cave spawnCave;
  private final HashMap<AreaReference, Cave> caves = new HashMap<AreaReference, Cave>();
  private final AreaReference current = new AreaReference();

  public CaveManager(SmoothWorld smoothWorld) {
    this.smoothWorld = smoothWorld;
  }

  public void apply(Area area) {
    getSpawnCave().apply(area);

    for (int aX = area.areaX - caveAreaRadius; aX <= area.areaX + caveAreaRadius; aX++) {
      for (int aZ = area.areaZ - caveAreaRadius; aZ <= area.areaZ + caveAreaRadius; aZ++) {
        // 6 bits = 2^6 = 64
        // one in 64 areas
        if (smoothWorld.pseudorandomBits(aX, aZ, 6, true) == 0) {
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

  private Cave getSpawnCave() {
    synchronized (this) {
      if (spawnCave == null) {
        int spawnCaveX = smoothWorld.pseudorandomInt(1, 0, Area.SIZE_BLOCKS * 4) - (Area.SIZE_BLOCKS * 2);
        int spawnCaveZ = smoothWorld.pseudorandomInt(0, 1, Area.SIZE_BLOCKS * 4) - (Area.SIZE_BLOCKS * 2);
        this.spawnCave = new Cave(spawnCaveX, spawnCaveZ, smoothWorld);
      }
      return spawnCave;
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
    return new Cave(x, z, smoothWorld);
  }
}
