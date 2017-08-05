package ethanjones.cubes.core.settings.type;

import ethanjones.cubes.core.localization.Localization;
import ethanjones.cubes.core.platform.Compatibility;
import ethanjones.cubes.core.settings.Setting;
import ethanjones.cubes.core.settings.VisualSettingManager;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonValue;

public class KeybindSetting extends Setting {
  
  private int key = Keys.UNKNOWN;
  
  public KeybindSetting(int key) {
    this.key = key;
  }
  
  @Override
  public Actor getActor(VisualSettingManager visualSettingManager) {
    final TextButton button = new TextButton(toString(), visualSettingManager.getSkin()) {
      boolean waiting = false;
      {
        final TextButton textButton = this;
        this.addListener(new ChangeListener() {
          @Override
          public void changed(ChangeEvent event, Actor actor) {
            if (waiting) {
              getStage().setKeyboardFocus(null);
              setText(KeybindSetting.this.toString());
            } else {
              getStage().setKeyboardFocus(textButton);
              setText(Localization.get("menu.settings.keybind_press"));
            }
            waiting = !waiting;
          }
        });
        
        this.addListener(new InputListener() {
          @Override
          public boolean keyDown(InputEvent event, int keycode) {
            if (waiting) {
              getStage().setKeyboardFocus(null);
              key = event.getKeyCode();
              waiting = false;
              setText(KeybindSetting.this.toString());
              onChange();
              return true;
            }
            return false;
          }
        });
      }
  
      @Override
      public boolean isPressed() {
        return waiting || super.isPressed();
      }
    };
    return button;
  }
  
  @Override
  public String toString() {
    String s = Input.Keys.toString(key);
    return s == null ? "None" : s;
  }
  
  @Override
  public JsonValue toJson() {
    return Json.value(key);
  }
  
  @Override
  public void readJson(JsonValue json) {
    key = json.asInt();
  }
  
  public int getKey() {
    return key;
  }
  
  @Override
  public boolean shouldDisplay() {
    return Compatibility.get().getApplicationType() == Application.ApplicationType.Desktop;
  }
}
