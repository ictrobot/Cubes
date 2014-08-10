package ethanjones.modularworld.core.platform.desktop;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.files.FileHandle;
import ethanjones.modularworld.core.compatibility.Compatibility;
import ethanjones.modularworld.graphics.asset.AssetFinder;
import ethanjones.modularworld.graphics.asset.AssetManager;
import ethanjones.modularworld.networking.NetworkingManager;

public class HeadlessCompatibility extends Compatibility {

  public final OS os;
  private final String[] arg;

  protected HeadlessCompatibility(String[] arg) {
    this(Application.ApplicationType.Desktop, arg);
  }

  protected HeadlessCompatibility(Application.ApplicationType applicationType, String[] arg) {
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

  public boolean isHeadless() {
    return true;
  }

  @Override
  public FileHandle getBaseFolder() {
    return getWorkingFolder();
  }

  @Override
  public void setNetworkParameter() {
    if (arg.length > 0) {
      NetworkingManager.NETWORK_PARAMETER = arg[0];
    }
  }

  @Override
  protected void run(ApplicationListener applicationListener) {
    new HeadlessApplication(applicationListener);
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
