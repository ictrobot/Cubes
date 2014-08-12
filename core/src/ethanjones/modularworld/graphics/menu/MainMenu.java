package ethanjones.modularworld.graphics.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import ethanjones.modularworld.core.Branding;

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
        //MenuManager.instance.adaptiveApplicationListener.setListener(new ModularWorldWrapper(
        //  new ModularWorldServer(new ServerNetworkingParameter()),
        //new ModularWorldClient(new ClientNetworkingParameter("localhost"))
        //));
        return true;
      }
    });
  }

  @Override
  public void render() {
    Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

    stage.act();
    stage.draw();
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
