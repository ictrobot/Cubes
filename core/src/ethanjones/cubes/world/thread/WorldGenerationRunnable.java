package ethanjones.cubes.world.thread;

import ethanjones.cubes.core.system.CubesException;
import ethanjones.cubes.side.Sided;
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

        AreaReference generate = task.generateQueue.poll();
        while (generate != null) {
          WorldTasks.generate(generate, task.world);

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
          WorldTasks.features(features, task.world);

          features = task.featuresQueue.poll();
        }

        if (queue.remove(task) && task.parameter.afterCompletion != null) {
          task.parameter.afterCompletion.run();
        }
      } catch (CubesException e) {
        if (e.className.equals(Sided.class.getName())) {
          queue.clear();
        } else {
          throw e;
        }
      }
    }
  }
}
