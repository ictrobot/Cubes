package ethanjones.cubes.core.system;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

import ethanjones.cubes.side.Side;
import ethanjones.cubes.side.Sided;

public class Executor {

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

  private static boolean running = false;
  private static ScheduledThreadPoolExecutor executor;
  private final static Object sync = new Object();

  public static synchronized <T> Future<T> execute(Callable<T> task) {
    synchronized (sync) {
      if (!running) start();
      return executor.submit(new Wrapper<T>(Sided.getSide(), task));
    }
  }

  public static synchronized void stop() {
    synchronized (sync) {
      executor.shutdownNow();
      running = false;
    }
  }

  private static synchronized void start() {
    synchronized (sync) {
      executor = new ScheduledThreadPoolExecutor(4, new ThreadFactory() {
        int threads = 0;

        public Thread newThread(Runnable r) {
          Thread t = new Thread(r);
          t.setName("Executor-" + threads++);
          t.setDaemon(true);
          return t;
        }
      });
      running = true;
    }
  }

}
