package ethanjones.modularworld.core.localization;

import ethanjones.modularworld.core.ModularWorldException;
import ethanjones.modularworld.core.logging.Log;
import ethanjones.modularworld.graphics.asset.AssetManager;

import java.util.HashMap;
import java.util.Locale;
import java.util.Scanner;

public class Localization {

  private static HashMap<String, Language> languages = new HashMap<String, Language>();
  private static Language language;

  private static Language load(String string) {
    try {
      HashMap<String, String> data = new HashMap<String, String>();
      Scanner scanner = new Scanner(string);
      String code = "";
      int i = 0;
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();
        if (line.startsWith("#")) continue;
        i++;
        if (i == 1) {
          code = line;
          continue;
        }
        int index = line.indexOf("=");
        if (index == -1) continue;
        String str = line.substring(0, index).toLowerCase();
        String localization = line.substring(index + 1).toLowerCase();
        data.put(str, localization);
      }
      scanner.close();
      return new Language(code, data);
    } catch (Exception e) {
      Log.error(new ModularWorldException("Failed to read language file: " + string, e));
    }
    return null;
  }

  public static void load(AssetManager.AssetFolder assetFolder) {
    AssetManager.AssetFolder languageFolder = assetFolder.folders.get("language");
    for (AssetManager.Asset asset : languageFolder.files.values()) {
      try {
        Language language = load(new String(asset.bytes));
        languages.put(language.getCode(), language);
      } catch (Exception e) {
        Log.error(new ModularWorldException("Failed to read language file: " + asset.path, e));
      }
    }
  }

  public static Language getLangauage() {
    if (language == null) {
      Language local = languages.get(Locale.getDefault().getLanguage() + "_" + Locale.getDefault().getCountry());
      if (local != null) {
        language = local;
      } else {
        language = languages.get("en_US");
      }
    }
    return language;
  }

  public static String get(String str) {
    String s = getLangauage().get(str);
    return s != null ? s : str;
  }
}
