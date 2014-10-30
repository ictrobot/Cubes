package ethanjones.cubes.core.platform.desktop;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import ethanjones.cubes.core.compatibility.Compatibility;
import ethanjones.cubes.core.mod.ModLoader;
import ethanjones.cubes.core.system.Branding;
import ethanjones.cubes.graphics.assets.AssetFinder;

public abstract class DesktopCompatibility extends Compatibility {

  public final OS os;
  private final String[] arg;
  protected DesktopModLoader modLoader;

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

    modLoader = new DesktopModLoader();
  }

  public FileHandle getBaseFolder() {
    if (isHeadless()) return getWorkingFolder();
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
  public void setupAssets() {
    if (Gdx.files.internal("version").file().exists()) {
      super.setupAssets();
    } else {//files are in a jar
      AssetFinder.extractCoreAssets();
    }
  }

  @Override
  public boolean isTouchScreen() {
    return true;
  }

  @Override
  public ModLoader getModLoader() {
    return modLoader;
  }
}
