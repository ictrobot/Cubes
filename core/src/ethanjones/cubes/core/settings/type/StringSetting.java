package ethanjones.cubes.core.settings.type;

import ethanjones.cubes.core.settings.Setting;
import ethanjones.cubes.core.settings.VisualSettingManager;
import ethanjones.cubes.graphics.menu.Menu;
import ethanjones.cubes.graphics.menus.SettingsMenu;
import ethanjones.data.DataGroup;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

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
    final TextField textField = new TextField(s, Menu.skin);
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
  }

  @Override
  public String toString() {
    return s;
  }

  @Override
  public DataGroup write() {
    DataGroup dataGroup = new DataGroup();
    dataGroup.put("data", s);
    return dataGroup;
  }

  @Override
  public void read(DataGroup data) {
    s = data.getString("data");
  }
}
