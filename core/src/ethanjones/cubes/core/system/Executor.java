package ethanjones.cubes.core.system;

import java.util.ArrayList;
import java.util.concurrent.*;

import ethanjones.cubes.side.Side;
import ethanjones.cubes.side.Sided;
import ethanjones.cubes.side.common.Cubes;

public class Executor {

  private static class ExecutorException extends RuntimeException {

    public ExecutorException(Exception e) {
      super("Exception thrown in executor thread", e);
    }
  }

  private static class CallableWrapper<T> implements Callable<T> {

    private final Side side;
    private final Cubes cubes;
    private final Callable<T> callable;

    public CallableWrapper(Callable<T> callable) {
      this.side = Sided.getSide();
      this.cubes = getCubes(side);
      this.callable = callable;
    }

    @Override
    public T call() throws Exception {
      if (shouldReturn(cubes)) return null;
      try {
        Sided.setSide(side);
        T t = callable.call();
        Sided.setSide(null);
        return t;
      } catch (Exception e) {
        Debug.crash(new ExecutorException(e));
        return null;
      }
    }
  }

  private static class RunnableWrapper implements Runnable {

    private final Side side;
    private final Cubes cubes;
    private final Runnable runnable;

    public RunnableWrapper(Runnable runnable) {
      this.side = Sided.getSide();
      this.cubes = getCubes(side);
      this.runnable = runnable;
    }

    @Override
    public void run() {
      if (shouldReturn(cubes)) return;
      try {
        Sided.setSide(side);
        runnable.run();
        Sided.setSide(null);
      } catch (Exception e) {
        Debug.crash(new ExecutorException(e));
      }
    }
  }

  private static boolean running = false;
  private static ScheduledThreadPoolExecutor executor;
  private static ArrayList<ScheduledFuture> scheduled = new ArrayList<ScheduledFuture>();
  private final static Object sync = new Object();

  public static synchronized <T> Future<T> execute(Callable<T> callable) {
    synchronized (sync) {
      if (!running) start();
      return executor.submit(new CallableWrapper<T>(callable));
    }
  }

  public static synchronized Future execute(Runnable runnable) {
    synchronized (sync) {
      if (!running) start();
      return executor.submit(new RunnableWrapper(runnable));
    }
  }

  public static synchronized <T> Future<T> schedule(Callable<T> callable, long delay, TimeUnit timeUnit) {
    synchronized (sync) {
      if (!running) start();
      ScheduledFuture<T> schedule = executor.schedule(new CallableWrapper<T>(callable), delay, timeUnit);
      scheduled.add(schedule);
      return schedule;
    }
  }

  public static synchronized Future schedule(Runnable runnable, long delay, TimeUnit timeUnit) {
    synchronized (sync) {
      if (!running) start();
      ScheduledFuture<?> schedule = executor.schedule(new RunnableWrapper(runnable), delay, timeUnit);
      scheduled.add(schedule);
      return schedule;
    }
  }

  public static synchronized Future scheduleAtFixedRate(Runnable runnable, long initialDelay, long period, TimeUnit unit) {
    synchronized (sync) {
      if (!running) start();
      ScheduledFuture<?> schedule = executor.scheduleAtFixedRate(new RunnableWrapper(runnable), initialDelay, period, unit);
      scheduled.add(schedule);
      return schedule;
    }
  }

  public static synchronized Future scheduleWithFixedDelay(Runnable runnable, long initialDelay, long period, TimeUnit unit) {
    synchronized (sync) {
      if (!running) start();
      ScheduledFuture<?> schedule = executor.scheduleWithFixedDelay(new RunnableWrapper(runnable), initialDelay, period, unit);
      scheduled.add(schedule);
      return schedule;
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
      executor.prestartAllCoreThreads();
    }
  }

  private static Cubes getCubes(Side side) {
    switch (side) {
      case Client:
        return Cubes.getClient();
      case Server:
        return Cubes.getServer();
      default:
        return null;
    }
  }

  private static boolean shouldReturn(Cubes cubes) {
    return cubes != null && !cubes.isRunning();
  }

}
