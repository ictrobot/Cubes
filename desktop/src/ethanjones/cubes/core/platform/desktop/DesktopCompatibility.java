package ethanjones.cubes.core.platform.desktop;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.mod.ModLoader;
import ethanjones.cubes.core.platform.Adapter;
import ethanjones.cubes.core.platform.CmdLineParser;
import ethanjones.cubes.core.platform.Compatibility;
import ethanjones.cubes.core.system.Branding;
import ethanjones.cubes.graphics.assets.AssetFinder;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;

public abstract class DesktopCompatibility extends Compatibility {

  protected static final FileHandle workingFolder = new FileHandle(System.getProperty("user.dir"));

  static void setup() {
    DesktopSecurityManager.setup();
    DesktopMemoryChecker.setup();
  }

  private final OS os;
  private final String[] arg;
  private DesktopModLoader modLoader;
  protected FileHandle baseFolder = null;

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

    modLoader = new DesktopModLoader();
  }

  public FileHandle getBaseFolder() {
    if (baseFolder != null) return baseFolder;
    if (Adapter.isDedicatedServer()) return workingFolder;
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
    if (!DesktopMemoryChecker.isRunning()) Log.warning("Desktop Memory Checker is disabled!");
  }

  @Override
  public boolean isTouchScreen() {
    return false;
  }

  @Override
  public ModLoader getModLoader() {
    return modLoader;
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
  public void setupCmdLineOptions(CmdLineParser cmdLineParser) {
    cmdLineParser.addBooleanOption("disable-memory-checker", "Disable desktop memory checker");
    cmdLineParser.addStringOption("base-folder", "Base Folder");
  }

  @Override
  public void parseCmdLineOptions(CmdLineParser cmdLineParser) {
    if (cmdLineParser.getOptionValue("disable-memory-checker", null) != null) DesktopMemoryChecker.disable();
    String baseFolder = cmdLineParser.getOptionValue("base-folder", null);
    if (baseFolder != null) this.baseFolder = Gdx.files.absolute(baseFolder);
  }
}
