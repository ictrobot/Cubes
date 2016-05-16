package ethanjones.cubes.world.thread;

import ethanjones.cubes.core.event.world.generation.AreaGeneratedEvent;
import ethanjones.cubes.core.event.world.generation.FeaturesEvent;
import ethanjones.cubes.core.event.world.generation.GenerationEvent;
import ethanjones.cubes.core.system.ThreadPool;
import ethanjones.cubes.side.Side;
import ethanjones.cubes.world.light.SunLight;
import ethanjones.cubes.world.reference.AreaReference;
import ethanjones.cubes.world.reference.multi.MultiAreaReference;
import ethanjones.cubes.world.server.WorldServer;
import ethanjones.cubes.world.storage.Area;

import java.util.concurrent.atomic.AtomicReference;

public class WorldTasks {

  public static final int GENERATION_THREADS = 8;

  private static final WorldGenerationThread gen = new WorldGenerationThread();
  private static final ThreadPool threadPool;

  static {
    threadPool = new ThreadPool("WorldGeneration", gen, GENERATION_THREADS);
    threadPool.setSide(Side.Server);
    threadPool.setPriority(Thread.MIN_PRIORITY);
    threadPool.setDaemon(true);
    threadPool.start();
  }

  public static GenerationTask request(WorldServer worldServer, MultiAreaReference references, WorldRequestParameter parameter) {
    WorldGenerationTask generationTask = new WorldGenerationTask(worldServer, references, parameter);
    gen.queue.add(generationTask);
    return generationTask;
  }

  protected static void generate(AreaReference areaReference, WorldServer world) {
    Area area = world.getArea(areaReference, false);
    if (area != null) return;

    area = new Area(areaReference.areaX, areaReference.areaZ);
    world.getTerrainGenerator().generate(area);
    new GenerationEvent(area, areaReference).post();

    world.setAreaInternal(area);
  }

  protected static void features(AreaReference areaReference, WorldServer world) {
    Area area = world.getArea(areaReference, false);
    if (area == null) return;

    AtomicReference<Object> features = area.features;

    if (features.compareAndSet(null, Thread.currentThread())) {
      world.getTerrainGenerator().features(area, world);
      new FeaturesEvent(area, areaReference).post();
      area.updateAll();
      area.rebuildHeightmap();
      SunLight.initialSunlight(area);
      new AreaGeneratedEvent(area, areaReference).post();
    }
  }
}
