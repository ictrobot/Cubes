package ethanjones.modularworld.core.localization;

import java.util.HashMap;

public class Language {

  private final String code;
  private final HashMap<String, String> localization;

  public Language(final String code, final HashMap<String, String> localization) {
    this.code = code;
    this.localization = localization;
  }

  public String get(String str) {
    return localization.get(str.toLowerCase());
  }

  public String getCode() {
    return code;
  }
}
