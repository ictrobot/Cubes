package ethanjones.cubes.core.system;

import ethanjones.cubes.core.gwt.ExitException;
import ethanjones.cubes.core.gwt.FakeAtomic.AtomicInteger;
import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.platform.Adapter;
import ethanjones.cubes.core.platform.Compatibility;

public class Debug {

  public static class UncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    public static final UncaughtExceptionHandler instance = new UncaughtExceptionHandler();

    private UncaughtExceptionHandler() {

    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
      if (throwable instanceof ExitException) return;
      crash(throwable);
    }
  }

  private static final AtomicInteger crashed = new AtomicInteger(0);

  public static void printProperties() {
    if (Branding.VERSION_HASH != null && !Branding.VERSION_HASH.isEmpty()) {
      Log.debug("Hash:               " + Branding.VERSION_HASH);
    }
    Log.debug("Java Home:          " + System.getProperty("java.home"));
    Log.debug("Java Vendor:        " + System.getProperty("java.vendor"));
    Log.debug("Java Vendor URL:    " + System.getProperty("java.vendor.url"));
    Log.debug("Java Version:       " + System.getProperty("java.version"));
    Log.debug("OS Name:            " + System.getProperty("os.name"));
    Log.debug("OS Architecture:    " + System.getProperty("os.arch"));
    Log.debug("OS Version:         " + System.getProperty("os.version"));
    Log.debug("libGDX version:     " + com.badlogic.gdx.Version.VERSION);
    Log.debug("User Home:          " + System.getProperty("user.home"));
    Log.debug("Working Directory:  " + System.getProperty("user.dir"));
  }

  public static synchronized void lowMemory() {
    Log.warning("Low Memory! " + Compatibility.get().getFreeMemory() + "MB Free!");
  }

  public static synchronized void criticalMemory() {
    crash(new RuntimeException("Detected OOM! " + Compatibility.get().getFreeMemory() + "MB Free!"));
  }

  public static void crash(Throwable throwable) {
    synchronized (Debug.class) {
      if (crashed.getAndIncrement() == 0) {
        //Primary Crash
        logCrash(throwable);
        try {
          Adapter.dispose();
        } catch (Exception e) {
        }
        if (Compatibility.get().handleCrash(throwable)) {
          errorExit();
        }
      } else {
        //Secondary Crash
        if (crashed.get() > 10) {
          Log.error("Over 10 crashes");
          errorExit();
        } else {
          logCrash(throwable);
        }
      }
    }
  }

  private static synchronized void logCrash(Throwable throwable) {
    final int crashedNum = crashed.get();
    try {
      if (crashedNum == 1) {
        Log.error(throwable.getClass().getSimpleName() + " CRASH");
      } else {
        Log.error(throwable.getClass().getSimpleName() + " CRASH " + crashedNum);
      }
    } catch (Exception e) {

    }
    try {
      Log.error(throwable);
    } catch (Exception e) {
      throwable.printStackTrace();
    }
  }

  protected static void errorExit() {
    try {
      System.out.flush();
    } catch (Exception ignored) {
      
    }
    Compatibility.get()._exit(1);
  }
}
