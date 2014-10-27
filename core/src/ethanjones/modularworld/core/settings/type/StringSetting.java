package ethanjones.modularworld.core.settings.type;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import ethanjones.data.DataGroup;
import ethanjones.modularworld.core.settings.Setting;
import ethanjones.modularworld.core.settings.VisualSettingManager;
import ethanjones.modularworld.graphics.menu.Menu;
import ethanjones.modularworld.graphics.menu.menus.SettingsMenu;

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

  public void set(String s) {
    this.s = s;
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

  @Override
  public String toString() {
    return s;
  }

  @Override
  public DataGroup write() {
    DataGroup dataGroup = new DataGroup();
    dataGroup.setString("data", s);
    return dataGroup;
  }

  @Override
  public void read(DataGroup data) {
    s = data.getString("data");
  }
}
