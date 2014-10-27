package ethanjones.modularworld.graphics.menu.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import ethanjones.modularworld.core.adapter.GraphicalAdapter;
import ethanjones.modularworld.core.localization.Localization;
import ethanjones.modularworld.core.settings.SettingGroup;
import ethanjones.modularworld.core.settings.Settings;
import ethanjones.modularworld.core.settings.VisualSettingManager;
import ethanjones.modularworld.core.system.ModularWorldException;
import ethanjones.modularworld.graphics.menu.Menu;
import ethanjones.modularworld.graphics.menu.MenuTools;

import java.util.ArrayList;
import java.util.Map;

public class SettingsMenu extends Menu implements VisualSettingManager {

  static final Value CELL_PADDING = new Value() {
    @Override
    public float get(Actor context) {
      return 5;
    }
  };
  static final Value CELL_WIDTH = new Value() {

    @Override
    public float get(Actor context) {
      return (Gdx.graphics.getWidth() / 2) - (CELL_PADDING.get(context) * 2);
    }
  };
  static final Value CELL_HEIGHT = new Value() {
    @Override
    public float get(Actor context) {
      return (Gdx.graphics.getHeight() / 10) - (CELL_PADDING.get(context) * 2);
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

    title = new Label(Localization.get("menu.settings.title"), skin.get("title", Label.LabelStyle.class));

    table = new Table(skin);

    for (Map.Entry<String, SettingGroup> entry : settingGroup.getChildGroups().entrySet()) {
      Label name = new Label(Settings.getLocalisedSettingGroupName(entry.getKey()), skin);
      name.setAlignment(Align.left, Align.left);

      Actor actor = entry.getValue().getActor(this);

      listObjects.add(new ListObject(name, actor));
    }

    for (String str : settingGroup.getChildren()) {
      Label name = new Label(Settings.getLocalisedSettingGroupName(str), skin);
      name.setAlignment(Align.left, Align.left);

      Actor actor = Settings.getSetting(str).getActor(this);

      listObjects.add(new ListObject(name, actor));
    }

    scrollPane = new ScrollPane(table, skin);
    scrollPane.setScrollingDisabled(true, false);

    back = MenuTools.getBackButton(this);
  }

  @Override
  public void addActors() {
    stage.addActor(title);
    stage.addActor(scrollPane);
    stage.addActor(back);
  }

  @Override
  public void resize(int width, int height) {
    super.resize(width, height);

    scrollPane.setBounds(0, height / 6, Gdx.graphics.getWidth(), height / 6 * 4);

    table.setWidth(width);
    table.clearChildren();

    for (int i = 0; i < listObjects.size(); i++) {
      ListObject listObject = listObjects.get(i);

      table.add(listObject.label).width(CELL_WIDTH).height(CELL_HEIGHT).pad(CELL_PADDING).fillX().fillY();
      table.add(listObject.actor).width(CELL_WIDTH).height(CELL_HEIGHT).pad(CELL_PADDING).fillX().fillY();
      table.row();
    }

    scrollPane.layout();

    MenuTools.setTitle(title);
    back.setBounds(width / 4, 0, width / 2, height / 6);
  }

  public void hide() {
    super.hide();
    for (ListObject listObject : listObjects) {
      saveEvent.reset();
      listObject.actor.fire(saveEvent);
    }
    Settings.write();
  }

  @Override
  public void setSettingGroup(SettingGroup settingGroup) {
    GraphicalAdapter.instance.setMenu(new SettingsMenu(settingGroup));
  }

  public static class SaveEvent extends Event {

  }

  private static class ListObject {

    public final Label label;
    public final Actor actor;

    public ListObject(Label label, Actor actor) {
      this.label = label;
      this.actor = actor;
      if (!(actor instanceof Layout)) throw new ModularWorldException("Settings actor must implement Layout");
    }
  }
}
