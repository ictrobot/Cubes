package ethanjones.modularworld.graphics.menu.menus;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import ethanjones.modularworld.core.adapter.GraphicalAdapter;
import ethanjones.modularworld.core.localization.Localization;
import ethanjones.modularworld.graphics.menu.Menu;
import ethanjones.modularworld.graphics.menu.MenuTools;
import ethanjones.modularworld.graphics.menu.actor.ResizableTextField;

public class MultiplayerConnectMenu extends Menu {

  Label title;
  ResizableTextField address;
  ResizableTextField port;
  TextButton connect;
  TextButton back;

  public MultiplayerConnectMenu() {
    super();
    title = new Label(Localization.get("menu.multiplayer_connect.title"), skin.get("title", Label.LabelStyle.class));
    address = new ResizableTextField("", skin);
    address.setMessageText(Localization.get("menu.multiplayer_connect.address"));
    port = new ResizableTextField("", skin);
    port.setMessageText(Localization.get("menu.multiplayer_connect.port"));
    port.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
    connect = new TextButton(Localization.get("menu.multiplayer_connect.connect"), skin);
    back = MenuTools.getBackButton(this);

    connect.addListener(new EventListener() {
      @Override
      public boolean handle(Event event) {
        if (!(event instanceof ChangeListener.ChangeEvent)) return false;
        GraphicalAdapter.instance.setMenu(new MultiplayerLoadingMenu(address.getText(), port.getText().isEmpty() ? 8080 : Integer.parseInt(port.getText())));
        return true;
      }
    });
  }

  @Override
  public void addActors() {
    stage.addActor(title);
    stage.addActor(address);
    stage.addActor(port);
    stage.addActor(connect);
    stage.addActor(back);
  }

  @Override
  public void resize(int width, int height) {
    super.resize(width, height);
    MenuTools.setTitle(title);
    MenuTools.arrange(width / 4, height / 4, width / 2, height / 2, MenuTools.Direction.Above, connect, port, address);
    MenuTools.copyPosAndSize(connect, back);
    back.setY(0);
    MenuTools.fitText(connect, port, address, back);
  }
}
