package ethanjones.cubes.graphics.menus;

import ethanjones.cubes.core.localization.Localization;
import ethanjones.cubes.core.platform.Adapter;
import ethanjones.cubes.core.settings.Setting;
import ethanjones.cubes.core.settings.SettingGroup;
import ethanjones.cubes.core.settings.Settings;
import ethanjones.cubes.core.settings.VisualSettingManager;
import ethanjones.cubes.core.system.CubesException;
import ethanjones.cubes.graphics.menu.Menu;
import ethanjones.cubes.graphics.menu.MenuTools;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.badlogic.gdx.utils.Align;

import java.util.ArrayList;
import java.util.Map;

import static ethanjones.cubes.graphics.Graphics.GUI_WIDTH;

public class SettingsMenu extends Menu implements VisualSettingManager {

  public static class SaveEvent extends Event {

  }

  private static class ListObject {

    public final Label label;
    public final Actor actor;

    public ListObject(Label label, Actor actor) {
      this.label = label;
      this.actor = actor;
      if (!(actor instanceof Layout)) throw new CubesException("Settings actor must implement Layout");
    }
  }

  static final Value CELL_WIDTH = new Value() {

    @Override
    public float get(Actor context) {
      return (GUI_WIDTH / 2) - 10;
    }
  };
  static SaveEvent saveEvent = new SaveEvent();
  private final SettingGroup settingGroup;
  Label title;
  ScrollPane scrollPane;
  Table table;
  TextButton back;
  ArrayList<ListObject> listObjects = new ArrayList<ListObject>();

  public SettingsMenu() {
    this(Settings.getBaseSettingGroup());
  }

  public SettingsMenu(SettingGroup settingGroup) {
    this.settingGroup = settingGroup;

    if (settingGroup.getUnlocalizedName().isEmpty()) {
      title = new Label(Localization.get("menu.settings.title"), skin.get("title", Label.LabelStyle.class));
    } else {
      title = new Label(Localization.get("menu.settings.titleWithName", Settings.getLocalisedSettingGroupName(settingGroup.getUnlocalizedName())), skin.get("title", Label.LabelStyle.class));
    }

    table = new Table(skin);

    for (Map.Entry<String, SettingGroup> entry : settingGroup.getChildGroups().entrySet()) {
      if (!entry.getValue().shouldDisplay()) continue;
      Label name = new Label(Settings.getLocalisedSettingGroupName(entry.getKey()), skin);
      name.setAlignment(Align.left, Align.left);

      Actor actor = entry.getValue().getActor(this);

      listObjects.add(new ListObject(name, actor));
    }

    for (String str : settingGroup.getChildren()) {
      Setting setting = Settings.getSetting(str);
      if (!setting.shouldDisplay()) continue;

      Label name = new Label(Settings.getLocalisedSettingGroupName(str), skin);
      name.setAlignment(Align.left, Align.left);

      Actor actor = setting.getActor(str, this);

      listObjects.add(new ListObject(name, actor));
    }

    scrollPane = new ScrollPane(table, skin);
    scrollPane.setScrollingDisabled(true, false);

    back = MenuTools.getBackButton(this);

    stage.addActor(title);
    stage.addActor(scrollPane);
    stage.addActor(back);

    table.defaults().width(CELL_WIDTH).pad(5).fillX().fillY().uniform();
    for (int i = 0; i < listObjects.size(); i++) {
      ListObject listObject = listObjects.get(i);

      table.add(listObject.label);
      table.add(listObject.actor);
      table.row();
    }
  }

  @Override
  public void resize(float width, float height) {
    super.resize(width, height);

    scrollPane.setBounds(0, height / 6, width, height / 6 * 4);

    table.setWidth(width);
    scrollPane.layout();

    MenuTools.setTitle(title);
    back.setBounds(width / 4, 0, width / 2, height / 6);
  }

  @Override
  public void show() {
    stage.setScrollFocus(scrollPane);
  }

  public void hide() {
    for (ListObject listObject : listObjects) {
      saveEvent.reset();
      listObject.actor.fire(saveEvent);
    }
    Settings.write();
  }

  @Override
  public void setSettingGroup(SettingGroup settingGroup) {
    Adapter.setMenu(new SettingsMenu(settingGroup));
  }

  @Override
  public Skin getSkin() {
    return Menu.skin;
  }
}
