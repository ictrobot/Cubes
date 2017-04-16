package ethanjones.cubes.core.platform.desktop;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.platform.Compatibility;
import ethanjones.cubes.graphics.assets.AssetFinder;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public abstract class DesktopCompatibility extends Compatibility {

  protected static void setup() {
    DesktopSecurityManager.setup();
    DesktopMemoryChecker.setup();
  }

  public final OS os;
  private final String[] arg;

  protected DesktopCompatibility(DesktopLauncher desktopLauncher, Application.ApplicationType applicationType, String[] arg) {
    super(desktopLauncher, applicationType);
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
  public void setupAssets() {
    if (Gdx.files.classpath("version").exists()) { //files in a jar
      Log.debug("Extracting assets");
      AssetFinder.extractCoreAssets();
    } else {
      Log.debug("Finding assets");
      super.setupAssets();
    }
  }

  @Override
  public void logEnvironment() {
    Runtime runtime = Runtime.getRuntime();
    Log.debug("Maximum Memory:     " + (int) (runtime.maxMemory() / 1048576) + "MB");
  }

  @Override
  public boolean isTouchScreen() {
    return false;
  }

  @Override
  public int getFreeMemory() {
    Runtime runtime = Runtime.getRuntime();
    int max = (int) (runtime.maxMemory() / 1048576); //1024 * 1024. Divide to get MB
    int allocated = (int) (runtime.totalMemory() / 1048576);
    int free = (int) (runtime.freeMemory() / 1048576);
    return free + (max - allocated);
  }

  @Override
  public boolean functionModifier() {
    return Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);
  }

  @Override
  public String[] getCommandLineArgs() {
    return arg;
  }
  
  @Override
  public void _exit(int status) {
    System.exit(status);
  }
}
