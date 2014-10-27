package ethanjones.modularworld.core.settings;

import com.badlogic.gdx.files.FileHandle;
import ethanjones.data.Data;
import ethanjones.data.DataGroup;
import ethanjones.data.DataTools;
import ethanjones.modularworld.core.compatibility.Compatibility;
import ethanjones.modularworld.core.localization.Localization;
import ethanjones.modularworld.core.logging.Log;
import ethanjones.modularworld.core.settings.type.BooleanSetting;
import ethanjones.modularworld.core.settings.type.IntegerSetting;
import ethanjones.modularworld.core.settings.type.StringSetting;

import java.util.HashMap;
import java.util.Map;

public class Settings {

  public static final String USERNAME = "username";
  public static final String GRAPHICS_VIEW_DISTANCE = "graphics.viewDistance";
  public static final String GRAPHICS_FOV = "graphics.fieldOfView";
  public static final String NETWORKING_PORT = "networking.port";

  public static final String GROUP_GRAPHICS = "graphics";
  public static final String GROUP_NETWORKING = "networking";


  protected static SettingGroup base = new SettingGroup();
  protected static HashMap<String, Setting> settings = new HashMap<String, Setting>();

  public static void init() {
    addSetting(USERNAME, new StringSetting("User"));
    addSetting(GRAPHICS_VIEW_DISTANCE, new IntegerSetting(1, 1, 10, IntegerSetting.Type.Slider));
    addSetting(GRAPHICS_FOV, new IntegerSetting(67, 10, 120, IntegerSetting.Type.Slider));
    addSetting(NETWORKING_PORT, new IntegerSetting(8080));

    base
      .add(USERNAME)
      .add(GROUP_GRAPHICS, new SettingGroup()
          .add(GRAPHICS_VIEW_DISTANCE)
          .add(GRAPHICS_FOV)
      )
      .add(GROUP_NETWORKING, new SettingGroup()
          .add(NETWORKING_PORT)
      )
    ;

    read();

    for (Map.Entry<String, Setting> entry : settings.entrySet()) {
      Log.debug("Setting \"" + getLocalisedSettingName(entry.getKey()) + "\" = \"" + entry.getValue() + "\"");
    }
  }

  public static void addSetting(String notLocalised, Setting setting) {
    settings.put(notLocalised, setting);
  }

  public static Setting getSetting(String notLocalised) {
    return settings.get(notLocalised);
  }

  //Get casted
  public static BooleanSetting getBooleanSetting(String notLocalised) {
    return (BooleanSetting) getSetting(notLocalised);
  }

  public static IntegerSetting getIntegerSetting(String notLocalised) {
    return (IntegerSetting) getSetting(notLocalised);
  }

  public static StringSetting getStringSetting(String notLocalised) {
    return (StringSetting) getSetting(notLocalised);
  }

  //Get casted values
  public static boolean getBooleanSettingValue(String notLocalised) {
    return getBooleanSetting(notLocalised).get();
  }

  public static int getIntegerSettingValue(String notLocalised) {
    return getIntegerSetting(notLocalised).get();
  }

  public static String getStringSettingValue(String notLocalised) {
    return getStringSetting(notLocalised).get();
  }

  public static void write() {
    FileHandle fileHandle = Compatibility.get().getBaseFolder().child("settings.data");
    DataGroup dataGroup = new DataGroup();
    for (Map.Entry<String, Setting> entry : settings.entrySet()) {
      dataGroup.setGroup(entry.getKey(), entry.getValue().write());
    }
    try {
      DataTools.write(dataGroup, fileHandle.file());
    } catch (Exception e) {
      Log.error("Failed to write settings", e);
      fileHandle.delete();
    }
  }

  public static void read() {
    FileHandle fileHandle = Compatibility.get().getBaseFolder().child("settings.data");
    DataGroup dataGroup;
    try {
      dataGroup = (DataGroup) DataTools.read(fileHandle.file());
    } catch (Exception e) {
      Log.error("Failed to read settings", e);
      return;
    }
    for (Map.Entry<String, Data> entry : dataGroup.getEntrySet()) {
      Setting setting = settings.get(entry.getKey());
      if (setting != null && entry.getValue() instanceof DataGroup) {
        setting.read((DataGroup) entry.getValue());
      }
    }
  }

  public static String getLocalisedSettingName(String notLocalised) {
    return Localization.get("settings." + notLocalised);
  }

  public static String getLocalisedSettingGroupName(String notLocalised) {
    return Localization.get("settings." + notLocalised);
  }

  public static SettingGroup getBaseSettingGroup() {
    return base;
  }
}
