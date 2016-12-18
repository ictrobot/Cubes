package ethanjones.cubes.core.settings;

import ethanjones.cubes.core.localization.Localization;
import ethanjones.cubes.graphics.menu.Menu;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class SettingGroup {

  private final ArrayList<String> children;
  private final LinkedHashMap<String, SettingGroup> childGroups;

  public SettingGroup() {
    children = new ArrayList<String>();
    childGroups = new LinkedHashMap<String, SettingGroup>();
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
    textButton.addListener(new ChangeListener() {
      @Override
      public void changed(ChangeEvent event, Actor actor) {
        visualSettingManager.setSettingGroup(SettingGroup.this);
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

  public boolean shouldDisplay() {
    return true;
  }
}
