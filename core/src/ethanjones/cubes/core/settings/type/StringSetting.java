package ethanjones.cubes.core.settings.type;

import ethanjones.cubes.core.settings.Setting;
import ethanjones.cubes.core.settings.VisualSettingManager;
import ethanjones.cubes.graphics.menus.SettingsMenu;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonValue;

public class StringSetting extends Setting {

  private String s;

  public StringSetting() {
    this("");
  }

  public StringSetting(String s) {
    this.s = s;
  }

  public String get() {
    return s;
  }

  @Override
  public Actor getActor(VisualSettingManager visualSettingManager) {
    final TextField textField = new TextField(s, visualSettingManager.getSkin());
    textField.addListener(new EventListener() {
      @Override
      public boolean handle(Event event) {
        if (!(event instanceof SettingsMenu.SaveEvent)) return false;
        set(textField.getText());
        return true;
      }
    });
    return textField;
  }

  public void set(String s) {
    this.s = s;
    onChange();
  }

  @Override
  public String toString() {
    return s;
  }
  
  @Override
  public JsonValue toJson() {
    return Json.value(s);
  }
  
  @Override
  public void readJson(JsonValue json) {
    String str = json.asString();
    if (str != null) this.s = str;
    onChange();
  }
  
}
