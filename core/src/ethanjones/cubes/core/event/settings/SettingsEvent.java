package ethanjones.cubes.core.event.settings;

import ethanjones.cubes.core.event.Event;

public class SettingsEvent extends Event {

  public SettingsEvent(boolean cancelable) {
    super(cancelable, false);
  }

}
