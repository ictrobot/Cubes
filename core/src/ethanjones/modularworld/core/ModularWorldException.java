package ethanjones.modularworld.core;

public class ModularWorldException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public ModularWorldException() {
  }

  public ModularWorldException(String arg0) {
    super(getString(arg0));
  }

  public ModularWorldException(Throwable arg0) {
    super(getString(null), arg0);
  }

  public ModularWorldException(String arg0, Throwable arg1) {
    super(getString(arg0), arg1);
  }

  public ModularWorldException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
    super(getString(arg0), arg1, arg2, arg3);
  }

  private static String getString(String suffix) {
    if (suffix == null) {
      suffix = "";
    }

    StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

    Class<?> c;
    try {
      c = Class.forName(stackTrace[3].getClassName());
      return c.getSimpleName() + "Exception: " + suffix;
    } catch (ClassNotFoundException e) {

    }

    return suffix;
  }

}
