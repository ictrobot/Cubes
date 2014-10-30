package ethanjones.cubes.core.system;

import ethanjones.cubes.core.logging.Log;

public class CubesSecurity {

  public static void checkSidedSetup() {
    String c = getStackTrace()[1].getClassName();
    if (c.equals("ethanjones.cubes.side.common.Cubes") || c.equals("ethanjones.cubes.core.adapter.GraphicalAdapter")) {
      Log.debug("Allowing class \'" + c + "\' to change sided variables");
    } else {
      Log.error("Class \'" + c + "\' tried to change sided variables");
      throw new SecurityException("Cannot change sided variables");
    }
  }

  private static StackTraceElement[] getStackTrace() {
    StackTraceElement[] st = Thread.currentThread().getStackTrace();
    int pos = -1;
    for (int i = 0; i < st.length; i++) {
      if (st[i].getClassName().equals(CubesSecurity.class.getName())) {
        pos = i + 2; // + 2 removes "getStackTrace" and "check..."
        break;
      }
    }
    StackTraceElement[] stackTrace = new StackTraceElement[st.length - pos];
    System.arraycopy(st, pos, stackTrace, 0, stackTrace.length);
    return stackTrace;
  }

  public static void checkSidedReset() {
    String c = getStackTrace()[1].getClassName();
    if (c.equals("ethanjones.cubes.side.common.Cubes") || c.equals("ethanjones.cubes.core.adapter.GraphicalAdapter")) {
      Log.debug("Allowing class \'" + c + "\' to reset sided variables");
    } else {
      Log.error("Class \'" + c + "\' tried to reset sided variables");
      throw new SecurityException("Cannot reset sided variables");
    }
  }

  public static void checkSetMW() {
    String c = getStackTrace()[1].getClassName();
    if (c.equals("ethanjones.cubes.side.common.Cubes") || c.equals("ethanjones.cubes.core.adapter.GraphicalAdapter") || c.startsWith("ethanjones.cubes.graphics.menu.")) {
      Log.debug("Allowing class \'" + c + "\' to set the Cubes instances");
    } else {
      Log.error("Class \'" + c + "\' tried to set the Cubes instances");
      throw new SecurityException("Cannot set the Cubes instances");
    }
  }

  public static void checkSetMenu() {
    String c = getStackTrace()[1].getClassName();
    if (c.equals("ethanjones.cubes.side.common.Cubes") || c.equals("ethanjones.cubes.core.adapter.GraphicalAdapter") || c.startsWith("ethanjones.cubes.graphics.menu.")) {
      Log.debug("Allowing class \'" + c + "\' to set the menu");
    } else {
      Log.error("Class \'" + c + "\' tried to set the menu");
      throw new SecurityException("Cannot set the menu");
    }
  }
}
