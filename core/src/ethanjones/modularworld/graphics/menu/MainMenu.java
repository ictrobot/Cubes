package ethanjones.modularworld.graphics.menu;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import ethanjones.modularworld.core.Branding;
import ethanjones.modularworld.core.adapter.GraphicalAdapter;
import ethanjones.modularworld.networking.client.ClientNetworkingParameter;
import ethanjones.modularworld.networking.server.ServerNetworkingParameter;
import ethanjones.modularworld.side.client.ModularWorldClient;
import ethanjones.modularworld.side.common.ModularWorld;
import ethanjones.modularworld.side.server.ModularWorldServer;

public class MainMenu extends Menu {

  Label name;
  Table buttons;
  TextButton singlePlayer;
  TextButton quit;

  public MainMenu() {
    super();
    stage.addActor(name = new Label(Branding.NAME, skin));
    stage.addActor(buttons = new Table());

    //TODO LOCALIZATION
    buttons.addActor(singlePlayer = new TextButton("Single Player", skin));
    singlePlayer.addListener(new EventListener() {
      @Override
      public boolean handle(Event event) {
        if (!(event instanceof ChangeListener.ChangeEvent)) return false;
        GraphicalAdapter.instance.setModularWorld(
          new ModularWorldServer(new ServerNetworkingParameter()),
          new ModularWorldClient(new ClientNetworkingParameter("localhost"))
        );
        GraphicalAdapter.instance.setMenu(null);
        return true;
      }
    });
    buttons.addActor(quit = new TextButton("Quit", skin));
    quit.addListener(new EventListener() {
      @Override
      public boolean handle(Event event) {
        if (!(event instanceof ChangeListener.ChangeEvent)) return false;
        ModularWorld.quit(true);
        return true;
      }
    });
    //TODO Add button for Mutliplayer
  }

  @Override
  public void resize(int width, int height) {
    super.resize(width, height);
    
    name.setFontScale(width / 200);
    name.setBounds(0, height - name.getPrefHeight(), width, name.getPrefHeight());
    name.setAlignment(Align.center, Align.center);

    int border = 6;
    buttons.setBounds(width / border, height / border, 2 * (width / border), 2 * (height / border));

    singlePlayer.setWidth(buttons.getWidth());
    singlePlayer.setHeight(buttons.getHeight() / 2);
    singlePlayer.setX(buttons.getX());
    singlePlayer.setY(buttons.getY() + (buttons.getHeight() / 2));
    quit.setWidth(buttons.getWidth());
    quit.setHeight(buttons.getHeight() / 2);
    quit.setX(buttons.getX());
    quit.setY(buttons.getY());
  }
}
