package ethanjones.cubes.core.settings;

import ethanjones.cubes.core.event.settings.SettingChangedEvent;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.eclipsesource.json.JsonValue;

public abstract class Setting {

  public abstract Actor getActor(VisualSettingManager visualSettingManager);

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
