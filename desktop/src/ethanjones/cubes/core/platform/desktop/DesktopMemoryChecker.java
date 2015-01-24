package ethanjones.cubes.core.platform.desktop;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

import ethanjones.cubes.core.system.Debug;

public final class DesktopMemoryChecker extends Thread {

  private static final int criticalMemoryThreshold = 50;
  private static final int lowMemoryThreshold = 100;

  public static void setup() {
    new DesktopMemoryChecker().start();
  }
  private final SoftReference<Object> outOfMemoryChecker = new SoftReference<Object>(new Integer[1024]);
  private WeakReference<Object> gcChecker = getGcChecker();
  private Runtime runtime = Runtime.getRuntime();

  public DesktopMemoryChecker() {
    setDaemon(true);
    setName(DesktopMemoryChecker.class.getSimpleName());
    setUncaughtExceptionHandler(Debug.UncaughtExceptionHandler.instance);
  }

  @Override
  public void run() {
    while (true) {
      if (outOfMemoryChecker.get() == null) {
        Debug.criticalMemory();
      }
      if (gcChecker.get() == null) { //GC has just run
        int free = getFree();
        if (free <= criticalMemoryThreshold) {
          Debug.criticalMemory();
        } else if (free <= lowMemoryThreshold) {
          Debug.lowMemory();
        }
        gcChecker = getGcChecker();
      }
      try {
        Thread.sleep(5);
      } catch (InterruptedException e) {

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
