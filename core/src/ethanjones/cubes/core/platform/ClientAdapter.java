package ethanjones.cubes.core.platform;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import java.util.concurrent.atomic.AtomicBoolean;

import ethanjones.cubes.core.localization.Localization;
import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.system.Branding;
import ethanjones.cubes.core.system.CubesException;
import ethanjones.cubes.core.system.Debug;
import ethanjones.cubes.graphics.Graphics;
import ethanjones.cubes.graphics.menu.Menu;
import ethanjones.cubes.graphics.menu.MenuManager;
import ethanjones.cubes.graphics.menus.MainMenu;
import ethanjones.cubes.input.InputChain;
import ethanjones.cubes.side.Side;
import ethanjones.cubes.side.client.CubesClient;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.side.server.CubesServer;
import ethanjones.cubes.side.server.integrated.IntegratedServer;

public class ClientAdapter implements AdapterInterface {

  private Menu menu;
  private IntegratedServer cubesServer;
  private CubesClient cubesClient;
  private Thread thread;

  private AtomicBoolean setupClient = new AtomicBoolean(false);
  private AtomicBoolean setupMenu = new AtomicBoolean(false);

  public ClientAdapter() {
    Adapter.setInterface(this);
  }

  @Override
  public void setClient(CubesClient cubesClient) throws UnsupportedOperationException {
    //CubesSecurity.checkSetCubes();
    if (cubesClient != null) {
      this.cubesClient = cubesClient;
      Log.debug("Client set");
      setupClient.set(true);
    } else {
      this.cubesClient = null;
      Log.debug("Client set to null");
      setupClient.set(false);
    }
  }

  @Override
  public void setServer(CubesServer cubesServer) throws UnsupportedOperationException {
    //CubesSecurity.checkSetCubes();
    if (cubesServer != null) {
      if (cubesServer instanceof IntegratedServer) {
        this.cubesServer = (IntegratedServer) cubesServer;
        Log.debug("Server set");
        this.cubesServer.start();
      } else {
        Log.warning("Server can only be set to an IntegratedServer");
      }
    } else {
      this.cubesServer = null;
      Log.debug("Server set to null");
    }
  }

  @Override
  public void setMenu(Menu menu) {
    //CubesSecurity.checkSetMenu();
    Menu old = this.menu;
    if (old != null) {
      old.save();
      InputChain.hideMenu(old);
    }
    this.menu = menu;
    if (menu != null) {
      Log.debug("Menu set to " + menu.getClass().getSimpleName());
      MenuManager.setMenu(menu);
      setupMenu.set(true);
    } else {
      Log.debug("Menu set to null");
      setupMenu.set(true);
    }
  }

  @Override
  public CubesClient getClient() {
    return cubesClient;
  }

  @Override
  public CubesServer getServer() {
    return cubesServer;
  }

  @Override
  public Menu getMenu() {
    return menu;
  }

  @Override
  public Side getSide() {
    return Side.Client;
  }

  @Override
  public void create() {
    try {
      Gdx.graphics.setTitle(Branding.DEBUG);
      thread = Thread.currentThread();
      thread.setName(getSide().name());
      Cubes.setup(this);
      setMenu(new MainMenu());
      Log.info(Localization.get("client.client_loaded"));
    } catch (Exception e) {
      Debug.crash(e);
    }
  }

  @Override
  public void resize(int width, int height) {
    try {
      Graphics.resize();
      if (menu != null) menu.resize(width, height);
      if (cubesClient != null) cubesClient.resize(width, height);
    } catch (Exception e) {
      Debug.crash(e);
    }
  }

  @Override
  public void render() {
    try {
      if (cubesClient == null && menu == null) { //Nothing to render
        Debug.crash(new CubesException("CubesClient and Menu both null"));
      }
      Compatibility.get().render();
      Gdx.gl20.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
      Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
      if (cubesClient != null) {
        if (setupClient.getAndSet(false)) {
          cubesClient.create();
          cubesClient.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
        cubesClient.render();
      } else {
        MenuManager.renderBackground();
      }
      if (menu != null) {
        if (setupMenu.getAndSet(false)) {
          menu.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
          InputChain.showMenu(menu);
        }
        menu.render(); //Render menu over client
      }
      if (menu != null || (cubesClient != null && cubesClient.renderer != null && cubesClient.renderer.noCursorCatching())) {
        Gdx.input.setCursorCatched(false);
      } else {
        Gdx.input.setCursorCatched(true);
      }
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
    Adapter.dispose();
  }

  @Override
  public Thread getThread() {
    return thread;
  }
}
