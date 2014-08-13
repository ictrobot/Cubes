package ethanjones.modularworld.core.debug;

import ethanjones.modularworld.core.logging.FileLogWriter;
import ethanjones.modularworld.core.logging.Log;
import ethanjones.modularworld.side.common.ModularWorld;

public class Debug {

  public static void printProperties() {
    Log.debug("Properties", "Java Home:          " + System.getProperty("java.home"));
    Log.debug("Properties", "Java Vendor:        " + System.getProperty("java.vendor"));
    Log.debug("Properties", "Java Vendor URL:    " + System.getProperty("java.vendor.url"));
    Log.debug("Properties", "Java Version:       " + System.getProperty("java.version"));
    Log.debug("Properties", "OS Name:            " + System.getProperty("os.name"));
    Log.debug("Properties", "OS Architecture:    " + System.getProperty("os.arch"));
    Log.debug("Properties", "OS Version:         " + System.getProperty("os.version"));
    Log.debug("Properties", "User Home:          " + System.getProperty("user.home"));
    Log.debug("Properties", "Working Directory:  " + System.getProperty("user.dir"));
    Log.debug("Properties", "Base Directory:     " + ModularWorld.baseFolder.file().getAbsolutePath());
    if (FileLogWriter.file != null)
      Log.debug("Properties", "Log file:           " + FileLogWriter.file.getAbsolutePath());
  }

}
