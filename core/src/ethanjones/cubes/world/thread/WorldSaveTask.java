package ethanjones.cubes.world.thread;

import ethanjones.cubes.core.util.locks.Locked;
import ethanjones.cubes.world.save.Save;
import ethanjones.cubes.world.storage.Area;
import ethanjones.cubes.world.storage.AreaMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class WorldSaveTask {

  public final Save save;
  public final int length;

  final ArrayList<Area> saveAreas = new ArrayList<Area>();
  final ConcurrentLinkedQueue<Area> saveQueue = new ConcurrentLinkedQueue<Area>();
  final CountDownLatch saveComplete = new CountDownLatch(WorldTasks.SAVE_THREADS);

  final AtomicLong timeStarted = new AtomicLong(0);
  final AtomicInteger written = new AtomicInteger(0);

  public WorldSaveTask(Save save, Collection<Area> areas) {
    this.save = save;
    this.saveQueue.addAll(areas);
    this.saveAreas.addAll(areas);
    this.length = saveQueue.size();
  }
  
  public WorldSaveTask(Save save, AreaMap areas) {
    this.save = save;
    try (Locked<WorldLockable> locked = areas.acquireReadLock()) {
      for (Area area : areas) {
        this.saveQueue.add(area);
        this.saveAreas.add(area);
      }
    }
    this.length = saveQueue.size();
  }
}
