package ethanjones.cubes.core.platform.desktop;

import ethanjones.cubes.core.platform.Compatibility;
import ethanjones.cubes.core.system.Debug;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicBoolean;

public final class DesktopMemoryChecker extends Thread {

  private static final int criticalMemoryThreshold = 25;
  private static final int lowMemoryThreshold = 50;

  private static DesktopMemoryChecker INSTANCE = null;

  static void setup() {
    if (INSTANCE == null) {
      INSTANCE =  new DesktopMemoryChecker();
      INSTANCE.start();
    }
  }

  static void disable() {
    if (INSTANCE != null) INSTANCE.running.set(false);
  }

  public static boolean isRunning() {
    return INSTANCE != null && INSTANCE.isAlive();
  }

  private final SoftReference<Object> outOfMemoryChecker = new SoftReference<Object>(new Integer[1024]);
  private WeakReference<Object> gcChecker = getGcChecker();
  private Runtime runtime = Runtime.getRuntime();
  private AtomicBoolean running = new AtomicBoolean(true);

  private DesktopMemoryChecker() {
    setDaemon(true);
    setName(DesktopMemoryChecker.class.getSimpleName());
    setUncaughtExceptionHandler(Debug.UncaughtExceptionHandler.instance);
  }

  @Override
  public void run() {
    while (running.get()) {
      if (outOfMemoryChecker.get() == null) {
        Debug.lowMemory();
        if (Compatibility.get().getFreeMemory() <= criticalMemoryThreshold) Debug.criticalMemory();
      }
      if (gcChecker.get() == null) { //GC has just run
        int free = Compatibility.get().getFreeMemory();
        if (free <= criticalMemoryThreshold) {
          Debug.criticalMemory();
        } else if (free <= lowMemoryThreshold) {
          Debug.lowMemory();
        }
        gcChecker = getGcChecker();
      }
      try {
        Thread.sleep(50);
      } catch (InterruptedException ignored) {

      }
    }
  }

  private int getFree() {
    int max = (int) (runtime.maxMemory() / 1048576);
    int allocated = (int) (runtime.totalMemory() / 1048576);
    int free = (int) (runtime.freeMemory() / 1048576);
    return free + (max - allocated);
  }

  private WeakReference<Object> getGcChecker() {
    return new WeakReference<Object>(new Integer[1024]);
  }
}
