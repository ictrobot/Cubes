package ethanjones.cubes.world.thread;

import ethanjones.cubes.core.gwt.Task;
import ethanjones.cubes.side.common.Side;
import ethanjones.cubes.world.reference.AreaReference;

import java.util.ArrayDeque;

public class WorldGenerationRunnable extends Task {
  public ArrayDeque<WorldGenerationTask> queue = new ArrayDeque<WorldGenerationTask>();

  public WorldGenerationRunnable() {
    super(2);
    setSide(Side.Server);
    setName("WorldGeneration");
    setAbbreviation("W");
  }

  @Override
  public void run() {
    while (true) {
      checkTime();

      WorldGenerationTask task = queue.peek();
      if (task == null) throw new TimelimitException();

      if (task.world.isDisposed()) {
        queue.remove(task);
        continue;
      }

      task.timeStarted.compareAndSet(0, System.currentTimeMillis());
      AreaReference generate = task.generateQueue.poll();
      while (generate != null) {
        int status = WorldTasks.generate(generate, task.world);
        if (status == 1) { // read from file
          task.readCounter.incrementAndGet();
        } else if (status == 2) { // generated
          task.generateCounter.incrementAndGet();
        }
        
        checkTime();
        generate = task.generateQueue.poll();
      }
  
      checkTime();
      AreaReference features = task.featuresQueue.poll();
      while (features != null) {
        int status = WorldTasks.features(features, task.world);
        if (status == 1) { // done features
          task.featureCounter.incrementAndGet();
        }
  
        checkTime();
        features = task.featuresQueue.poll();
      }

      if (queue.remove(task) && task.parameter.afterCompletion != null) {
        task.printStatistics();
        task.parameter.afterCompletion.run();
      }
    }
  }
}
