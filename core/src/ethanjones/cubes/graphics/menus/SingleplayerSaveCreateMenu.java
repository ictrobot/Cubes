package ethanjones.cubes.graphics.menus;

import ethanjones.cubes.core.localization.Localization;
import ethanjones.cubes.core.platform.Adapter;
import ethanjones.cubes.graphics.menu.Menu;
import ethanjones.cubes.graphics.menu.MenuTools;
import ethanjones.cubes.world.client.ClientSaveManager;
import ethanjones.cubes.world.generator.GeneratorManager;
import ethanjones.cubes.world.save.Gamemode;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class SingleplayerSaveCreateMenu extends Menu {

  Label title;
  TextField name;
  SelectBox<SaveTypeDisplay> generator;
  SelectBox<Gamemode> mode;
  TextField seed;
  TextButton start;
  TextButton back;

  public SingleplayerSaveCreateMenu() {
    super();
    title = new Label(Localization.get("menu.singleplayer.create.title"), skin.get("title", Label.LabelStyle.class));
    name = new TextField("", skin);
    name.setMessageText(Localization.get("menu.singleplayer.create.name"));
    name.setTextFieldFilter(new TextField.TextFieldFilter() {
      @Override
      public boolean acceptChar(TextField textField, char c) {
        return c >= 0x20 && c < 0x7F;
      }
    });

    generator = new SelectBox<SaveTypeDisplay>(skin);
    String[] types = GeneratorManager.ids();
    SaveTypeDisplay[] display = new SaveTypeDisplay[types.length];
    for (int i = 0; i < types.length; i++) {
      display[i] = new SaveTypeDisplay(types[i]);
    }
    generator.setItems(display);

    mode = new SelectBox<Gamemode>(skin);
    mode.setItems(Gamemode.values());

    seed = new TextField("", skin);
    seed.setMessageText(Localization.get("menu.singleplayer.create.seed"));

    start = new TextButton(Localization.get("menu.singleplayer.create.start"), skin);
    back = MenuTools.getBackButton(this);

    start.addListener(new ChangeListener() {
      @Override
      public void changed(ChangeEvent event, Actor actor) {
        Adapter.setMenu(new SingleplayerLoadingMenu(ClientSaveManager.createSave(name.getText(), generator.getSelected().id, mode.getSelected(), seed.getText())));
      }
    });

    stage.addActor(title);
    stage.addActor(name);
    stage.addActor(generator);
    stage.addActor(mode);
    stage.addActor(seed);
    stage.addActor(start);
    stage.addActor(back);
  }

  @Override
  public void resize(float width, float height) {
    super.resize(width, height);
    MenuTools.setTitle(title);
    MenuTools.arrange(width / 4, height / 4, width / 2, height / 2, MenuTools.Direction.Above, start, seed, mode, generator, name);
    MenuTools.copyPosAndSize(start, back);
    back.setY(0);
  }

  private static class SaveTypeDisplay {
    public final String id;

    public SaveTypeDisplay(String id) {
      this.id = id;
    }

    @Override
    public String toString() {
      return GeneratorManager.getName(id);
    }
  }
}
