package ethanjones.modularworld.core;

public class ModularWorldException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public ModularWorldException() {
  }

  public ModularWorldException(String arg0) {
    super(getString(arg0));
  }

  public ModularWorldException(Throwable arg0) {
    super(getString(null, arg0.getStackTrace()), arg0);
  }

  public ModularWorldException(String arg0, Throwable arg1) {
    super(getString(arg0, arg1.getStackTrace()), arg1);
  }

  private static String getString(String suffix) {
    return getString(suffix, Thread.currentThread().getStackTrace());
  }

  private static String getString(String suffix, StackTraceElement[] stackTrace) {
    if (suffix == null) {
      suffix = "";
    }

    Class<?> c;
    try {
      c = Class.forName(stackTrace[3].getClassName());
      return c.getSimpleName() + "Exception: " + suffix;
    } catch (ClassNotFoundException e) {

    }

    return suffix;
  }

  public static ModularWorldException getModularWorldException(Exception e) {
    if (!(e instanceof ModularWorldException)) {
      e = new ModularWorldException(e);
    }
    return (ModularWorldException) e;
  }

}
