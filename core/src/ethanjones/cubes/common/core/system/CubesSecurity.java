package ethanjones.cubes.common.core.system;

import ethanjones.cubes.common.core.logging.Log;

public class CubesSecurity {

  public static void checkSidedSetup() {
    String c = getStackTrace()[1].getClassName();
    if (c.equals("ethanjones.cubes.Cubes") || c.equals("ethanjones.cubes.platform.GraphicalAdapter")) {
      Log.debug("Allowing class \"" + c + "\" to change sided variables");
    } else {
      Log.error("Class \"" + c + "\" tried to change sided variables");
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
    if (c.equals("ethanjones.cubes.Cubes") || c.equals("ethanjones.cubes.platform.GraphicalAdapter")) {
      Log.debug("Allowing class \"" + c + "\" to reset sided variables");
    } else {
      Log.error("Class \"" + c + "\" tried to reset sided variables");
      throw new SecurityException("Cannot reset sided variables");
    }
  }
}
