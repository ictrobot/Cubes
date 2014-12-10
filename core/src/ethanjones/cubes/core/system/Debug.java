package ethanjones.cubes.core.system;

import com.badlogic.gdx.Version;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

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

  private static AtomicBoolean crashed = new AtomicBoolean(false);

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
    Memory.update();
    Log.debug("Maximum Memory:     " + Memory.max + Memory.unit);
    Log.debug("libGDX version      " + Version.VERSION);
    Log.debug("User Home:          " + System.getProperty("user.home"));
    Log.debug("Working Directory:  " + System.getProperty("user.dir"));
    Log.debug("Base Directory:     " + Compatibility.get().getBaseFolder().file().getAbsolutePath());
    if (FileLogWriter.file != null) Log.debug("Log file:           " + FileLogWriter.file.getAbsolutePath());
  }

  public static synchronized void crash(Throwable throwable) {
    if (crashed.get()) System.exit(2);
    try {
      if (throwable instanceof OutOfMemoryError) {
        Log.error("Out of Memory CRASH!");
      } else {
        Log.error("CRASH");
      }
    } catch (Exception e) {

    }
    try {
      Log.error(throwable);
    } catch (Exception e) {
      throwable.printStackTrace();
    }
    try {
      printMods(LogLevel.error);
    } catch (Exception e) {

    }

    //Logging of crash finished, can now exit
    crashed.set(true);

    try {
      Adapter.getInterface().dispose();
    } catch (Exception e) {

    }

    errorExit();
  }

  public static synchronized void printMods(LogLevel logLevel) {
    if (ModManager.getMods().size() > 0) {
      Log.log(logLevel, "Mods:");
      for (ModInstance modInstance : ModManager.getMods()) {
        String str = modInstance.getModName();
        List<ModState> modStates = modInstance.getModStates();
        if (modStates.size() > 0) str = str + " - ";
        for (int i = 0; i < modStates.size(); i++) {
          str = str + modStates.get(i).name();
          if (i != modStates.size() - 1) str = str + " > ";
        }
        Log.log(logLevel, str);
      }
    } else {
      Log.log(logLevel, "No Mods");
    }
  }

  protected static void errorExit() {
    System.exit(1);
  }
}
