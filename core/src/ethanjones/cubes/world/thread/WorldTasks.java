package ethanjones.cubes.world.thread;

import ethanjones.cubes.core.event.world.generation.AreaLoadedEvent;
import ethanjones.cubes.core.event.world.generation.FeaturesEvent;
import ethanjones.cubes.core.event.world.generation.GenerationEvent;
import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.system.ThreadPool;
import ethanjones.cubes.side.Side;
import ethanjones.cubes.world.light.SunLight;
import ethanjones.cubes.world.reference.AreaReference;
import ethanjones.cubes.world.reference.multi.MultiAreaReference;
import ethanjones.cubes.world.save.Save;
import ethanjones.cubes.world.server.WorldServer;
import ethanjones.cubes.world.storage.Area;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;

public class WorldTasks {

  public static final int GENERATION_THREADS = 8;
  public static final int SAVE_THREADS = 8;

  private static final WorldGenerationRunnable gen = new WorldGenerationRunnable();
  private static final WorldSaveRunnable save = new WorldSaveRunnable();
  private static final ThreadPool genThreadPool;
  private static final ThreadPool saveThreadPool;

  static {
    genThreadPool = new ThreadPool("WorldGeneration", gen, GENERATION_THREADS);
    genThreadPool.setSide(Side.Server);
    genThreadPool.setPriority(Thread.MIN_PRIORITY);
    genThreadPool.setDaemon(true);
    genThreadPool.start();
    saveThreadPool = new ThreadPool("Save", save, SAVE_THREADS);
    saveThreadPool.setSide(Side.Server);
    saveThreadPool.setPriority(Thread.MIN_PRIORITY + 1);
    saveThreadPool.setDaemon(true);
    saveThreadPool.start();
  }

  public static GenerationTask request(WorldServer worldServer, MultiAreaReference references, WorldRequestParameter parameter) {
    WorldGenerationTask generationTask = new WorldGenerationTask(worldServer, references, parameter);
    gen.queue.add(generationTask);
    return generationTask;
  }

  public static void save(Save s, Collection<Area> areas) {
    WorldSaveTask saveTask = new WorldSaveTask(s, areas);
    save.queue.add(saveTask);
  }

  public static boolean currentlySaving() {
    return save.queue.size() > 0;
  }

  public static boolean waitSaveFinish() {
    while (currentlySaving()) {
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        Log.debug(e);
        return false;
      }
    }
    return true;
  }

  protected static void generate(AreaReference areaReference, WorldServer world) {
    Area area = world.getArea(areaReference, false);
    if (area != null) return;
    area = world.save.readArea(areaReference.areaX, areaReference.areaZ);
    if (area != null) {
      world.setAreaInternal(area);
      if (area.features.get() != null) new AreaLoadedEvent(area, areaReference).post();
      return;
    }

    area = new Area(areaReference.areaX, areaReference.areaZ);
    world.getTerrainGenerator().generate(area);
    new GenerationEvent(area, areaReference).post();
    area.modify();

    world.setAreaInternal(area);
  }

  protected static void features(AreaReference areaReference, WorldServer world) {
    Area area = world.getArea(areaReference, false);
    if (area == null) return;

    AtomicReference<Object> features = area.features;

    if (features.compareAndSet(null, Thread.currentThread())) {
      world.getTerrainGenerator().features(area, world);
      new FeaturesEvent(area, areaReference).post();
      area.initialUpdate();
      SunLight.initialSunlight(area);
      new AreaLoadedEvent(area, areaReference).post();
    }
  }
}
