package ethanjones.cubes.core.settings;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.eclipsesource.json.JsonValue;
import ethanjones.cubes.core.event.settings.SettingChangedEvent;

public abstract class Setting {

  public abstract Actor getActor(String settingName, VisualSettingManager visualSettingManager);

  public abstract String toString();
  
  public abstract JsonValue toJson();
  
  public abstract void readJson(JsonValue json);
  
  public void onChange() {
    new SettingChangedEvent(this).post();
  }

  public boolean shouldDisplay() {
    return true;
  }
}
