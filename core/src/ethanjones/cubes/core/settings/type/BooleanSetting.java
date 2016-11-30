package ethanjones.cubes.core.settings.type;

import ethanjones.cubes.core.settings.Setting;
import ethanjones.cubes.core.settings.VisualSettingManager;
import ethanjones.cubes.graphics.menu.Menu;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonValue;

public class BooleanSetting extends Setting {

  private boolean b;

  public BooleanSetting() {
    this(false);
  }

  public BooleanSetting(boolean b) {
    this.b = b;
  }

  @Override
  public Actor getActor(VisualSettingManager visualSettingManager) {
    final TextButton textButton = new TextButton(b ? "True" : "False", Menu.skin);
    textButton.addListener(new ChangeListener() {
      @Override
      public void changed(ChangeEvent event, Actor actor) {//easier than SettingsMenu.SaveEvent and works
        set(!get());
        textButton.setText(b ? "True" : "False");
      }
    });
    return textButton;
  }

  public void set(boolean b) {
    this.b = b;
    onChange();
  }

  public boolean get() {
    return b;
  }

  @Override
  public String toString() {
    return Boolean.toString(b);
  }
  
  @Override
  public JsonValue toJson() {
    return Json.value(this.b);
  }
  
  @Override
  public void readJson(JsonValue json) {
    this.b = json.asBoolean();
    onChange();
  }
}
