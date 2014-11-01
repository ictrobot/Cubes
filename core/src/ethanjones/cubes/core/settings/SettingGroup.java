package ethanjones.cubes.core.settings;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import java.util.ArrayList;
import java.util.HashMap;

import ethanjones.cubes.core.localization.Localization;
import ethanjones.cubes.graphics.menu.Menu;

public class SettingGroup {

  private final ArrayList<String> children;
  private final HashMap<String, SettingGroup> childGroups;

  public SettingGroup() {
    children = new ArrayList<String>();
    childGroups = new HashMap<String, SettingGroup>();
  }

  public SettingGroup add(String notLocalised) {
    children.add(notLocalised);
    return this;
  }

  public SettingGroup add(String name, SettingGroup settingGroup) {
    childGroups.put(name, settingGroup);
    return this;
  }

  public Actor getActor(final VisualSettingManager visualSettingManager) {
    final TextButton textButton = new TextButton(Localization.get("menu.settings.open_group"), Menu.skin);
    final SettingGroup settingGroup = this;
    textButton.addListener(new EventListener() {
      @Override
      public boolean handle(Event event) {
        if (!(event instanceof ChangeListener.ChangeEvent)) return false;
        visualSettingManager.setSettingGroup(settingGroup);
        return true;
      }
    });
    return textButton;
  }

  public ArrayList<String> getChildren() {
    return children;
  }

  public HashMap<String, SettingGroup> getChildGroups() {
    return childGroups;
  }
}