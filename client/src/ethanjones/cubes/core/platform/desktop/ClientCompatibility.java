package ethanjones.cubes.core.platform.desktop;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class ClientCompatibility extends DesktopCompatibility {

  protected ClientCompatibility(ClientLauncher clientLauncher, String[] arg) {
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
    config.forceExit = false;

    new LwjglApplication(applicationListener, config);
  }
}
