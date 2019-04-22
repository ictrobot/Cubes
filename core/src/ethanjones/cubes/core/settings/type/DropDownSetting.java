package ethanjones.cubes.core.settings.type;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonValue;
import ethanjones.cubes.core.settings.Setting;
import ethanjones.cubes.core.settings.Settings;
import ethanjones.cubes.core.settings.VisualSettingManager;
import ethanjones.cubes.core.system.CubesException;
import ethanjones.cubes.graphics.menus.SettingsMenu;

import java.util.Arrays;

public class DropDownSetting extends Setting {

  protected final String[] options;
  protected String selected;

  public DropDownSetting(String... options) {
    if (options == null || options.length == 0) throw new IllegalArgumentException(Arrays.toString(options));
    this.options = options;
    this.selected = options[0];
  }

  @Override
  public Actor getActor(final String settingName, VisualSettingManager visualSettingManager) {
    final SelectBox<String> selectBox = new SelectBox<String>(visualSettingManager.getSkin()) {
      @Override
      protected String toString(String s) {
        return Settings.getLocalisedSettingName(settingName + "." + s);
      }
    };
    selectBox.setItems(options);
    selectBox.setSelected(selected);
    selectBox.addListener(new EventListener() {
      @Override
      public boolean handle(Event event) {
        if (!(event instanceof SettingsMenu.SaveEvent)) return false;
        set(selectBox.getSelected());
        return true;
      }
    });
    return selectBox;
  }

  @Override
  public String toString() {
    return selected;
  }

  @Override
  public JsonValue toJson() {
    return Json.value(selected);
  }

  public String getSelected() {
    return selected;
  }

  @Override
  public void readJson(JsonValue json) {
    set(json.asString());
  }

  public void set(String s) {
    for (String option : options) {
      if (option.equals(s)) {
        boolean change = !selected.equals(option);
        selected = option;
        if (change) onChange();
        return;
      }
    }
    throw new CubesException("Invalid selection: '" + s + "' should be one of " + Arrays.toString(options));
  }
}
