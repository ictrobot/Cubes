package ethanjones.modularworld.graphics.menu.menus;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.GdxRuntimeException;
import ethanjones.modularworld.core.adapter.GraphicalAdapter;
import ethanjones.modularworld.core.localization.Localization;
import ethanjones.modularworld.graphics.menu.Menu;
import ethanjones.modularworld.graphics.menu.MenuTools;
import ethanjones.modularworld.graphics.menu.actor.ResizableTextField;
import ethanjones.modularworld.networking.client.ClientNetworkingParameter;
import ethanjones.modularworld.side.client.ModularWorldClient;

public class MultiplayerConnectMenu extends Menu {

  public static class MultiplayerLoading extends InfoMenu {

    private final String address;
    private final int port;

    public MultiplayerLoading(String address, int port) {
      super(Localization.get("menu.multiplayer_connect.connecting"), false);
      this.address = address;
      this.port = port;
    }

    public void render() {
      super.render(); //TODO fix not showing connecting
      try {
        ModularWorldClient modularWorldClient = new ModularWorldClient(new ClientNetworkingParameter(address, port));
        GraphicalAdapter.instance.setModularWorld(
          null,
          modularWorldClient
        );
        GraphicalAdapter.instance.setMenu(null);
      } catch (Exception e) {
        if (e instanceof GdxRuntimeException && e.getCause() instanceof Exception) e = (Exception) e.getCause();
        GraphicalAdapter.instance.setMenu(new MultiplayerFailedConnect(e));
      }
    }
  }

  public static class MultiplayerFailedConnect extends InfoMenu {

    private boolean firstRender = true;

    public MultiplayerFailedConnect(Exception e) {
      super(Localization.get("menu.multiplayer_connect.failed_exception") + System.lineSeparator() + e.getClass().getSimpleName(), Localization.get("menu.general.return_main_menu"));
    }

    public MultiplayerFailedConnect() {
      super(Localization.get("menu.multiplayer_connect.failed"), Localization.get("menu.general.return_main_menu"));
    }

    public void render() {
      super.render();
      if (!firstRender) return;
      addButtonListener(new EventListener() {
        @Override
        public boolean handle(Event event) {
          if (!(event instanceof ChangeListener.ChangeEvent)) return false;
          GraphicalAdapter.instance.gotoMainMenu();
          return true;
        }
      });
      GraphicalAdapter.instance.setModularWorld(null, null);
      firstRender = false;
    }
  }

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
        GraphicalAdapter.instance.setMenu(new MultiplayerLoading(address.getText(), port.getText().isEmpty() ? 8080 : Integer.parseInt(port.getText())));
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
