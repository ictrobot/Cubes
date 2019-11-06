package ethanjones.cubes.core.platform.desktop;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.logging.LogLevel;
import ethanjones.cubes.side.common.Cubes;

import java.security.Permission;

public final class DesktopSecurityManager extends SecurityManager {

  static void setup() {
    try {
      System.setSecurityManager(new DesktopSecurityManager());
    } catch (SecurityException e) {
      System.out.println("Failed to set the Security Manager");
      if (System.getSecurityManager() != null) {
        System.out.println("Security Manager class: \"" + System.getSecurityManager().getClass() + "\"");
      }
      e.printStackTrace();
      System.exit(1);
    }
  }

  @Override
  public synchronized void checkPermission(Permission perm) {
    if (perm.getName().startsWith("exitVM.")) {
      if (!Cubes.cubesSetup()) return;
      String c = caller();
      if (c.equals("ethanjones.cubes.side.common.Cubes") || c.equals("ethanjones.cubes.core.system.Debug") || c.equals("ethanjones.cubes.core.platform.Adapter")) {
        Log.debug("Allowing class \"" + c + "\" to exit");
        stackTrace(LogLevel.debug);
      } else {
        Log.error("Class \"" + c + "\" tried to exit");
        stackTrace(LogLevel.error);
        throw new SecurityException("Cannot exit");
      }
    } else if ("setSecurityManager".equals(perm.getName())) {
      Log.error("Class \"" + caller() + "\" tried to set the security manager");
      stackTrace(LogLevel.error);
      throw new SecurityException("Cannot replace the security manager");
    } else if ("createSecurityManager".equals(perm.getName())) {
      Log.error("Class \"" + caller() + "\" tried to create a security manager");
      stackTrace(LogLevel.error);
      throw new SecurityException("Cannot create a security manager");
    }
  }

  private static void stackTrace(LogLevel logLevel) {
    Log.log(logLevel, "Stack trace:");
    StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
    for (int i = 3; i < stackTrace.length; i++) {
      StackTraceElement stackTraceElement = stackTrace[i];
      Log.log(logLevel, "  " + stackTraceElement.toString());
    }
  }

  private String caller() {
    Class[] classContext = getClassContext();
    Class caller = classContext[callerIndex(classContext)];
    return caller.getName();
  }

  private static int callerIndex(Class[] ctx) {
    boolean seenOwnClass = false;
    for (int i = 0; i < ctx.length; i++) {
      if (ctx[i] == DesktopSecurityManager.class) {
        seenOwnClass = true;
      } else if (seenOwnClass && !ctx[i].getName().startsWith("java.") && !ctx[i].getName().startsWith("com.badlogic.")) {
        return i;
      }
    }
    stackTrace(LogLevel.error);
    throw new SecurityException("Can't locate caller");
  }
}
