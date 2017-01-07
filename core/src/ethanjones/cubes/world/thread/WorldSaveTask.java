package ethanjones.cubes.world.thread;

import ethanjones.cubes.world.save.Save;
import ethanjones.cubes.world.storage.Area;
import ethanjones.cubes.world.storage.AreaMap;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class WorldSaveTask {

  public final Save save;
  public final ConcurrentLinkedQueue<Area> saveQueue = new ConcurrentLinkedQueue<Area>();
  public final CountDownLatch saveComplete = new CountDownLatch(WorldTasks.SAVE_THREADS);
  public final int length;
  public final AtomicInteger written = new AtomicInteger(0);

  public WorldSaveTask(Save save, Collection<Area> areas) {
    this.save = save;
    this.saveQueue.addAll(areas);
    this.length = saveQueue.size();
  }
  
  public WorldSaveTask(Save save, AreaMap areas) {
    this.save = save;
    areas.lock.readLock();
    for (Area area : areas) {
      this.saveQueue.add(area);
    }
    areas.lock.readUnlock();
    this.length = saveQueue.size();
  }
}
