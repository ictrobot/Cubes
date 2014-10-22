package ethanjones.modularworld.core.system;

import ethanjones.modularworld.core.compatibility.Compatibility;
import ethanjones.modularworld.core.logging.Log;
import ethanjones.modularworld.core.logging.loggers.FileLogWriter;
import ethanjones.modularworld.side.client.ModularWorldClient;
import ethanjones.modularworld.side.server.ModularWorldServer;

public class Debug {

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
    if (FileLogWriter.file != null)
      Log.debug("Log file:           " + FileLogWriter.file.getAbsolutePath());
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
      if (ModularWorldClient.instance != null) ModularWorldClient.instance.dispose();
    } catch (Exception e) {

    }
    try {
      if (ModularWorldServer.instance != null) ModularWorldServer.instance.dispose();
    } catch (Exception e) {

    }
    System.exit(1);
  }

  public static class UncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    @Override
    public void uncaughtException(Thread t, Throwable e) {
      crash(e);
    }
  }
}
