package ethanjones.cubes.world.thread;

import ethanjones.cubes.core.system.CubesException;
import ethanjones.cubes.side.common.Side;
import ethanjones.cubes.world.reference.AreaReference;

import java.util.concurrent.LinkedBlockingQueue;

public class WorldGenerationRunnable implements Runnable {
  public LinkedBlockingQueue<WorldGenerationTask> queue = new LinkedBlockingQueue<WorldGenerationTask>();

  @Override
  public void run() {
    while (!Thread.interrupted()) {
      try {
        WorldGenerationTask task = queue.peek();
        while (task == null) {
          try {
            Thread.sleep(25);
          } catch (InterruptedException e) {
            return;
          }
          task = queue.peek();
        }

        if (task.world.disposed.get()) {
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

          generate = task.generateQueue.poll();
        }

        task.generationComplete.countDown();
        try {
          task.generationComplete.await();
        } catch (InterruptedException e) {
          return;
        }

        AreaReference features = task.featuresQueue.poll();
        while (features != null) {
          int status = WorldTasks.features(features, task.world);
          if (status == 1) { // done features
            task.featureCounter.incrementAndGet();
          }

          features = task.featuresQueue.poll();
        }

        if (queue.remove(task) && task.parameter.afterCompletion != null) {
          task.printStatistics();
          task.parameter.afterCompletion.run();
        }
      } catch (CubesException e) {
        if (e.className.equals(Side.class.getName())) {
          queue.clear();
        } else {
          throw e;
        }
      }
    }
  }
}
