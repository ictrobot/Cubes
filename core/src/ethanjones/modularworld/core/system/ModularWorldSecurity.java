package ethanjones.modularworld.core.system;

import ethanjones.modularworld.core.logging.Log;

public class ModularWorldSecurity {

  private static StackTraceElement[] getStackTrace() {
    StackTraceElement[] st = Thread.currentThread().getStackTrace();
    StackTraceElement[] stackTrace = new StackTraceElement[st.length - 3];
    System.arraycopy(st, 3, stackTrace, 0, stackTrace.length);
    return stackTrace;
  }

  public static void checkSidedSetup() {
    String c = getStackTrace()[1].getClassName();
    if (c.equals("ethanjones.modularworld.side.common.ModularWorld") || c.equals("ethanjones.modularworld.core.adapter.GraphicalAdapter")) {
      Log.debug("Allowing class \'" + c + "\' to change sided variables");
    } else {
      Log.error("Class \'" + c + "\' tried to change sided variables");
      throw new SecurityException("Cannot change sided variables");
    }
  }

  public static void checkSidedReset() {
    String c = getStackTrace()[1].getClassName();
    if (c.equals("ethanjones.modularworld.side.common.ModularWorld") || c.equals("ethanjones.modularworld.core.adapter.GraphicalAdapter")) {
      Log.debug("Allowing class \'" + c + "\' to reset sided variables");
    } else {
      Log.error("Class \'" + c + "\' tried to reset sided variables");
      throw new SecurityException("Cannot reset sided variables");
    }
  }

  public static void checkSetMW() {
    String c = getStackTrace()[1].getClassName();
    if (c.equals("ethanjones.modularworld.side.common.ModularWorld") || c.equals("ethanjones.modularworld.core.adapter.GraphicalAdapter") || c.startsWith("ethanjones.modularworld.graphics.menu.")) {
      Log.debug("Allowing class \'" + c + "\' to set the ModularWorld instances");
    } else {
      Log.error("Class \'" + c + "\' tried to set the ModularWorld instances");
      throw new SecurityException("Cannot set the ModularWorld instances");
    }
  }

  public static void checkSetMenu() {
    String c = getStackTrace()[1].getClassName();
    if (c.equals("ethanjones.modularworld.side.common.ModularWorld") || c.equals("ethanjones.modularworld.core.adapter.GraphicalAdapter") || c.startsWith("ethanjones.modularworld.graphics.menu.")) {
      Log.debug("Allowing class \'" + c + "\' to set the menu");
    } else {
      Log.error("Class \'" + c + "\' tried to set the menu");
      throw new SecurityException("Cannot set the menu");
    }
  }
}
