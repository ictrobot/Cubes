package ethanjones.cubes.core.system;

import com.badlogic.gdx.Version;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.logging.LogLevel;
import ethanjones.cubes.core.logging.loggers.FileLogWriter;
import ethanjones.cubes.core.mod.ModInstance;
import ethanjones.cubes.core.mod.ModManager;
import ethanjones.cubes.core.mod.ModState;
import ethanjones.cubes.core.platform.Adapter;
import ethanjones.cubes.core.platform.Compatibility;

public class Debug {

  public static class UncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    public static final UncaughtExceptionHandler instance = new UncaughtExceptionHandler();

    private UncaughtExceptionHandler() {

    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
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
    Log.debug("libGDX version      " + Version.VERSION);
    Log.debug("User Home:          " + System.getProperty("user.home"));
    Log.debug("Working Directory:  " + System.getProperty("user.dir"));
    Log.debug("Base Directory:     " + Compatibility.get().getBaseFolder().file().getAbsolutePath());
    if (FileLogWriter.file != null) Log.debug("Log file:           " + FileLogWriter.file.getAbsolutePath());
  }

  public static synchronized void lowMemory() {
    Log.warning("Low Memory! " + Compatibility.get().getFreeMemory() + "MB Free!");
  }

  public static synchronized void criticalMemory() {
    crash(new OutOfMemoryError("Detected"));
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
    if (crashedNum == 1) {
      try {
        printMods(LogLevel.error);
      } catch (Exception e) {

      }
    }
  }

  protected static void errorExit() {
    System.exit(1);
  }

  private static synchronized void printMods(LogLevel logLevel) {
    if (ModManager.getMods().size() > 0) {
      Log.log(logLevel, "Mods:");
      for (ModInstance modInstance : ModManager.getMods()) {
        StringBuilder builder = new StringBuilder();
        builder.append(modInstance.getName());
        List<ModState> modStates = modInstance.getModStates();
        if (modStates.size() > 0) builder.append(" - ");
        for (int i = 0; i < modStates.size(); i++) {
          builder.append(modStates.get(i).name());
          if (i != modStates.size() - 1) builder.append(" > ");
        }
        Log.log(logLevel, builder.toString());
      }
    } else {
      Log.log(logLevel, "No Mods");
    }
  }
}
