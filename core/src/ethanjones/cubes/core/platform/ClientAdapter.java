package ethanjones.cubes.core.platform;

import ethanjones.cubes.core.localization.Localization;
import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.settings.Keybinds;
import ethanjones.cubes.core.system.Branding;
import ethanjones.cubes.core.system.CubesException;
import ethanjones.cubes.core.system.Debug;
import ethanjones.cubes.graphics.Graphics;
import ethanjones.cubes.graphics.Screenshot;
import ethanjones.cubes.graphics.menu.Menu;
import ethanjones.cubes.graphics.menu.MenuManager;
import ethanjones.cubes.graphics.menus.ClientErrorMenu.UnresponsiveIntegratedServerMenu;
import ethanjones.cubes.graphics.menus.MainMenu;
import ethanjones.cubes.graphics.menus.SingleplayerLoadingMenu;
import ethanjones.cubes.graphics.menus.SplashMenu;
import ethanjones.cubes.input.InputChain;
import ethanjones.cubes.side.client.CubesClient;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.side.common.Side;
import ethanjones.cubes.side.server.CubesServer;
import ethanjones.cubes.side.server.integrated.IntegratedServer;
import ethanjones.cubes.world.client.ClientSaveManager;
import ethanjones.cubes.world.client.WorldClient;
import ethanjones.cubes.world.save.Gamemode;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;

import java.util.concurrent.atomic.AtomicBoolean;

public class ClientAdapter implements AdapterInterface {

  private Menu menu;
  private IntegratedServer cubesServer;
  private CubesClient cubesClient;
  private CubesCmdLineOptions.ClientCmdLineOptions options;
  private Thread thread;

  private AtomicBoolean shownSplash = new AtomicBoolean(false);
  private AtomicBoolean setupCubes = new AtomicBoolean(false);
  private AtomicBoolean setupClient = new AtomicBoolean(false);
  private AtomicBoolean setupMenu = new AtomicBoolean(false);

  public ClientAdapter() {
    Adapter.setInterface(this);
  }

  @Override
  public void create() {
    try {
      Gdx.graphics.setTitle(Branding.DEBUG);
      thread = Thread.currentThread();
      thread.setName(getSide().name());

      options = new CubesCmdLineOptions.ClientCmdLineOptions();
      options.parse();
      
      Cubes.preInit(this);
    } catch (StopLoopException e) {
      Log.debug(e);
    } catch (Exception e) {
      Debug.crash(e);
    }
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
  public void resize(int width, int height) {
    if (width == 0 || height == 0) return;
    try {
      Graphics.resize(width, height);
      if (menu != null) menu.resize(Graphics.GUI_WIDTH, Graphics.GUI_HEIGHT);
      if (cubesClient != null) cubesClient.resize(Graphics.RENDER_WIDTH, Graphics.RENDER_HEIGHT);
    } catch (StopLoopException e) {
      Log.debug(e);
    } catch (Exception e) {
      Debug.crash(e);
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
        throw new CubesException("Server can only be set to an IntegratedServer");
      }
    } else {
      this.cubesServer = null;
      Log.debug("Server set to null");
    }
  }
  
  private boolean splashScreen() {
    Log.debug("Showing splash screen");
    glClear();
    
    SplashMenu splashMenu = new SplashMenu();
    Graphics.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    splashMenu.resize(Graphics.GUI_WIDTH, Graphics.GUI_HEIGHT);
    splashMenu.render();
    Log.debug("Splash screen rendered");
    
    return true;
  }

  private boolean initCubes() {
    Cubes.init();
    if (options.loadTemporaryWorld) {
      Log.info(Localization.get("client.load_temporary_world"));
      Adapter.setMenu(new SingleplayerLoadingMenu(ClientSaveManager.createTemporarySave("core:smooth", Gamemode.creative, "")));
    } else {
      setMenu(new MainMenu());
      Log.info(Localization.get("client.client_loaded"));
    }
    return true;
  }
  
  @Override
  public void render() {
    if (shownSplash.compareAndSet(false, true) && splashScreen()) return;
    if (setupCubes.compareAndSet(false, true) && initCubes()) return;
    try {
      if (cubesClient == null && menu == null) { //Nothing to render
        Debug.crash(new CubesException("CubesClient and Menu both null"));
      }
      boolean takeScreenshot = Keybinds.isJustPressed(Keybinds.KEYBIND_SCREENSHOT);
      if (takeScreenshot) Screenshot.startScreenshot();

      Compatibility.get().update();
      glClear();
      if (cubesClient != null) {
        if (setupClient.getAndSet(false)) {
          cubesClient.create();
          cubesClient.resize(Graphics.RENDER_WIDTH, Graphics.RENDER_HEIGHT);
        }
        cubesClient.render();
      }
      if (menu != null) {
        if (setupMenu.getAndSet(false)) {
          menu.resize(Graphics.GUI_WIDTH, Graphics.GUI_HEIGHT);
          InputChain.showMenu(menu);
          menu.show();
        }
        if (menu.shouldRenderBackground()) MenuManager.renderBackground();
        menu.render(); //Render menu over client
      }
      if (menu != null || (cubesClient != null && cubesClient.renderer != null && cubesClient.renderer.noCursorCatching())) {
        Gdx.input.setCursorCatched(false);
      } else {
        Gdx.input.setCursorCatched(true);
      }
      if (!Branding.IS_DEBUG && cubesClient != null && cubesServer != null && cubesServer.isRunning() && CubesServer.lastUpdateTime() + 2500 < System.currentTimeMillis()) {
        Log.error("Server is unresponsive");
        Debug.printThreads();
        Adapter.gotoMenu(new UnresponsiveIntegratedServerMenu());
      }

      if (takeScreenshot) Screenshot.endScreenshot();
    } catch (StopLoopException e) {
      Log.debug(e);
    } catch (Exception e) {
      Debug.crash(e);
    }
  }

  private void glClear() {
    try {
      Color skyColour = ((WorldClient) cubesClient.world).getSkyColour();
      Gdx.gl20.glClearColor(skyColour.r, skyColour.g, skyColour.b, skyColour.a);
    } catch (Exception ignored) {
      Gdx.gl20.glClearColor(0, 0, 0, 1f);
    }
    Gdx.gl20.glViewport(0, 0, Graphics.RENDER_WIDTH, Graphics.RENDER_HEIGHT);
    Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
  }

  @Override
  public void setMenu(Menu menu) {
    //CubesSecurity.checkSetMenu();
    Menu old = this.menu;
    if (old != null) {
      old.hide();
      InputChain.hideMenu(old);
    }
    this.menu = menu;
    if (menu != null) {
      Log.debug("Menu set to " + menu.getClass().getSimpleName());
      MenuManager.setMenu(menu);
      setupMenu.set(true);
    } else {
      Log.debug("Menu set to null");
      setupMenu.set(false);
    }
  }

  @Override
  public void pause() {
    try {
      if (cubesClient != null) cubesClient.pause();
    } catch (StopLoopException e) {
      Log.debug(e);
    } catch (Exception e) {
      Debug.crash(e);
    }
  }

  @Override
  public CubesClient getClient() {
    return cubesClient;
  }

  @Override
  public void resume() {
    try {
      if (cubesClient != null) cubesClient.resume();
    } catch (StopLoopException e) {
      Log.debug(e);
    } catch (Exception e) {
      Debug.crash(e);
    }
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
  public void dispose() {
    try {
      Adapter.dispose();
    } catch (StopLoopException ignored) {

    }
  }

  @Override
  public Thread getThread() {
    return thread;
  }
}
