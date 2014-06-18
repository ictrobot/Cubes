package ethanjones.modularworld.core.exception;

public class CustomException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public CustomException() {
  }

  public CustomException(String arg0) {
    super(getString(arg0));
  }

  public CustomException(Throwable arg0) {
    super(getString(null), arg0);
  }

  public CustomException(String arg0, Throwable arg1) {
    super(getString(arg0), arg1);
  }

  public CustomException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
    super(getString(arg0), arg1, arg2, arg3);
  }

  public static String getString(String suffix) {
    if (suffix == null) {
      suffix = "";
    }

    StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

    Class<?> c;
    try {
      c = Class.forName(stackTrace[3].getClassName());
      return c.getSimpleName() + ": " + suffix;
    } catch (ClassNotFoundException e) {

    }

    return suffix;
  }

}
