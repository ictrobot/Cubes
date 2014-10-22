package ethanjones.modularworld.core.system;

import ethanjones.modularworld.core.logging.Log;
import ethanjones.modularworld.core.logging.LogLevel;

import java.security.Permission;

public class ModularWorldSecurityManager extends SecurityManager {

  public ModularWorldSecurityManager() {
    super();
  }

  private static void stackTrace(LogLevel logLevel) {
    Log.log(logLevel, "Stack trace:");
    StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
    for (int i = 2; i < stackTrace.length; i++) {
      StackTraceElement stackTraceElement = stackTrace[i];
      Log.log(logLevel, "  " + stackTraceElement.toString());
    }
  }

  @Override
  public void checkPermission(Permission perm) {
    if (getClassContext().length < 5) throw new SecurityException();
    if (perm.getName().startsWith("mwSidedSetup.") || perm.getName().startsWith("mwSidedReset.")) {
      String c = getClassContext()[2].getName();
      if (c.equals("ethanjones.modularworld.side.common.ModularWorld") || c.equals("ethanjones.modularworld.core.adapter.GraphicalAdapter")) {
        Log.debug("Allowing class \'" + c + "\' to change sided variables");
        stackTrace(LogLevel.debug);
      } else {
        Log.error("Class \'" + c + "\' tried to change sided variables");
        stackTrace(LogLevel.error);
        throw new SecurityException("Cannot change sided variables");
      }
    } else if (perm.getName().startsWith("exitVM.")) {
      String c = getClassContext()[4].getName();
      if (c.startsWith("com.badlogic.gdx.backends.lwjgl.Lwjgl") || c.equals("ethanjones.modularworld.side.common.ModularWorld") || c.equals("ethanjones.modularworld.core.system.Debug")) {
        Log.debug("Allowing class \'" + c + "\' to exit");
        stackTrace(LogLevel.debug);
      } else {
        Log.error("Class \'" + c + "\' tried to exit");
        stackTrace(LogLevel.error);
        throw new SecurityException("Cannot exit");
      }
    } else if ("setSecurityManager".equals(perm.getName())) {
      String c = getClassContext()[3].getName();
      Log.error("Class \'" + c + "\' tried to set the security manager");
      stackTrace(LogLevel.error);
      throw new SecurityException("Cannot replace the security manager");
    } else if ("createSecurityManager".equals(perm.getName())) {
      String c = getClassContext()[2].getName();
      Log.error("Class \'" + c + "\' tried to create a security manager");
      stackTrace(LogLevel.error);
      throw new SecurityException("Cannot create a security manager");
    }
  }
}
