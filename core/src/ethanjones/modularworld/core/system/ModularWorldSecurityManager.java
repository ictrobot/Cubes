package ethanjones.modularworld.core.system;

import ethanjones.modularworld.core.logging.Log;
import ethanjones.modularworld.core.logging.LogLevel;

import java.security.Permission;

public class ModularWorldSecurityManager extends SecurityManager {

  public ModularWorldSecurityManager() {
    super();
  }

  private static void stackTrace(LogLevel logLevel, Class[] classes) {
    Log.log(logLevel, "Stack trace:");
    for (int i = 0; i < classes.length; i++) {
      Log.log(logLevel, "  " + classes[i].getName());
    }
  }

  @Override
  public void checkPermission(Permission perm) {
    if (getClassContext().length < 5) throw new SecurityException();
    String c = getClassContext()[4].getName();
    if (perm.getName().startsWith("exitVM")) {
      if (c.startsWith("com.badlogic.gdx.backends.lwjgl.Lwjgl") || c.equals("ethanjones.modularworld.side.common.ModularWorld") || c.equals("ethanjones.modularworld.core.system.Debug")) {
        Log.debug("Allowing class \'" + c + "\' to exit");
        stackTrace(LogLevel.debug, getClassContext());
      } else {
        Log.error("Class \'" + c + "\' tried to exit");
        stackTrace(LogLevel.error, getClassContext());
        throw new SecurityException("Cannot exit");
      }
    } else if ("setSecurityManager".equals(perm.getName())) {
      Log.error("Class \'" + c + "\' tried to set the security manager");
      stackTrace(LogLevel.error, getClassContext());
      throw new SecurityException("Cannot replace the security manager");
    } else if ("createSecurityManager".equals(perm.getName())) {
      Log.error("Class \'" + c + "\' tried to create a security manager");
      stackTrace(LogLevel.error, getClassContext());
      throw new SecurityException("Cannot create a security manager");
    }
  }
}
