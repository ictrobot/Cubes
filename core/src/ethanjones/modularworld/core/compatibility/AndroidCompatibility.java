package ethanjones.modularworld.core.compatibility;

import com.badlogic.gdx.Application;
import ethanjones.modularworld.core.events.EventHandler;
import ethanjones.modularworld.core.events.setting.AfterProcessSettingEvent;
import ethanjones.modularworld.core.settings.Settings;

public class AndroidCompatibility extends Compatibility {

  protected AndroidCompatibility() {
    super(Application.ApplicationType.Android);
  }

  @EventHandler
  public void compatibilitySettings(AfterProcessSettingEvent event) {
    Settings.renderer_block_viewDistance.getIntegerSetting().setValue(1);
  }
}
