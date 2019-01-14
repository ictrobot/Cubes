package ethanjones.cubes.core.platform.desktop;

import ethanjones.cubes.core.event.EventHandler;
import ethanjones.cubes.core.event.settings.AddSettingsEvent;
import ethanjones.cubes.core.logging.loggers.FileLogWriter;
import ethanjones.cubes.core.settings.Keybinds;
import ethanjones.cubes.core.settings.Settings;
import ethanjones.cubes.core.settings.type.BooleanSetting;
import ethanjones.cubes.core.settings.type.DropDownSetting;
import ethanjones.cubes.core.system.Branding;
import ethanjones.cubes.core.system.CubesException;
import ethanjones.cubes.core.system.Debug;
import ethanjones.cubes.core.util.Toggle;
import ethanjones.cubes.side.common.Side;

import com.badlogic.gdx.*;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window;
import com.badlogic.gdx.math.MathUtils;

import java.util.Arrays;
import java.util.Comparator;

public class ClientCompatibility extends DesktopCompatibility {

  private static int DEFAULT_WINDOW_WIDTH = 960; // qHD
  private static int DEFAULT_WINDOW_HEIGHT = 540;
  private static int MINIMUM_WINDOW_WIDTH = 768;
  private static int MINIMUM_WINDOW_HEIGHT = 432;

  // store window state before full screen
  private int windowX = 0;
  private int windowY = 0;
  private int windowWidth = DEFAULT_WINDOW_WIDTH;
  private int windowHeight = DEFAULT_WINDOW_HEIGHT;
  private long fullscreenMS = 0;

  private Toggle fullscreen = new Toggle() {
    @Override
    protected void doEnable() {
      if (!fullscreenMode()) throw new CubesException("Failed to enter fullscreen");
    }

    @Override
    protected void doDisable() {
      if (!windowedMode()) throw new CubesException("Failed to enter windowed mode");
      resizeWindowFromScaleFactor(ethanjones.cubes.graphics.Graphics.scaleFactor());
    }
  };

  ClientCompatibility(ClientLauncher clientLauncher, String[] arg) {
    super(clientLauncher, Application.ApplicationType.Desktop, arg);
  }

  @Override
  protected void run(ApplicationListener applicationListener) {
    Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();

    config.setWindowIcon(Files.FileType.Internal,"assets/icon-16x.png", "assets/icon-32x.png", "assets/icon-64x.png", "assets/icon-128x.png");
    config.useVsync(false);
    config.setWindowedMode(windowWidth, windowHeight);
    config.setWindowSizeLimits(768, 432, -1, -1);

    new Lwjgl3Application(applicationListener, config);
  }

  @Override
  public void init() {
    super.init();
    resizeWindowFromScaleFactor(ethanjones.cubes.graphics.Graphics.scaleFactor());
  }

  @EventHandler
  public void scaleFactorResizeWindow(ethanjones.cubes.graphics.Graphics.ScaleFactorChangedEvent event) {
    if (!event.temporary) resizeWindowFromScaleFactor(event.newScaleFactor);
  }

  private void resizeWindowFromScaleFactor(float newScaleFactor) {
    final float scaleFactor = Math.max(newScaleFactor, 1f);

    final Graphics.Monitor monitor = Gdx.graphics.getMonitor();
    final Graphics.DisplayMode mode = getBestDisplayMode(monitor);
    if (mode == null || fullscreen.isEnabled()) return;

    // Client may not be setup yet, delaying to end of the frame ensures it is
    Gdx.app.postRunnable(new Runnable() {
      @Override
      public void run() {
        int wantedMinWidth = (int) Math.min(MINIMUM_WINDOW_WIDTH * scaleFactor, mode.width * 0.8f);
        int wantedMinHeight = (int) Math.min(MINIMUM_WINDOW_HEIGHT * scaleFactor, mode.height * 0.8f);
        int resizeWidth = (int) (DEFAULT_WINDOW_WIDTH * scaleFactor);
        int resizeHeight = (int) (DEFAULT_WINDOW_HEIGHT * scaleFactor);

        Lwjgl3Window window = ((Lwjgl3Graphics) Gdx.graphics).getWindow();

        if (resizeWidth > 0.8 * mode.width || resizeHeight > 0.8 * mode.height) {
          // if window would almost fill screen, maximize
          window.maximizeWindow();
        } else if (resizeWidth >= Gdx.graphics.getWidth() && resizeHeight >= Gdx.graphics.getHeight()) {
          // maintain center of window if possible without going off edge of screen
          int posX = MathUtils.clamp(
              window.getPositionX() - ((resizeWidth - Gdx.graphics.getWidth()) / 2),
              monitor.virtualX + 32,
              monitor.virtualX + mode.width - resizeWidth - 32);
          int posY = MathUtils.clamp(
              window.getPositionY() - ((resizeHeight - Gdx.graphics.getHeight()) / 2),
              monitor.virtualY + 32,
              monitor.virtualY + mode.height - resizeHeight - 32);
          window.setPosition(posX, posY);

          // first set minimum size to desired size to force expansion, then reset to desired minimum size
          window.setSizeLimits(resizeWidth, resizeHeight, -1, -1);
        }

        window.setSizeLimits(wantedMinWidth, wantedMinHeight, -1, -1);
      }
    });
  }

