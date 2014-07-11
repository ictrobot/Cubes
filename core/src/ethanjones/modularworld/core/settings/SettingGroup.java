package ethanjones.modularworld.core.settings;


import java.util.Collection;
import java.util.HashMap;

public class SettingGroup {

  private static final String lineSeparator = System.getProperty("line.separator");

  private final SettingGroup parent;
  private final HashMap<String, SettingGroup> childGroups;
  private final HashMap<String, Setting> childSettings;
  private String name;


  public SettingGroup(String name, SettingGroup parent) {
    this.name = name;
    this.parent = parent;
    if (parent != null) {
      this.parent.setSettingGroup(this);
    }
    childGroups = new HashMap<String, SettingGroup>();
    childSettings = new HashMap<String, Setting>();
  }

  protected static String getString(Setting<?> setting, boolean equals, boolean line) {
    StringBuilder s = new StringBuilder();
    s.append(setting.name);
    if (equals) s.append("=").append(setting.getString());
    SettingGroup group = setting.parent;
    while (group != null) {
      s.insert(0, ".").insert(0, group.getName());
      group = group.getParent();
    }
    if (line) s.append(lineSeparator);
    return s.toString();
  }

  public SettingGroup getParent() {
    return parent;
  }

  public Collection<SettingGroup> getChildGroups() {
    return childGroups.values();
  }

  public SettingGroup getSettingGroup(String name) {
    SettingGroup s = childGroups.get(name);
    if (s != null) {
      return s;
    }
    return new SettingGroup(name, this);
  }

  public SettingGroup setSettingGroup(SettingGroup setting) {
    childGroups.put(setting.getName(), setting);
    return setting;
  }

  public Collection<Setting> getChildSettings() {
    return childSettings.values();
  }

  public Setting getSetting(String name, Setting setting) {
    Setting s = childSettings.get(name);
    if (s != null) {
      return s;
    }
    getChildSettings().add(setting);
    return setting;
  }

  public Setting getSetting(String name) {
    return childSettings.get(name);
  }

  public Setting setSetting(Setting setting) {
    childSettings.put(setting.getName(), setting);
    return setting;
  }

  public String getString() {
    StringBuilder stringBuilder = new StringBuilder();
    for (SettingGroup settingGroup : getChildGroups()) {
      stringBuilder.append(settingGroup.getString());
    }
    for (Setting setting : getChildSettings()) {
      stringBuilder.append(getString(setting, true, true));
    }
    return stringBuilder.toString();
  }

  public String getName() {
    return name;
  }

  public int hashCode() {
    return name.hashCode() ^ childGroups.hashCode() ^ childSettings.hashCode();
  }
}
