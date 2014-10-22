package ethanjones.modularworld.core.settings;

import com.badlogic.gdx.files.FileHandle;
import ethanjones.modularworld.core.compatibility.Compatibility;
import ethanjones.modularworld.core.logging.Log;
import ethanjones.modularworld.core.system.ModularWorldException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class SettingsManager {

  protected SettingGroup main;

  public SettingsManager() {
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

  protected void restore(File file) {
    try {
      BufferedReader br = new BufferedReader(new FileReader(file));
      String line;
      while ((line = br.readLine()) != null) {
        if (line.startsWith("#")) continue;
        int index = line.indexOf("=");
        String name = line.substring(0, index);
        String equals = line.substring(index + 1);

        String[] parts = name.split("\\.");
        if (parts.length < 1 || parts == null) continue;
        name = parts[parts.length - 1];
        SettingGroup group = main;
        int i = 1;
        while (i < parts.length - 1) {
          group = group.getSettingGroup(parts[i]);
          i++;
        }

        Setting setting = group.getSetting(name);
        if (setting == null) {
          continue;
        }
        setting.restore(equals);
      }
      br.close();
    } catch (Exception e) {
      Log.error(new ModularWorldException("Failed to restore settings", e));
    }
  }

  public void writeToFile() {
    FileHandle fileHandle = Compatibility.get().getBaseFolder().child("settings.conf");
    fileHandle.writeString(main.getString(), false);
  }

  public void readFromFile() {
    Setting.restoreFailed.clear();
    FileHandle fileHandle = Compatibility.get().getBaseFolder().child("settings.conf");
    try {
      fileHandle.file().createNewFile();
    } catch (Exception e) {

    }
    restore(fileHandle.file());
    for (Setting setting : Setting.restoreFailed) {
      Log.error("Failed to restore " + setting.getClass().getSimpleName() + " '" + SettingGroup.getString(setting, false, false) + "'");
    }
  }

  public void print() {
    print(main);
  }

  private void print(SettingGroup settingGroup) {
    for (SettingGroup s : settingGroup.getChildGroups()) {
      print(s);
    }
    for (Setting s : settingGroup.getChildSettings()) {
      Log.debug(SettingGroup.getString(s, true, false));
    }
  }
}
