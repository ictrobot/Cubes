package ethanjones.cubes.core.settings;

import ethanjones.cubes.core.localization.Localization;
import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.platform.Compatibility;
import ethanjones.cubes.core.settings.type.*;
import ethanjones.cubes.graphics.world.ao.AmbientOcclusion;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonObject.Member;
import com.eclipsesource.json.WriterConfig;

import java.io.Reader;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map;

public class Settings {

  public static final String USERNAME = "username";
  public static final String UUID = "uuid";
  public static final String GRAPHICS_VIEW_DISTANCE = "graphics.viewDistance";
  public static final String GRAPHICS_FOV = "graphics.fieldOfView";
  public static final String GRAPHICS_VSYNC = "graphics.vsync";
  public static final String GRAPHICS_FOG = "graphics.fog";
  public static final String GRAPHICS_SCALE = "graphics.scale";
  public static final String GRAPHICS_AO = "graphics.ambientOcclusion";
  public static final String GRAPHICS_SIMPLE_SHADER = "graphics.simpleShader";
  public static final String INPUT_MOUSE_SENSITIVITY = "input.mouseSensitivity";
  public static final String INPUT_TOUCHPAD_SIZE = "input.touchpadSize";
  public static final String INPUT_TOUCHPAD_LEFT = "input.touchpadLeft";
  public static final String NETWORKING_PORT = "networking.port";
  public static final String DEBUG_FRAMETIME_GRAPH = "debug.frametimeGraph";
  public static final String DEBUG_GL_PROFILER = "debug.glProfiler";

  public static final String GROUP_GRAPHICS = "graphics";
  public static final String GROUP_INPUT = "input";
  public static final String GROUP_NETWORKING = "networking";
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
    addSetting(GRAPHICS_SCALE, new FloatSetting(1f, 0.5f, 2f, FloatSetting.Type.Slider) {
      {
        this.sliderSteps = 0.125f;
      }

      @Override
      public void onChange() {
        super.onChange();
        Gdx.app.getApplicationListener().resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
      }
    });
    addSetting(GRAPHICS_AO, AmbientOcclusion.getSetting());
    addSetting(GRAPHICS_SIMPLE_SHADER, new BooleanSetting(false) {
      @Override
      public boolean shouldDisplay() {
        return Compatibility.get().getApplicationType() == Application.ApplicationType.Desktop;
      }
    });

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

    addSetting(NETWORKING_PORT, new IntegerSetting(24842));

    addSetting(DEBUG_FRAMETIME_GRAPH, new BooleanSetting(false));
    addSetting(DEBUG_GL_PROFILER, new BooleanSetting(false));

    String keybindsGroup = Keybinds.KEYBIND_GROUP;
    SettingGroup keybinds = Keybinds.init();
  
    base.add(USERNAME)
            .add(GROUP_GRAPHICS, new SettingGroup().add(GRAPHICS_VIEW_DISTANCE).add(GRAPHICS_FOV).add(GRAPHICS_VSYNC).add(GRAPHICS_FOG).add(GRAPHICS_SCALE).add(GRAPHICS_AO).add(GRAPHICS_SIMPLE_SHADER))
            .add(GROUP_INPUT, new SettingGroup().add(keybindsGroup, keybinds).add(INPUT_MOUSE_SENSITIVITY).add(INPUT_TOUCHPAD_SIZE).add(INPUT_TOUCHPAD_LEFT))
            .add(GROUP_NETWORKING, new SettingGroup().add(NETWORKING_PORT))
            .add(GROUP_DEBUG, new SettingGroup().add(DEBUG_FRAMETIME_GRAPH).add(DEBUG_GL_PROFILER));

    if (!read()) {
      Log.info("Creating new settings file");
      write();
    }
  }

  public static boolean isSetup() {
    return base.getChildGroups().size() > 0 || base.getChildren().size() > 0;
  }

  public static void addSetting(String notLocalised, Setting setting) {
    settings.put(notLocalised, setting);
  }

  public static boolean read() {
    FileHandle fileHandle = Compatibility.get().getBaseFolder().child("settings.json");
    
    try {
      Reader reader = fileHandle.reader();
      JsonObject json = Json.parse(reader).asObject();
      reader.close();
  
      for (Member member : json) {
        Setting setting = settings.get(member.getName());
        setting.readJson(member.getValue());
      }
      
      return true;
    } catch (Exception e) {
      Log.error("Failed to read settings", e);
      fileHandle.delete();
      return false;
    }
  }

  public static boolean write() {
    FileHandle fileHandle = Compatibility.get().getBaseFolder().child("settings.json");
    JsonObject json = new JsonObject();
    for (Map.Entry<String, Setting> entry : settings.entrySet()) {
      json.set(entry.getKey(), entry.getValue().toJson());
    }
    try {
      Writer writer = fileHandle.writer(false);
      json.writeTo(writer, WriterConfig.PRETTY_PRINT);
      writer.close();
    } catch (Exception e) {
      Log.error("Failed to write settings", e);
      fileHandle.delete();
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
