package ethanjones.cubes.core.system;

import com.badlogic.gdx.Gdx;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

import ethanjones.cubes.core.logging.Log;

import static ethanjones.cubes.core.system.Memory.*;

public final class MemoryChecker extends Thread {

  private final SoftReference<Object> outOfMemoryChecker = new SoftReference<Object>(new Integer[1024]);
  private WeakReference<Object> gcChecker = getGcChecker();
  private boolean warned = false;

  private static final int freeMemoryAlert = max > 1000 ? 100 : max / 10;

  public MemoryChecker() {
    setDaemon(true);
    setName(MemoryChecker.class.getSimpleName());
    setUncaughtExceptionHandler(Debug.UncaughtExceptionHandler.instance);
    setPriority(Thread.MIN_PRIORITY);
  }

  @Override
  public void run() {
    while (true) {
      if (outOfMemoryChecker.get() == null) {
        Log.error("Out Of Memory!");
        Debug.errorExit();
      }
      if (gcChecker.get() == null && !warned) { //GC has just run
        update();
        if (totalFree < freeMemoryAlert) {
          Log.error(totalFree + unit + " Memory Free!");
          warned = true;
          Gdx.app.exit();
        } else {
          gcChecker = getGcChecker();
        }
      }

      try {
        Thread.sleep(5);
      } catch (InterruptedException e) {

      }
    }
  }

  private WeakReference<Object> getGcChecker() {
    return new WeakReference<Object>(new Integer[1024]);
  }

  public static void init() {
    new MemoryChecker().start();
  }
}
