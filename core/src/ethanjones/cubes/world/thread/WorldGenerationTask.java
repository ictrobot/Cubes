package ethanjones.cubes.world.thread;

import ethanjones.cubes.world.reference.AreaReference;
import ethanjones.cubes.world.reference.multi.MultiAreaReference;
import ethanjones.cubes.world.reference.multi.WorldRegion;
import ethanjones.cubes.world.server.WorldServer;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

public class WorldGenerationTask {

  public final WorldServer world;
  public final MultiAreaReference references;
  public final ConcurrentLinkedQueue<AreaReference> generateQueue = new ConcurrentLinkedQueue<AreaReference>();
  public final ConcurrentLinkedQueue<AreaReference> featuresQueue = new ConcurrentLinkedQueue<AreaReference>();

  public WorldGenerationTask(WorldServer world, MultiAreaReference references) {
    this.world = world;
    this.references = references;
    fillQueues();
  }

  private void fillQueues() {
    Collection<AreaReference> references = this.references.getAreaReferences();

    if (this.references instanceof WorldRegion) {
      WorldRegion f = (WorldRegion) this.references;
      WorldRegion g = new WorldRegion(f.minAreaX - 1, f.maxAreaX + 1, f.minAreaZ - 1, f.maxAreaZ + 1);
      generateQueue.addAll(g.getAreaReferences());
    } else {
      Set<AreaReference> set = new HashSet<AreaReference>(references.size() * 4);
      for (AreaReference reference : references) {
        set.add(reference.clone().offset(0, 1));
        set.add(reference.clone().offset(0, -1));
        set.add(reference.clone().offset(1, 0));
        set.add(reference.clone().offset(1, 1));
        set.add(reference.clone().offset(1, -1));
        set.add(reference.clone().offset(-1, 0));
        set.add(reference.clone().offset(-1, 1));
        set.add(reference.clone().offset(-1, -1));
      }
      generateQueue.addAll(set);
    }

    featuresQueue.addAll(references);
  }
}
