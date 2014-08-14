package ethanjones.modularworld.core.adapter;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import ethanjones.modularworld.core.Branding;
import ethanjones.modularworld.core.ModularWorldException;
import ethanjones.modularworld.core.logging.Log;
import ethanjones.modularworld.graphics.menu.MainMenu;
import ethanjones.modularworld.graphics.menu.Menu;
import ethanjones.modularworld.side.client.ModularWorldClient;
import ethanjones.modularworld.side.common.ModularWorld;
import ethanjones.modularworld.side.server.ModularWorldServer;

public class GraphicalAdapter implements ApplicationListener {

  public static GraphicalAdapter instance;

  private Menu menu;
  private ModularWorldServer modularWorldServer;
  private ModularWorldClient modularWorldClient;

  public GraphicalAdapter() {
    GraphicalAdapter.instance = this;
  }

  public void setMenu(Menu menu) {
    Menu old = this.menu;
    if (old != null) {
      old.hide();
      old.dispose();
    }
    this.menu = menu;
    if (menu != null) {
      Log.debug("Menu set to " + menu.getClass().getSimpleName());
      menu.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
      menu.show();
    } else {
      Log.debug("Menu set to null");
    }

  }

  public Menu getMenu() {
    return menu;
  }

  public void setModularWorld(ModularWorldServer modularWorldServer, ModularWorldClient modularWorldClient) {
    this.modularWorldServer = modularWorldServer;
    this.modularWorldClient = modularWorldClient;

    if (modularWorldServer != null) {
      Log.debug("ModularWorldServer set");
      modularWorldServer.create();
      modularWorldServer.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    } else {
      Log.debug("ModularWorldServer set to null");
    }
    if (modularWorldClient != null) {
      Log.debug("ModularWorldClient set");
      modularWorldClient.create();
      modularWorldClient.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    } else {
      Log.debug("ModularWorldClient set to null");
    }
  }

  @Override
  public void create() {
    try {
      ModularWorld.setup();
      Gdx.graphics.setTitle(Branding.DEBUG);
      setMenu(new MainMenu());
    } catch (Exception e) {
      Log.error(Branding.NAME, "", ModularWorldException.getModularWorldException(e));
    }
  }

  @Override
  public void resize(int width, int height) {
    try {
      if (menu != null) menu.resize(width, height);
      if (modularWorldServer != null) modularWorldServer.resize(width, height);
      if (modularWorldClient != null) modularWorldClient.resize(width, height);
    } catch (Exception e) {
      Log.error(Branding.NAME, "", ModularWorldException.getModularWorldException(e));
    }
  }

  @Override
  public void render() {
    try {
      Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
      if (menu == null) {
        Gdx.input.setCursorCatched(true);
      } else {
        Gdx.input.setCursorCatched(false);
      }
      if (modularWorldServer != null) modularWorldServer.render();
      if (modularWorldClient != null) modularWorldClient.render();
      if (menu != null) menu.render(); //Render menu over client
    } catch (Exception e) {
      Log.error(Branding.NAME, "", ModularWorldException.getModularWorldException(e));
    }
  }

  @Override
  public void pause() {
    try {
      if (modularWorldServer != null) modularWorldServer.pause();
      if (modularWorldClient != null) modularWorldClient.pause();
    } catch (Exception e) {
      Log.error(Branding.NAME, "", ModularWorldException.getModularWorldException(e));
    }
  }

  @Override
  public void resume() {
    try {
      if (modularWorldServer != null) modularWorldServer.resume();
      if (modularWorldClient != null) modularWorldClient.resume();
    } catch (Exception e) {
      Log.error(Branding.NAME, "", ModularWorldException.getModularWorldException(e));
    }
  }

  @Override
  public void dispose() {
    try {
      if (menu != null) {
        menu.hide();
        menu.dispose();
      }
      Menu.disposeSpriteBatch();
      if (modularWorldServer != null) modularWorldServer.dispose();
      if (modularWorldClient != null) modularWorldClient.dispose();
    } catch (Exception e) {
      Log.error(Branding.NAME, "", ModularWorldException.getModularWorldException(e));
    }
  }

  public void gotoMainMenu() {
    setModularWorld(null, null);
    setMenu(new MainMenu());
  }
}
