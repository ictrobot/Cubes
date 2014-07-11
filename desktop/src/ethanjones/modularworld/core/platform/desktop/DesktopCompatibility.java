package ethanjones.modularworld.core.platform.desktop;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import ethanjones.modularworld.core.Branding;
import ethanjones.modularworld.core.compatibility.Compatibility;
import ethanjones.modularworld.graphics.AssetManager;

public class DesktopCompatibility extends Compatibility {

  public final OS os;

  protected DesktopCompatibility() {
    this(Application.ApplicationType.Desktop);
  }

  protected DesktopCompatibility(Application.ApplicationType applicationType) {
    super(applicationType);

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

    }
  }

  public static enum OS {
    Windows, Linux, Mac, Unknown;
  }

}
