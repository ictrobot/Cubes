package ethanjones.modularworld.core.system;

import ethanjones.modularworld.side.Side;
import ethanjones.modularworld.side.Sided;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

public class Threads {

  private static ScheduledThreadPoolExecutor executor;

  public static void init() {
    executor = new ScheduledThreadPoolExecutor(8, new ThreadFactory() {
      int threads = 0;

      public Thread newThread(Runnable r) {
        Thread t = new Thread(r);
        t.setName("Executor-" + threads++);
        t.setDaemon(true);
        return t;
      }
    });
  }

  public static synchronized <T> Future<T> execute(Callable<T> task) {
    return executor.submit(new Wrapper(Sided.getSide(), task));
  }

  public static synchronized void disposeExecutor() {
    executor.shutdownNow();
  }

  private static class Wrapper<T> implements Callable<T> {

    private final Side side;
    private final Callable<T> callable;

    public Wrapper(Side side, Callable<T> callable) {
      this.side = side;
      this.callable = callable;
    }

    @Override
    public T call() throws Exception {
      Sided.setSide(side);
      T t = callable.call();
      Sided.setSide(null);
      return t;
    }
  }
}
