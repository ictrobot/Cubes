package ethanjones.modularworld.core.adapter;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import ethanjones.modularworld.core.logging.Log;
import ethanjones.modularworld.core.system.Branding;
import ethanjones.modularworld.core.system.Debug;
import ethanjones.modularworld.core.system.Memory;
import ethanjones.modularworld.graphics.menu.Fonts;
import ethanjones.modularworld.graphics.menu.Menu;
import ethanjones.modularworld.graphics.menu.MenuManager;
import ethanjones.modularworld.graphics.menu.menus.MainMenu;
import ethanjones.modularworld.side.Side;
import ethanjones.modularworld.side.Sided;
import ethanjones.modularworld.side.client.ModularWorldClient;
import ethanjones.modularworld.side.common.ModularWorld;
import ethanjones.modularworld.side.server.ModularWorldServer;
import ethanjones.modularworld.side.server.ModularWorldServerThread;

public class GraphicalAdapter implements ApplicationListener {

  public static GraphicalAdapter instance;

  private Menu menu;
  private ModularWorldServerThread modularWorldServerThread;
  private ModularWorldClient modularWorldClient;

  public GraphicalAdapter() {
    GraphicalAdapter.instance = this;
  }

  public Menu getMenu() {
    return menu;
  }

  public void setMenu(Menu menu) {
    Menu old = this.menu;
    if (old != null) {
      old.hide();
    }
    this.menu = menu;
    if (menu != null) {
      Log.debug("Menu set to " + menu.getClass().getSimpleName());
      MenuManager.setMenu(menu);
      menu.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
      menu.show();
    } else {
      Log.debug("Menu set to null");
    }

  }

  public void setModularWorld(ModularWorldServer modularWorldServer, ModularWorldClient modularWorldClient) {
    if (this.modularWorldServerThread != null) this.modularWorldServerThread.dispose();
    if (this.modularWorldClient != null) this.modularWorldClient.dispose();

    if (modularWorldServer != null) {
      Log.debug("ModularWorldServer set");
      modularWorldServerThread = new ModularWorldServerThread(modularWorldServer);
      modularWorldServerThread.start();
    } else {
      Log.debug("ModularWorldServer set to null");
    }
    if (modularWorldClient != null) {
      Log.debug("ModularWorldClient set");
      modularWorldClient.create();
      modularWorldClient.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
      this.modularWorldClient = modularWorldClient;
    } else {
      Log.debug("ModularWorldClient set to null");
    }
  }

  @Override
  public void create() {
    try {
      Thread.currentThread().setName("MAIN");
      ModularWorld.setup();
      Sided.setup(Side.Client);
      Gdx.graphics.setTitle(Branding.DEBUG);
      setMenu(new MainMenu());
    } catch (Exception e) {
      Debug.crash(e);
    }
  }

  @Override
  public void resize(int width, int height) {
    try {
      Fonts.resize();
      if (menu != null) menu.resize(width, height);
      if (modularWorldClient != null) modularWorldClient.resize(width, height);
    } catch (Exception e) {
      Debug.crash(e);
    }
  }

  @Override
  public void render() {
    try {
      Memory.update();
      Gdx.gl20.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
      Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
      if (menu == null) {
        Gdx.input.setCursorCatched(true);
      } else {
        Gdx.input.setCursorCatched(false);
      }
      if (modularWorldClient != null) modularWorldClient.render();
      if (menu != null) menu.render(); //Render menu over client
    } catch (Exception e) {
      Debug.crash(e);
    }
  }

  @Override
  public void pause() {
    try {
      if (modularWorldClient != null) modularWorldClient.pause();
    } catch (Exception e) {
      Debug.crash(e);
    }
  }

  @Override
  public void resume() {
    try {
      if (modularWorldClient != null) modularWorldClient.resume();
    } catch (Exception e) {
      Debug.crash(e);
    }
  }

  @Override
  public void dispose() {
    try {
      if (menu != null) {
        menu.hide();
      }
      ModularWorld.quit(true);
    } catch (Exception e) {
      Debug.crash(e);
    }
  }

  public void gotoMainMenu() {
    setModularWorld(null, null);
    setMenu(new MainMenu());
  }
}
