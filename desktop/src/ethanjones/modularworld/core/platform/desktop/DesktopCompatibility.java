package ethanjones.modularworld.core.platform.desktop;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.files.FileHandle;
import ethanjones.modularworld.core.Branding;
import ethanjones.modularworld.core.compatibility.Compatibility;
import ethanjones.modularworld.graphics.asset.AssetFinder;
import ethanjones.modularworld.graphics.asset.AssetManager;
import ethanjones.modularworld.networking.NetworkingManager;

public class DesktopCompatibility extends Compatibility {

  public final OS os;
  private final String[] arg;

  protected DesktopCompatibility(String[] arg) {
    this(Application.ApplicationType.Desktop, arg);
  }

  protected DesktopCompatibility(Application.ApplicationType applicationType, String[] arg) {
    super(applicationType);
    this.arg = arg;

    String str = (System.getProperty("os.name")).toUpperCase();
    if (str.contains("WIN")) {
      os = OS.Windows;
    } else if (str.contains("MAC")) {
      os = OS.Mac;
    } else if (str.contains("LINUX")) {
      os = OS.Linux;
    } else {
      os = OS.Unknown;
    }
  }

  @Override
  public void setNetworkParameter() {
    if (arg.length > 0) {
      NetworkingManager.NETWORK_PARAMETER = arg[0];
    }
  }

  @Override
  protected void run(ApplicationListener applicationListener) {
    LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
    config.vSyncEnabled = false;
    config.foregroundFPS = 0;
    config.backgroundFPS = 0;
    new LwjglApplication(applicationListener, config);
  }

  public FileHandle getBaseFolder() {
    FileHandle homeDir = Gdx.files.absolute(System.getProperty("user.home"));
    switch (os) {
      case Windows:
        return Gdx.files.absolute(System.getenv("APPDATA")).child(Branding.NAME);
      case Mac:
        return homeDir.child("Library").child("Application Support").child(Branding.NAME);
      default:
        return homeDir.child("." + Branding.NAME);
    }
  }

  @Override
  public void getAssets(AssetManager assetManager) {
    if (Gdx.files.internal("version").file().exists()) {
      super.getAssets(assetManager);
      return;
    } else {//files are in a jar
      AssetFinder.extractAssets(assetManager);
    }
  }

  public static enum OS {
    Windows, Linux, Mac, Unknown;
  }

}
