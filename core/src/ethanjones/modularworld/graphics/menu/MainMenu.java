package ethanjones.modularworld.graphics.menu;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import ethanjones.modularworld.core.Branding;
import ethanjones.modularworld.networking.client.ClientNetworkingParameter;
import ethanjones.modularworld.networking.server.ServerNetworkingParameter;
import ethanjones.modularworld.side.client.ClientAdapter;
import ethanjones.modularworld.side.client.ModularWorldClient;
import ethanjones.modularworld.side.server.ModularWorldServer;

public class MainMenu extends Menu {

  Label name;
  Table buttons;
  TextButton singlePlayer;

  public MainMenu() {
    super();
    stage.addActor(name = new Label(Branding.NAME, skin));
    stage.addActor(buttons = new Table());

    buttons.addActor(singlePlayer = new TextButton("Single Player", skin));//TODO LOCALIZATION
    singlePlayer.addListener(new EventListener() {
      @Override
      public boolean handle(Event event) {
        if (!(event instanceof ChangeListener.ChangeEvent)) return false;
        ClientAdapter.instance.setModularWorld(
          new ModularWorldServer(new ServerNetworkingParameter()),
          new ModularWorldClient(new ClientNetworkingParameter("localhost"))
        );
        ClientAdapter.instance.setMenu(null);
        return true;
      }
    });
  }

  @Override
  public void resize(int width, int height) {
    super.resize(width, height);

    int border = 6;

    name.setBounds(0, height - name.getPrefHeight(), width, name.getPrefHeight());
    name.setAlignment(Align.center, Align.center);

    buttons.setBounds(width / border, height / border, width / border * (border - 2), height / border * (border - 2));

    singlePlayer.setFillParent(true);
  }
}
