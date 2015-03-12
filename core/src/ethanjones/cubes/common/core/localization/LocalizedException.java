package ethanjones.cubes.common.core.localization;

public class LocalizedException extends Exception {

  public LocalizedException(String arg0, Object... format) {
    super(Localization.get(arg0, format));
  }

  public LocalizedException(Throwable arg0, Object... format) {
    super(arg0);
  }

  public LocalizedException(String arg0, Throwable arg1, Object... format) {
    super(Localization.get(arg0, format), arg1);
  }
  
}
