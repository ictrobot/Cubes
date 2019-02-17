package ethanjones.cubes.core.util;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class RunnableQueue {

  private final ConcurrentLinkedQueue<Runnable> queueA = new ConcurrentLinkedQueue<>();
  private final ConcurrentLinkedQueue<Runnable> queueB = new ConcurrentLinkedQueue<>();
  private AtomicInteger which = new AtomicInteger();

  public void add(Runnable runnable) {
    if (which.get() % 2 == 0) {
      queueA.add(runnable);
    } else {
      queueB.add(runnable);
    }
  }

  public void runAll() {
    ConcurrentLinkedQueue<Runnable> q = which.getAndIncrement() % 2 == 0 ? queueA : queueB;
    Runnable runnable;
    while ((runnable = q.poll()) != null) {
      runnable.run();
    }
  }
}
