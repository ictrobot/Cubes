package ethanjones.cubes.core.system;

import ethanjones.cubes.core.compatibility.Compatibility;
import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.logging.loggers.FileLogWriter;
import ethanjones.cubes.side.client.CubesClient;
import ethanjones.cubes.side.server.CubesServer;

public class Debug {

  public static class UncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    @Override
    public void uncaughtException(Thread t, Throwable e) {
      crash(e);
    }
  }

  public static void printProperties() {
    Log.debug("Java Home:          " + System.getProperty("java.home"));
    Log.debug("Java Vendor:        " + System.getProperty("java.vendor"));
    Log.debug("Java Vendor URL:    " + System.getProperty("java.vendor.url"));
    Log.debug("Java Version:       " + System.getProperty("java.version"));
    Log.debug("OS Name:            " + System.getProperty("os.name"));
    Log.debug("OS Architecture:    " + System.getProperty("os.arch"));
    Log.debug("OS Version:         " + System.getProperty("os.version"));
    Log.debug("User Home:          " + System.getProperty("user.home"));
    Log.debug("Working Directory:  " + System.getProperty("user.dir"));
    Log.debug("Base Directory:     " + Compatibility.get().getBaseFolder().file().getAbsolutePath());
    if (FileLogWriter.file != null) Log.debug("Log file:           " + FileLogWriter.file.getAbsolutePath());
  }

  public static synchronized void crash(Throwable throwable) {
    try {
      Log.error("CRASH");
    } catch (Exception e) {

    }
    try {
      Log.error(throwable);
    } catch (Exception e) {
      throwable.printStackTrace();
    }
    try {
      if (CubesClient.instance != null) CubesClient.instance.dispose();
    } catch (Exception e) {

    }
    try {
      if (CubesServer.instance != null) CubesServer.instance.dispose();
    } catch (Exception e) {

    }
    System.exit(1);
  }
}
