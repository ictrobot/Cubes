package ethanjones.modularworld.core.system;

public class ModularWorldException extends RuntimeException {

  private static final long serialVersionUID = 1L;
  private final String c;

  public ModularWorldException(String arg0) {
    super(arg0);
    c = Thread.currentThread().getStackTrace()[2].getClassName();
  }

  public ModularWorldException(Throwable arg0) {
    super(arg0);
    c = Thread.currentThread().getStackTrace()[2].getClassName();
  }

  public ModularWorldException(String arg0, Throwable arg1) {
    super(arg0, arg1);
    c = Thread.currentThread().getStackTrace()[2].getClassName();
  }

  public static ModularWorldException getModularWorldException(Exception e) {
    if (!(e instanceof ModularWorldException)) {
      e = new ModularWorldException(e);
    }
    return (ModularWorldException) e;
  }

  public String toString() {
    return (getLocalizedMessage() != null) ? (c + ": " + getLocalizedMessage()) : c;
  }

}
