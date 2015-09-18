package ethanjones.cubes.world.thread;

import ethanjones.cubes.core.system.CubesException;
import ethanjones.cubes.side.Sided;
import ethanjones.cubes.world.reference.AreaReference;

import java.util.concurrent.LinkedBlockingQueue;

public class WorldGenerationThread implements Runnable {
  public LinkedBlockingQueue<WorldGenerationTask> queue = new LinkedBlockingQueue<WorldGenerationTask>();

  @Override
  public void run() {
    while (!Thread.interrupted()) {
      try {
        WorldGenerationTask task = queue.peek();
        while (task == null) {
          try {
            Thread.sleep(5);
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
          if (generate.areaX == 4 && generate.areaZ == -2)
            System.out.println("G");
          WorldTasks.generate(generate, task.world);

          generate = task.generateQueue.poll();
        }

        AreaReference features = task.featuresQueue.poll();
        while (features != null) {
          WorldTasks.features(features, task.world);

          features = task.featuresQueue.poll();
        }

        queue.remove(task);
      } catch (CubesException e) {
        if (e.className.equals(Sided.class.getName())) {
          while (!queue.isEmpty()) {
            queue.poll();
          }
        } else {
          throw e;
        }
      }
    }
  }
}
