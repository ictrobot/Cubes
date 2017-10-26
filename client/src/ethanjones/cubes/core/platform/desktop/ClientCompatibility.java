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

import java.util.Arrays;
import java.util.Comparator;

public class ClientCompatibility extends DesktopCompatibility {

  private final static Comparator<Graphics.DisplayMode> displayModeComparator = new Comparator<Graphics.DisplayMode>() {
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
  };

  private int windowX = 0;
  private int windowY = 0;
  private int windowWidth = 960; // qHD
  private int windowHeight = 540;
  private long fullscreenMS = 0;

  private Toggle fullscreen = new Toggle() {
    @Override
    protected void doEnable() {
      if (!fullscreenMode()) throw new CubesException("Failed to enter fullscreen");
    }

    @Override
    protected void doDisable() {
      if (!windowedMode()) throw new CubesException("Failed to enter windowed mode");
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

    new Lwjgl3Application(applicationListener, config);
  }

  @EventHandler
  public void addSettings(AddSettingsEvent e) {
    Settings.addSetting("client.graphics.vsync", new BooleanSetting(false) {
      @Override
      public void onChange() {
        super.onChange();
        Gdx.graphics.setVSync(get());
      }
    });
    Settings.getBaseSettingGroup().getChildGroups().get("graphics").add("client.graphics.vsync");

    Settings.addSetting("client.graphics.fullscreen", new DropDownSetting("Windowed Borderless", "Fullscreen") {
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

  private boolean fullscreenMode() {
    Lwjgl3Window window = ((Lwjgl3Graphics) Gdx.graphics).getWindow();

    windowWidth = Gdx.graphics.getWidth();
    windowHeight = Gdx.graphics.getHeight();
    windowX = window.getPositionX();
    windowY = window.getPositionY();

    Graphics.Monitor monitor = Gdx.graphics.getMonitor();
    Graphics.DisplayMode[] displayModes = Gdx.graphics.getDisplayModes(monitor);
    if (displayModes.length == 0) return false;
    Arrays.sort(displayModes, displayModeComparator);

    if ("Windowed Borderless".equals(((DropDownSetting) Settings.getSetting("client.graphics.fullscreen")).getSelected())) {
      Gdx.graphics.setUndecorated(true);
      Gdx.graphics.setResizable(false);
      window.setPosition(monitor.virtualX, monitor.virtualY);
      return Gdx.graphics.setWindowedMode(displayModes[0].width, displayModes[0].height);
    } else {
      return Gdx.graphics.setFullscreenMode(displayModes[0]);
    }
  }
  
  @Override
  public boolean handleCrash(Throwable throwable) {
    if (Branding.IS_DEBUG) return false; // don't open if in debug
    return ClientCrashHandler.handle(Debug.getLogString(FileLogWriter.file.getAbsolutePath()));
  }

}
