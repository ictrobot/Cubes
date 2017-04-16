package ethanjones.cubes.world.thread;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.world.reference.AreaReference;
import ethanjones.cubes.world.reference.multi.MultiAreaReference;
import ethanjones.cubes.world.reference.multi.WorldRegion;
import ethanjones.cubes.world.server.WorldServer;

import com.badlogic.gdx.math.MathUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import ethanjones.cubes.core.gwt.FakeAtomic.AtomicInteger;
import ethanjones.cubes.core.gwt.FakeAtomic.AtomicLong;

public class WorldGenerationTask implements GenerationTask {

  public final WorldServer world;
  public final MultiAreaReference references;
  public final WorldRequestParameter parameter;
  public final ConcurrentLinkedQueue<AreaReference> generateQueue = new ConcurrentLinkedQueue<AreaReference>();
  public final ConcurrentLinkedQueue<AreaReference> featuresQueue = new ConcurrentLinkedQueue<AreaReference>();
  public final AtomicLong timeStarted = new AtomicLong(0);
  public final AtomicInteger generateCounter = new AtomicInteger(0);
  public final AtomicInteger featureCounter = new AtomicInteger(0);
  public final AtomicInteger readCounter = new AtomicInteger(0);
  public final int generateSize;
  public final int featureSize;

  public WorldGenerationTask(WorldServer world, MultiAreaReference references, WorldRequestParameter parameter) {
    this.world = world;
    this.references = references;
    this.parameter = parameter != null ? parameter : WorldRequestParameter.DEFAULT;

    generateSize = fillGenerateQueue();
    featureSize = fillFeaturesQueue();
  }

  private int fillGenerateQueue() {
    Set<AreaReference> generate;
    if (this.references instanceof WorldRegion) {
      WorldRegion f = (WorldRegion) this.references;
      WorldRegion g = new WorldRegion(f.minAreaX - 1, f.maxAreaX + 1, f.minAreaZ - 1, f.maxAreaZ + 1);
      generate = g.getAreaReferences();
    } else {
      generate = new HashSet<AreaReference>();
      for (AreaReference reference : references.getAreaReferences()) {
        generate.add(reference.copy().offset(0, 1));
        generate.add(reference.copy().offset(0, -1));
        generate.add(reference.copy().offset(1, 0));
        generate.add(reference.copy().offset(1, 1));
        generate.add(reference.copy().offset(1, -1));
        generate.add(reference.copy().offset(-1, 0));
        generate.add(reference.copy().offset(-1, 1));
        generate.add(reference.copy().offset(-1, -1));
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
    return generate.size();
  }

  private int fillFeaturesQueue() {
    Set<AreaReference> set;
    if (parameter.prioritise != null) {
      set = new TreeSet<AreaReference>(parameter.getComparator());
      set.addAll(references.getAreaReferences());
    } else {
      set = references.getAreaReferences();
    }
    featuresQueue.addAll(set);
    return set.size();
  }

  @Override
  public int totalGenerate() {
    return generateSize;
  }

  @Override
  public int totalFeatures() {
    return featureSize;
  }

  @Override
  public int doneGenerate() {
    return generateSize - generateQueue.size();
  }

  @Override
  public int doneFeatures() {
    return featureSize - featuresQueue.size();
  }

  public void printStatistics() {
    long now = System.currentTimeMillis();
    long delta = now - timeStarted.get();
    Log.debug("Generated " + generateCounter.get() + " Read " + readCounter.get() + " Features " + featureCounter.get() + " Total " + generateSize + "," + featureSize + " Time " + delta + "ms");
  }
}
