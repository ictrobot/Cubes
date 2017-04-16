package ethanjones.cubes.core.settings;

import ethanjones.cubes.core.platform.Compatibility;
import ethanjones.cubes.core.settings.type.KeybindSetting;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

public class Keybinds {
  
  public static final String KEYBIND_GROUP = "input.keybind";
  public static final String KEYBIND_BASE = "input.keybind.";
  
  public static final String KEYBIND_FORWARD = KEYBIND_BASE + "forward";
  public static final String KEYBIND_BACK = KEYBIND_BASE + "back";
  public static final String KEYBIND_LEFT = KEYBIND_BASE + "left";
  public static final String KEYBIND_RIGHT = KEYBIND_BASE + "right";
  public static final String KEYBIND_JUMP = KEYBIND_BASE + "jump";
  public static final String KEYBIND_DESCEND = KEYBIND_BASE + "descend";
  
  public static final String KEYBIND_THROW = KEYBIND_BASE + "throw";
  public static final String KEYBIND_INVENTORY = KEYBIND_BASE + "inventory";
  public static final String KEYBIND_CHAT = KEYBIND_BASE + "chat";
  
  public static final String KEYBIND_FULLSCREEN = KEYBIND_BASE + "fullscreen";
  public static final String KEYBIND_HIDEGUI = KEYBIND_BASE + "hidegui";
  public static final String KEYBIND_DEBUG = KEYBIND_BASE + "debug";
  public static final String KEYBIND_AREABOUNDARIES = KEYBIND_BASE + "areaboundaries";
  
  private static SettingGroup settingGroup;
  
  public static SettingGroup init() {
    if (settingGroup == null) {
      Settings.addSetting(KEYBIND_FORWARD, new KeybindSetting(Keys.W));
      Settings.addSetting(KEYBIND_BACK, new KeybindSetting(Keys.S));
      Settings.addSetting(KEYBIND_LEFT, new KeybindSetting(Keys.A));
      Settings.addSetting(KEYBIND_RIGHT, new KeybindSetting(Keys.D));
      Settings.addSetting(KEYBIND_JUMP, new KeybindSetting(Keys.SPACE));
      Settings.addSetting(KEYBIND_DESCEND, new KeybindSetting(Keys.SHIFT_LEFT));
      
      Settings.addSetting(KEYBIND_THROW, new KeybindSetting(Keys.Q));
      Settings.addSetting(KEYBIND_INVENTORY, new KeybindSetting(Keys.E));
      Settings.addSetting(KEYBIND_CHAT, new KeybindSetting(Keys.F4));
      
      Settings.addSetting(KEYBIND_FULLSCREEN, new KeybindSetting(Keys.F11));
      Settings.addSetting(KEYBIND_HIDEGUI, new KeybindSetting(Keys.F1));
      Settings.addSetting(KEYBIND_DEBUG, new KeybindSetting(Keys.F3));
      Settings.addSetting(KEYBIND_AREABOUNDARIES, new KeybindSetting(Keys.F7));
      
      settingGroup = new SettingGroup() {
        @Override
        public boolean shouldDisplay() {
          return Compatibility.get().getApplicationType() == ApplicationType.Desktop;
        }
      };
      settingGroup.add(KEYBIND_FORWARD).add(KEYBIND_BACK).add(KEYBIND_LEFT).add(KEYBIND_RIGHT).add(KEYBIND_JUMP).add(KEYBIND_DESCEND);
      settingGroup.add(KEYBIND_INVENTORY).add(KEYBIND_CHAT);
      settingGroup.add(KEYBIND_FULLSCREEN).add(KEYBIND_HIDEGUI).add(KEYBIND_DEBUG).add(KEYBIND_AREABOUNDARIES);
    }
    return settingGroup;
  }
  
  public static int getCode(String s) {
    return ((KeybindSetting) Settings.getSetting(s)).getKey();
  }
  
  public static boolean isJustPressed(String s) {
    return Gdx.input.isKeyJustPressed(getCode(s));
  }
  
  public static boolean isPressed(String s) {
    return Gdx.input.isKeyPressed(getCode(s));
  }
}
