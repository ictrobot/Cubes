package ethanjones.cubes.core.platform.desktop;

import ethanjones.cubes.core.logging.loggers.FileLogWriter;
import ethanjones.cubes.core.settings.Keybinds;
import ethanjones.cubes.core.system.Branding;
import ethanjones.cubes.core.system.Debug;
import ethanjones.cubes.side.common.Side;

import com.badlogic.gdx.*;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

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

  private boolean fullscreen = false;
  private int windowWidth = 960; // qHD
  private int windowHeight = 540;

  ClientCompatibility(ClientLauncher clientLauncher, String[] arg) {
    super(clientLauncher, Application.ApplicationType.Desktop, arg);
  }

  @Override
  protected void run(ApplicationListener applicationListener) {
    LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

    config.addIcon("assets/icon-16x.png", Files.FileType.Internal);
    config.addIcon("assets/icon-32x.png", Files.FileType.Internal);
    config.addIcon("assets/icon-64x.png", Files.FileType.Internal);
    config.addIcon("assets/icon-128x.png", Files.FileType.Internal);

    config.vSyncEnabled = false;
    config.foregroundFPS = 0;
    config.backgroundFPS = 0;

    config.width = windowWidth;
    config.height = windowHeight;

    config.forceExit = false;

    new LwjglApplication(applicationListener, config);
  }

  @Override
  public void update() {
    super.update();
    if (Side.isClient() && Keybinds.isJustPressed(Keybinds.KEYBIND_FULLSCREEN)) {
      if (fullscreen) {
        if (windowedMode()) fullscreen = false;
      } else {
        if (fullscreenMode()) fullscreen = true;
      }
    }
  }

  public boolean windowedMode() {
    return Gdx.graphics.setWindowedMode(windowWidth, windowHeight);
  }

  public boolean fullscreenMode() {
    windowWidth = Gdx.graphics.getWidth();
    windowHeight = Gdx.graphics.getHeight();

    Graphics.DisplayMode[] displayModes = Gdx.graphics.getDisplayModes();
    if (displayModes.length == 0) return false;
    Arrays.sort(displayModes, displayModeComparator);
    return Gdx.graphics.setFullscreenMode(displayModes[0]);
  }
  
  @Override
  public boolean handleCrash(Throwable throwable) {
    if (Branding.IS_DEBUG) return false; // don't open if in debug
    return ClientCrashHandler.handle(Debug.getLogString(FileLogWriter.file.getAbsolutePath()));
  }

}
