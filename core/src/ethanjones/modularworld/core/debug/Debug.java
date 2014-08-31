package ethanjones.modularworld.core.debug;

import ethanjones.modularworld.core.logging.Log;
import ethanjones.modularworld.core.logging.loggers.FileLogWriter;
import ethanjones.modularworld.side.common.ModularWorld;

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
    Log.debug("Base Directory:     " + ModularWorld.baseFolder.file().getAbsolutePath());
    if (FileLogWriter.file != null)
      Log.debug("Log file:           " + FileLogWriter.file.getAbsolutePath());
  }

}
