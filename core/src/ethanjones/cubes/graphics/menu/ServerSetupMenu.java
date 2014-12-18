package ethanjones.cubes.graphics.menu;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import ethanjones.cubes.core.localization.Localization;
import ethanjones.cubes.core.platform.Adapter;
import ethanjones.cubes.core.settings.Settings;
import ethanjones.cubes.graphics.gui.Gui;
import ethanjones.cubes.graphics.gui.MenuTools;
import ethanjones.cubes.graphics.gui.StageMenu;

public class ServerSetupMenu extends StageMenu {

  Label title;
  TextField port;
  TextButton start;
  TextButton back;

  public ServerSetupMenu() {
    super();
    title = new Label(Localization.get("menu.server.title"), Gui.skin.get("title", Label.LabelStyle.class));
    port = new TextField("", Gui.skin);
    port.setMessageText(Localization.get("menu.server.port", Settings.getIntegerSettingValue(Settings.NETWORKING_PORT)));
    port.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
    start = new TextButton(Localization.get("menu.server.start"), Gui.skin);
    back = MenuTools.getBackButton(this);

    start.addListener(new ChangeListener() {
      @Override
      public void changed(ChangeEvent event, Actor actor) {
        Adapter.setMenu(new ServerRunningMenu(port.getText().isEmpty() ? Settings.getIntegerSettingValue(Settings.NETWORKING_PORT) : Integer.parseInt(port.getText())));
      }
    });

    stage.addActor(title);
    stage.addActor(port);
    stage.addActor(start);
    stage.addActor(back);
  }

  @Override
  public void resize(int width, int height) {
    super.resize(width, height);
    MenuTools.setTitle(title);
    MenuTools.arrange(width / 4, height / 2, width / 2, height / 4, MenuTools.Direction.Above, start, port);
    MenuTools.copyPosAndSize(start, back);
    back.setY(0);
  }
}
