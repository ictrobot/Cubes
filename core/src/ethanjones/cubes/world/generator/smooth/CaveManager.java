package ethanjones.cubes.world.generator.smooth;

import ethanjones.cubes.core.system.Executor;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.world.reference.AreaReference;
import ethanjones.cubes.world.save.Save;
import ethanjones.cubes.world.storage.Area;

import java.util.concurrent.ConcurrentHashMap;

public class CaveManager {
  public static final int caveAreaRadius = 4;
  public static final int caveBlockRadius = caveAreaRadius * Area.SIZE_BLOCKS;
  public static final int caveSafeBlockRadius = caveBlockRadius - Area.SIZE_BLOCKS;

  private final SmoothWorld smoothWorld;
  private Cave spawnCave;
  private final ConcurrentHashMap<AreaReference, Object> caves = new ConcurrentHashMap<AreaReference, Object>();

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
          Cave cave = null;

          AreaReference areaReference = new AreaReference().setFromAreaCoordinates(aX, aZ);
          Object o = caves.putIfAbsent(areaReference, Thread.currentThread());
          if (o instanceof Cave) {
            cave = (Cave) o;
          } else if (o == null) { // this thread
            cave = loadCave(areaReference);
            if (cave == null) cave = generateCave(areaReference);
            if (!caves.replace(areaReference.clone(), Thread.currentThread(), cave)) throw new IllegalStateException();
            synchronized (caves) {
              caves.notifyAll();
            }
          } else if (o instanceof Thread) { // wait for another thread
            while (cave == null) {
              synchronized (caves) {
                try {
                  caves.wait();
                } catch (InterruptedException ignored) {
                }
              }
              o = caves.get(areaReference);
              if (o instanceof Cave) cave = (Cave) o;
            }
          } else {
            throw new IllegalStateException(o.getClass().getName());
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
        CaveGenerator caveGenerator = new CaveGenerator(spawnCaveX, spawnCaveZ, smoothWorld);
        this.spawnCave = caveGenerator.generate();
      }
      return spawnCave;
    }
  }

  private Cave loadCave(AreaReference a) {
    return Cubes.getServer().world.save.readCave(a);
  }

  private Cave generateCave(final AreaReference a) {
    int offsetX = smoothWorld.pseudorandomInt(a.areaX, a.areaZ, Area.SIZE_BLOCKS - 1);
    int offsetZ = smoothWorld.pseudorandomInt(a.areaZ, a.areaX, Area.SIZE_BLOCKS - 1);

    int x = a.minBlockX() + offsetX;
    int z = a.minBlockZ() + offsetZ;

    CaveGenerator caveGenerator = new CaveGenerator(x, z, smoothWorld);
    final Cave cave = caveGenerator.generate();
    final Save save = Cubes.getServer().world.save;
    Executor.executeNotSided(new Runnable() {
      @Override
      public void run() {
        save.writeCave(a, cave);
      }
    });
    return cave;
  }
}
