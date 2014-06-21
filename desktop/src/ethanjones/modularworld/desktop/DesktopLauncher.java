package ethanjones.modularworld.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import ethanjones.modularworld.ModularWorld;

public class DesktopLauncher {
  public static void main(String[] arg) {
    LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
    config.vSyncEnabled = false;
    config.foregroundFPS = 0;
    config.backgroundFPS = 0;
    new LwjglApplication(new ModularWorld(), config);
  }
}
