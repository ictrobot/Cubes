package ethanjones.cubes.world.thread;

import ethanjones.cubes.world.reference.AreaReference;
import ethanjones.cubes.world.reference.multi.MultiAreaReference;
import ethanjones.cubes.world.reference.multi.WorldRegion;
import ethanjones.cubes.world.server.WorldServer;
import ethanjones.cubes.world.storage.Area;

import com.badlogic.gdx.math.MathUtils;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

public class WorldGenerationTask {

  public final WorldServer world;
  public final MultiAreaReference references;
  public final WorldRequestParameter parameter;
  public final ConcurrentLinkedQueue<AreaReference> generateQueue = new ConcurrentLinkedQueue<AreaReference>();
  public final CountDownLatch generationComplete = new CountDownLatch(WorldTasks.GENERATION_THREADS);
  public final ConcurrentLinkedQueue<AreaReference> featuresQueue = new ConcurrentLinkedQueue<AreaReference>();

  public WorldGenerationTask(WorldServer world, MultiAreaReference references, WorldRequestParameter parameter) {
    this.world = world;
    this.references = references;
    this.parameter = parameter != null ? parameter : WorldRequestParameter.DEFAULT;

    fillGenerateQueue();
    fillFeaturesQueue();
  }

  private void fillGenerateQueue() {
    Set<AreaReference> generate;
    if (this.references instanceof WorldRegion) {
      WorldRegion f = (WorldRegion) this.references;
      WorldRegion g = new WorldRegion(f.minAreaX - 1, f.maxAreaX + 1, f.minAreaZ - 1, f.maxAreaZ + 1);
      generate = g.getAreaReferences();
    } else {
      generate = new HashSet<AreaReference>();
      for (AreaReference reference : references.getAreaReferences()) {
        generate.add(reference.clone().offset(0, 1));
        generate.add(reference.clone().offset(0, -1));
        generate.add(reference.clone().offset(1, 0));
        generate.add(reference.clone().offset(1, 1));
        generate.add(reference.clone().offset(1, -1));
        generate.add(reference.clone().offset(-1, 0));
        generate.add(reference.clone().offset(-1, 1));
        generate.add(reference.clone().offset(-1, -1));
      }
    }

    if (generate.size() >= 50) {
      //randomize so all threads not waiting for one cave
      ArrayList<AreaReference> copy = new ArrayList<AreaReference>(generate);
      while (copy.size() > 0) {
        generateQueue.add(copy.remove(MathUtils.random.nextInt(copy.size())));
      }
    } else {
      generateQueue.addAll(generate);
    }
  }

  private void fillFeaturesQueue() {
    if (parameter.prioritise != null) {
      TreeSet<AreaReference> set = new TreeSet<AreaReference>(parameter.getComparator());
      set.addAll(references.getAreaReferences());
      featuresQueue.addAll(set);
    } else {
      featuresQueue.addAll(references.getAreaReferences());
    }
  }
}
