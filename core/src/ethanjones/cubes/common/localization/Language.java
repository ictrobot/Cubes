package ethanjones.cubes.common.localization;

import java.util.HashMap;

public class Language {

  private final String code;
  private final HashMap<String, String> localization;

  public Language(final String code) {
    this.code = code;
    this.localization = new HashMap<String, String>();
  }

  public String get(String str) {
    return localization.get(str.toLowerCase());
  }

  public void add(String unlocalized, String localized) {
    localization.put(unlocalized, localized);
  }

  public String getCode() {
    return code;
  }
}
