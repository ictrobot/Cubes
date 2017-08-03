package ethanjones.cubes.core.settings;

import ethanjones.cubes.core.localization.Localization;
import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.platform.Compatibility;
import ethanjones.cubes.core.settings.type.*;
import ethanjones.cubes.graphics.world.ao.AmbientOcclusion;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonObject.Member;

import java.util.LinkedHashMap;
import java.util.Map;

public class Settings {

  public static final String USERNAME = "username";
  public static final String UUID = "uuid";
  public static final String GRAPHICS_VIEW_DISTANCE = "graphics.viewDistance";
  public static final String GRAPHICS_FOV = "graphics.fieldOfView";
  public static final String GRAPHICS_VSYNC = "graphics.vsync";
  public static final String GRAPHICS_FOG = "graphics.fog";
  public static final String GRAPHICS_AO = "graphics.ambientOcclusion";
  public static final String INPUT_MOUSE_SENSITIVITY = "input.mouseSensitivity";
  public static final String INPUT_TOUCHPAD_SIZE = "input.touchpadSize";
  public static final String INPUT_TOUCHPAD_LEFT = "input.touchpadLeft";
  public static final String INPUT_TOUCH = "input.touch";
  public static final String DEBUG_FRAMETIME_GRAPH = "debug.frametimeGraph";

  public static final String GROUP_GRAPHICS = "graphics";
  public static final String GROUP_INPUT = "input";
  public static final String GROUP_DEBUG = "debug";

  protected static SettingGroup base = new SettingGroup();
  protected static LinkedHashMap<String, Setting> settings = new LinkedHashMap<String, Setting>();

  public static void init() {
    addSetting(USERNAME, new StringSetting("User"));
    addSetting(UUID, new PlayerUUIDSetting());
    addSetting(GRAPHICS_VIEW_DISTANCE, new IntegerSetting(2, 2, 16, IntegerSetting.Type.Slider));
    addSetting(GRAPHICS_FOV, new IntegerSetting(70, 10, 120, IntegerSetting.Type.Slider));
    addSetting(GRAPHICS_VSYNC, new BooleanSetting(false) {
  
      @Override
      public void onChange() {
        super.onChange();
        Gdx.graphics.setVSync(get());
      }
  
      @Override
      public boolean shouldDisplay() {
        return Compatibility.get().getApplicationType() == Application.ApplicationType.Desktop;
      }
    });
    addSetting(GRAPHICS_FOG, new BooleanSetting(true));
    addSetting(GRAPHICS_AO, AmbientOcclusion.getSetting());

    addSetting(INPUT_TOUCH, new BooleanSetting(Compatibility.get().guessTouchScreen()));
    addSetting(INPUT_MOUSE_SENSITIVITY, new FloatSetting(0.5f, 0.05f, 1f, FloatSetting.Type.Slider));
    addSetting(INPUT_TOUCHPAD_SIZE, new FloatSetting(0.45f, 0.30f, 0.60f, FloatSetting.Type.Slider) {
      {
        this.sliderSteps = 0.005f;
      }

      @Override
      public boolean shouldDisplay() {
        return Compatibility.get().isTouchScreen();
      }
    });
    addSetting(INPUT_TOUCHPAD_LEFT, new BooleanSetting(false) {
      @Override
      public boolean shouldDisplay() {
        return Compatibility.get().isTouchScreen();
      }
    });
    addSetting(DEBUG_FRAMETIME_GRAPH, new BooleanSetting(false));
  
    String keybindsGroup = Keybinds.KEYBIND_GROUP;
    SettingGroup keybinds = Keybinds.init();
  
    base.add(USERNAME)
            .add(GROUP_GRAPHICS, new SettingGroup().add(GRAPHICS_VIEW_DISTANCE).add(GRAPHICS_FOV).add(GRAPHICS_VSYNC).add(GRAPHICS_FOG).add(GRAPHICS_AO))
            .add(GROUP_INPUT, new SettingGroup().add(keybindsGroup, keybinds).add(INPUT_TOUCH).add(INPUT_MOUSE_SENSITIVITY).add(INPUT_TOUCHPAD_SIZE).add(INPUT_TOUCHPAD_LEFT))
            .add(GROUP_DEBUG, new SettingGroup().add(DEBUG_FRAMETIME_GRAPH));

    if (!read()) {
      Log.info("Creating new settings file");
      write();
    }
  }

  public static void addSetting(String notLocalised, Setting setting) {
    settings.put(notLocalised, setting);
  }

  public static boolean read() {
    Preferences preferences = Gdx.app.getPreferences("ethanjones-cubes-settings");
    String jsonStr = preferences.getString("settings", "");
    if (jsonStr == null || jsonStr.isEmpty()) return false;

    try {
      JsonObject json = Json.parse(jsonStr).asObject();

      for (Member member : json) {
        Setting setting = Settings.settings.get(member.getName());
        setting.readJson(member.getValue());
      }
      
      return true;
    } catch (Exception e) {
      Log.error("Failed to read settings", e);
      return false;
    }
  }

  public static boolean write() {
    Preferences preferences = Gdx.app.getPreferences("ethanjones-cubes-settings");
    JsonObject json = new JsonObject();
    for (Map.Entry<String, Setting> entry : settings.entrySet()) {
      json.set(entry.getKey(), entry.getValue().toJson());
    }
    try {
      preferences.putString("settings", json.toString());
      preferences.flush();
    } catch (Exception e) {
      Log.error("Failed to write settings", e);
      return false;
    }
    return true;
  }

  public static void print() {
    for (Map.Entry<String, Setting> entry : settings.entrySet()) {
      Log.debug("Setting \"" + getLocalisedSettingName(entry.getKey()) + "\" = \"" + entry.getValue() + "\"");
    }
  }

  public static String getLocalisedSettingName(String notLocalised) {
    return Localization.get("setting." + notLocalised);
  }

  //Get casted values
  public static boolean getBooleanSettingValue(String notLocalised) {
    return getBooleanSetting(notLocalised).get();
  }

  //Get casted
  public static BooleanSetting getBooleanSetting(String notLocalised) {
    return (BooleanSetting) getSetting(notLocalised);
  }

  public static Setting getSetting(String notLocalised) {
    return settings.get(notLocalised);
  }

  public static int getIntegerSettingValue(String notLocalised) {
    return getIntegerSetting(notLocalised).get();
  }

  public static IntegerSetting getIntegerSetting(String notLocalised) {
    return (IntegerSetting) getSetting(notLocalised);
  }

  public static float getFloatSettingValue(String notLocalised) {
    return getFloatSetting(notLocalised).get();
  }

  public static FloatSetting getFloatSetting(String notLocalised) {
    return (FloatSetting) getSetting(notLocalised);
  }

  public static String getStringSettingValue(String notLocalised) {
    return getStringSetting(notLocalised).get();
  }

  public static StringSetting getStringSetting(String notLocalised) {
    return (StringSetting) getSetting(notLocalised);
  }

  public static String getLocalisedSettingGroupName(String notLocalised) {
    return Localization.get("setting." + notLocalised);
  }

  public static SettingGroup getBaseSettingGroup() {
    return base;
  }
}
