package ethanjones.cubes.graphics.menu.menus;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import ethanjones.cubes.core.localization.Localization;
import ethanjones.cubes.core.platform.Adapter;
import ethanjones.cubes.graphics.menu.Menu;
import ethanjones.cubes.graphics.menu.MenuTools;

public class MultiplayerConnectMenu extends Menu {

  Label title;
  TextField address;
  TextField port;
  TextButton connect;
  TextButton back;

  public MultiplayerConnectMenu() {
    super();
    title = new Label(Localization.get("menu.multiplayer.title"), skin.get("title", Label.LabelStyle.class));
    address = new TextField("", skin);
    address.setMessageText(Localization.get("menu.multiplayer.address"));
    port = new TextField("", skin);
    port.setMessageText(Localization.get("menu.multiplayer.port", 24842)); //Not "Settings.getIntegerSettingValue(Settings.NETWORKING_PORT)" because the port is set on the server
    port.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
    connect = new TextButton(Localization.get("menu.multiplayer.connect"), skin);
    back = MenuTools.getBackButton(this);

    connect.addListener(new ChangeListener() {
      @Override
      public void changed(ChangeEvent event, Actor actor) {
        Adapter.setMenu(new MultiplayerLoadingMenu(address.getText().isEmpty() ? "localhost" : address.getText(), port.getText().isEmpty() ? 24842 : Integer.parseInt(port.getText())));
      }
    });
  }

  @Override
  public void resize(int width, int height) {
    super.resize(width, height);
    MenuTools.setTitle(title);
    MenuTools.arrange(width / 4, height / 4, width / 2, height / 2, MenuTools.Direction.Above, connect, port, address);
    MenuTools.copyPosAndSize(connect, back);
    back.setY(0);
  }

  @Override
  public void addActors() {
    stage.addActor(title);
    stage.addActor(address);
    stage.addActor(port);
    stage.addActor(connect);
    stage.addActor(back);
  }
}
