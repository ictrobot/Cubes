package ethanjones.cubes.core.adapter;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.system.Branding;
import ethanjones.cubes.core.system.CubesSecurity;
import ethanjones.cubes.core.system.Debug;
import ethanjones.cubes.core.system.Memory;
import ethanjones.cubes.graphics.menu.Fonts;
import ethanjones.cubes.graphics.menu.Menu;
import ethanjones.cubes.graphics.menu.MenuManager;
import ethanjones.cubes.graphics.menu.menus.MainMenu;
import ethanjones.cubes.side.Side;
import ethanjones.cubes.side.Sided;
import ethanjones.cubes.side.client.CubesClient;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.side.server.CubesServer;
import ethanjones.cubes.side.server.CubesServerThread;

public class GraphicalAdapter implements ApplicationListener {

  public static GraphicalAdapter instance;

  private Menu menu;
  private CubesServerThread cubesServerThread;
  private CubesClient cubesClient;

  public GraphicalAdapter() {
    GraphicalAdapter.instance = this;
  }

  public Menu getMenu() {
    return menu;
  }

  public void setMenu(Menu menu) {
    CubesSecurity.checkSetMenu();
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

  public void setCubes(CubesServer cubesServer, CubesClient cubesClient) {
    CubesSecurity.checkSetMW();
    if (cubesServer != null) {
      cubesServerThread = new CubesServerThread(cubesServer);
      CubesServer.instance = cubesServer;
      Log.debug("Server set");
      cubesServerThread.start();
    } else {
      cubesServerThread = null;
      CubesServer.instance = null;
      Log.debug("Server set to null");
    }
    if (cubesClient != null) {
      this.cubesClient = cubesClient;
      CubesClient.instance = cubesClient;
      Log.debug("Client set");
      cubesClient.create();
      cubesClient.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    } else {
      this.cubesClient = null;
      CubesClient.instance = null;
      Log.debug("Client set to null");
    }
  }

  @Override
  public void create() {
    try {
      Gdx.graphics.setTitle(Branding.DEBUG);
      Thread.currentThread().setName(Side.Client.name());
      Cubes.setup();
      Sided.setup(Side.Client);
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
      if (cubesClient != null) cubesClient.resize(width, height);
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
      if (cubesClient != null) cubesClient.render();
      if (menu != null) menu.render(); //Render menu over client
    } catch (Exception e) {
      Debug.crash(e);
    }
  }

  @Override
  public void pause() {
    try {
      if (cubesClient != null) cubesClient.pause();
    } catch (Exception e) {
      Debug.crash(e);
    }
  }

  @Override
  public void resume() {
    try {
      if (cubesClient != null) cubesClient.resume();
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
      Cubes.quit(true);
    } catch (Exception e) {
      Debug.crash(e);
    }
  }
}
