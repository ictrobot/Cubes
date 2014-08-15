package ethanjones.modularworld.graphics.menu;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import ethanjones.modularworld.core.adapter.GraphicalAdapter;
import ethanjones.modularworld.core.localization.Localization;
import ethanjones.modularworld.networking.client.ClientNetworkingParameter;
import ethanjones.modularworld.side.client.ModularWorldClient;

public class MultiplayerConnectMenu extends Menu {

  Label title;
  TextField address;
  TextField port;
  TextButton connect;

  public MultiplayerConnectMenu() {
    super();
    stage.addActor(title = new Label(Localization.get("menu.multiplayer_connect.title"), skin));
    stage.addActor(address = new TextField("", skin));
    address.setMessageText(Localization.get("menu.multiplayer_connect.address"));
    stage.addActor(port = new TextField("", skin));
    port.setMessageText(Localization.get("menu.multiplayer_connect.port"));
    stage.addActor(connect = new TextButton(Localization.get("menu.multiplayer_connect.connect"), skin));

    connect.addListener(new EventListener() {
      @Override
      public boolean handle(Event event) {
        if (!(event instanceof ChangeListener.ChangeEvent)) return false;
        Integer portInt = port.getText().isEmpty() ? 8080 : Integer.parseInt(port.getText());
        GraphicalAdapter.instance.setModularWorld(
          null,
          new ModularWorldClient(new ClientNetworkingParameter(address.getText(), portInt))
        );
        GraphicalAdapter.instance.setMenu(null);
        return true;
      }
    });
  }

  @Override
  public void resize(int width, int height) {
    super.resize(width, height);

    title.setFontScale(width / 400);
    title.setBounds(0, height - title.getPrefHeight(), width, title.getPrefHeight());
    title.setAlignment(Align.center, Align.center);

    int border = 6;
    address.setWidth(2 * (width / border));
    address.setHeight(height / border);
    address.setX((width / 2) - width / border);
    address.setY(height / 2);
    port.setWidth(address.getWidth());
    port.setHeight(address.getHeight());
    port.setX(address.getX());
    port.setY(address.getY() - port.getHeight());

    connect.setWidth(width / border);
    connect.setHeight(height / border);
    connect.setX(width / 2 - (connect.getWidth() / 2));
    connect.setY(port.getY() - port.getHeight());
  }
}
