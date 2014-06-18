package ethanjones.modularworld.core.settings;

import java.util.Collection;

public class Settings {

  private static final String lineSeparator = System.getProperty("line.separator");

  SettingGroup main;

  public Settings() {
    this.main = new SettingGroup("settings", null);
  }

  public SettingGroup getSettingGroup(String name) {
    return main.getSettingGroup(name);
  }

  public Setting getSetting(String name, Setting setting) {
    return main.getSetting(name, setting);
  }

  public String getString() {
    return main.getString();
  }

  protected static String getString(Collection<Setting> settings) {
    StringBuilder s = new StringBuilder();
    for (Setting setting : settings) {
      s.append(getString(setting));
    }
    return s.toString();
  }

  protected static String getString(Setting<?> setting) {
    StringBuilder s = new StringBuilder();
    s.append(setting.name).append("=").append(setting.getString());
    SettingGroup group = setting.parent;
    while (group != null) {
      s.insert(0, ".").insert(0, group.getName());
      group = group.getParent();
    }
    s.append(lineSeparator);
    return s.toString();
  }

  protected void restore(String string) {

  }

}
