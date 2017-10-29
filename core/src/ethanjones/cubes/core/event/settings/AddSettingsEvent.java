package ethanjones.cubes.core.event.settings;

/**
 * Posted when Cubes' settings have been setup, before reading settings from file
 */
public class AddSettingsEvent extends SettingsEvent {

  public AddSettingsEvent() {
    super(false);
  }

}