  @EventHandler
  public void addSettings(AddSettingsEvent e) {
    Settings.addSetting("client.graphics.vsync", new BooleanSetting(true) {
      @Override
      public void onChange() {
        super.onChange();
        Gdx.graphics.setVSync(get());
      }
    });
    Settings.getBaseSettingGroup().getChildGroups().get("graphics").add("client.graphics.vsync");

    Settings.addSetting("client.graphics.fullscreen", new DropDownSetting("windowedBorderless", "fullscreen") {
      @Override
      public void onChange() {
        super.onChange();
        if (fullscreen.isEnabled()) {
          fullscreen.disable();
          fullscreen.enable();
        }
      }
    });
    Settings.getBaseSettingGroup().getChildGroups().get("graphics").add("client.graphics.fullscreen");
  }

  @Override
  public void update() {
    super.update();
    if (!Side.isServer() && Keybinds.isJustPressed(Keybinds.KEYBIND_FULLSCREEN) && Math.abs(System.currentTimeMillis() - fullscreenMS) > 100) {
      fullscreenMS = System.currentTimeMillis(); // when going into fullscreen, key events refire, so cooldown is needed to prevent instantly leaving fullscreen
      fullscreen.toggle();
    }
  }

  private boolean windowedMode() {
    Gdx.graphics.setUndecorated(false);
    Gdx.graphics.setResizable(true);
    ((Lwjgl3Graphics) Gdx.graphics).getWindow().setPosition(windowX, windowY);

    return Gdx.graphics.setWindowedMode(windowWidth, windowHeight);
  }

  private Graphics.DisplayMode getBestDisplayMode(Graphics.Monitor monitor) {
    Graphics.DisplayMode[] displayModes = Gdx.graphics.getDisplayModes(monitor);
    if (displayModes.length == 0) return null;
    // best first
    Arrays.sort(displayModes, new Comparator<Graphics.DisplayMode>() {
      @Override
      public int compare(Graphics.DisplayMode o1, Graphics.DisplayMode o2) {
        int i1 = o1.width * o1.height;
        int i2 = o2.width * o2.height;
        if (i1 < i2) return 1;
        if (i1 > i2) return -1;

        if (o1.refreshRate < o2.refreshRate) return 1;
        if (o1.refreshRate > o2.refreshRate) return -1;

        if (o1.bitsPerPixel < o2.bitsPerPixel) return 1;
        if (o1.bitsPerPixel > o2.bitsPerPixel) return -1;

        return 0;
      }
    });
    return displayModes[0];
  }

  private boolean fullscreenMode() {
    Lwjgl3Window window = ((Lwjgl3Graphics) Gdx.graphics).getWindow();

    windowWidth = Gdx.graphics.getWidth();
    windowHeight = Gdx.graphics.getHeight();
    windowX = window.getPositionX();
    windowY = window.getPositionY();

    Graphics.Monitor monitor = Gdx.graphics.getMonitor();
    Graphics.DisplayMode mode = getBestDisplayMode(monitor);
    if (mode == null) return false;

    if ("windowedBorderless".equals(((DropDownSetting) Settings.getSetting("client.graphics.fullscreen")).getSelected())) {
      Gdx.graphics.setUndecorated(true);
      Gdx.graphics.setResizable(false);
      window.setPosition(monitor.virtualX, monitor.virtualY);
      return Gdx.graphics.setWindowedMode(mode.width, mode.height);
    } else {
      return Gdx.graphics.setFullscreenMode(mode);
    }
  }
  
  @Override
  public boolean handleCrash(Throwable throwable) {
    if (Branding.IS_DEBUG) return false; // don't open if in debug
    System.out.println("Trying to open crash dialog!");
    return ClientCrashHandler.handle(Debug.getLogString(FileLogWriter.file.getAbsolutePath()));
  }

}
