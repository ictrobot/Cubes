package ethanjones.cubes.core.localization;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.system.CubesException;
import ethanjones.cubes.graphics.assets.Asset;
import ethanjones.cubes.graphics.assets.AssetManager;
import ethanjones.cubes.graphics.assets.AssetType;
import ethanjones.cubes.graphics.assets.Assets;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

public class Localization {

  public static final String DEFAULT_LANGUAGE = "en_GB";

  private static HashMap<String, Language> languages = new HashMap<String, Language>();
  private static Language defaultLanguage;
  private static Language language;

  public static void load() {
    for (AssetManager assetManager : Assets.getAssetManagers()) {
      for (Asset asset : assetManager.getAssets(AssetType.language.name())) {
        String lang = asset.getPath().substring(AssetType.language.name().length() + 1);
        lang = lang.substring(0, lang.indexOf("/"));
        Language language = languages.get(lang);
        if (language == null) {
          language = new Language(lang);
          languages.put(lang, language);
        }
        try {
          load(language, asset.getFileHandle().readString());
          Log.debug("Loaded localisation file \"" + assetManager.getName() + ":" + asset.getPath() + "\"");
        } catch (Exception e) {
          Log.error(new CubesException("Failed to read localisation file: " + asset.getPath(), e));
        }
      }
    }

    defaultLanguage = languages.get(DEFAULT_LANGUAGE);
    if (defaultLanguage == null) {
      throw new CubesException("Failed to load the default language (" + DEFAULT_LANGUAGE + ")");
    }

    String local = Locale.getDefault().getLanguage() + "_" + Locale.getDefault().getCountry();
    language = languages.get(local);
    if (language == null) {
      Log.info("No localisation for " + local);
    } else {
      Log.info("Localisation loaded for " + local);
    }
  }

  private static void load(Language language, String string) throws Exception {
    for (String line : string.split("\n")) {
      line = line.trim();
      if (line.startsWith("#")) continue;
      int index = line.indexOf("=");
      if (index == -1) continue;
      String str = line.substring(0, index).toLowerCase();
      String localization = line.substring(index + 1).replace("\\n", "\n");
      language.add(str, localization);
    }
  }

  public static String get(String str, Object... format) {
    if (language != null) {
      String localised = language.get(str);
      if (localised != null) return format(localised, format);
    }
    String defaultLocalised = defaultLanguage.get(str);
    if (defaultLocalised != null) return format(defaultLocalised, format);
    return str;
  }

  public static String getFirstAvailable(String... strings) {
    for (String str : strings) {
      if (language != null) {
        String localised = language.get(str);
        if (localised != null) return localised;
      }
      String defaultLocalised = defaultLanguage.get(str);
      if (defaultLocalised != null) return defaultLocalised;
    }
    return strings[0];
  }

  private static String format(String str, Object[] format) {
    if (format.length == 0) return str;
    String f = Arrays.deepToString(format);
    return str + " " + f.substring(1, f.length() - 1);
  }

  public static void add(String lang, String unlocalized, String localized) {
    languages.get(lang).add(unlocalized, localized);
  }
}
