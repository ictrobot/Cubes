package ethanjones.cubes.core.event.settings;

import ethanjones.cubes.core.settings.Setting;

/**
 * Posted when a Setting changes
 */
public class SettingChangedEvent extends SettingsEvent {

  private final Setting setting;

  public SettingChangedEvent(Setting setting) {
    super(false);
    this.setting = setting;
  }

  public Setting getSetting() {
    return setting;
  }
}
