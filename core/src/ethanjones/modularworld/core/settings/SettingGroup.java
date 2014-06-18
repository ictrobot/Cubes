package ethanjones.modularworld.core.settings;


import java.util.ArrayList;

public class SettingGroup {

  private String name;
  private final SettingGroup parent;
  private final ArrayList<SettingGroup> childGroups;
  private final ArrayList<Setting> childSettings;


  public SettingGroup(String name, SettingGroup parent) {
    this.name = name;
    this.parent = parent;
    if (parent != null) {
      this.parent.getChildGroups().add(this);
    }
    childGroups = new ArrayList<SettingGroup>();
    childSettings = new ArrayList<Setting>();
  }

  public SettingGroup getParent() {
    return parent;
  }

  public ArrayList<SettingGroup> getChildGroups() {
    return childGroups;
  }

  public SettingGroup getSettingGroup(String name) {
    for (SettingGroup settingGroup : getChildGroups()) {
      if (settingGroup.getName().toLowerCase() == name.toLowerCase()) {
        return settingGroup;
      }
    }
    SettingGroup settingGroup = new SettingGroup(name, this);
    return settingGroup;
  }

  public ArrayList<Setting> getChildSettings() {
    return childSettings;
  }

  public Setting getSetting(String name, Setting setting) {
    for (Setting s : getChildSettings()) {
      if (s.getName().toLowerCase() == name.toLowerCase()) {
        return s;
      }
    }
    getChildSettings().add(setting);
    return setting;
  }

  public String getString() {
    StringBuilder stringBuilder = new StringBuilder();
    for (SettingGroup settingGroup : getChildGroups()) {
      stringBuilder.append(settingGroup.getString());
    }
    stringBuilder.append(Settings.getString(getChildSettings()));
    return stringBuilder.toString();
  }

  public String getName() {
    return name;
  }
}
