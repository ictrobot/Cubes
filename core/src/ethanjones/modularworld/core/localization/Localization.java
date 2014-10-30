package ethanjones.modularworld.core.localization;

import ethanjones.modularworld.core.logging.Log;
import ethanjones.modularworld.core.system.ModularWorldException;
import ethanjones.modularworld.graphics.assets.Asset;
import ethanjones.modularworld.graphics.assets.AssetType;
import ethanjones.modularworld.graphics.assets.Assets;

import java.util.HashMap;
import java.util.Locale;
import java.util.Scanner;

public class Localization {

  public static final String DEFAULT_LANGUAGE = "en_GB";

  private static HashMap<String, Language> languages = new HashMap<String, Language>();
  private static Language defaultLanguage;
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
        String localization = line.substring(index + 1);
        data.put(str, localization);
      }
      scanner.close();
      return new Language(code, data);
    } catch (Exception e) {
      Log.error(new ModularWorldException("Failed to read language file: " + string, e));
    }
    return null;
  }

  public static void load() {
    for (Asset asset : Assets.getCoreAssetManager().getAssets(AssetType.language.name())) {
      try {
        language = load(asset.getFileHandle().readString());
        languages.put(language.getCode(), language);
        Log.debug("Loaded localisation \"" + language.getCode() + "\" from file \"" + asset.getPath() + "\"");
      } catch (Exception e) {
        Log.error(new ModularWorldException("Failed to read localisation file: " + asset.getPath(), e));
      }
    }

    defaultLanguage = languages.get(DEFAULT_LANGUAGE);
    if (defaultLanguage == null)
      throw new ModularWorldException("Failed to load the default language (" + DEFAULT_LANGUAGE + ")");

    String local = Locale.getDefault().getLanguage() + "_" + Locale.getDefault().getCountry();
    language = languages.get(local);
    if (language == null) {
      Log.info("No localisation for " + local);
    } else {
      Log.info("Localisation loaded for " + local);
    }
  }


  public static String get(String str) {
    if (language != null) {
      String localised = language.get(str);
      if (localised != null) return localised;
    }
    String defaultLocalised = defaultLanguage.get(str);
    if (defaultLocalised != null) return defaultLocalised;
    return str;
  }
}
