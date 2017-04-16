package ethanjones.cubes.core.system;

public class CubesException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public static CubesException get(Exception e) {
    if (!(e instanceof CubesException)) {
      e = new CubesException(e);
    }
    return (CubesException) e;
  }

  public CubesException(String arg0) {
    super(arg0);
  }

  public CubesException(Throwable arg0) {
    super(arg0);
  }

  public CubesException(String arg0, Throwable arg1) {
    super(arg0, arg1);
  }

}
