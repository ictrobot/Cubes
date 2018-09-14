package ethanjones.cubes.core.platform;

import ethanjones.cubes.core.event.EventBus;
import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.logging.LogWriter;
import ethanjones.cubes.core.mod.ModLoader;
import ethanjones.cubes.core.system.CubesException;
import ethanjones.cubes.core.system.Debug.UncaughtExceptionHandler;
import ethanjones.cubes.graphics.assets.AssetFinder;
import ethanjones.cubes.graphics.assets.Assets;
import ethanjones.cubes.side.common.Side;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import net.bytebuddy.dynamic.DynamicType.Loaded;
import net.bytebuddy.dynamic.DynamicType.Unloaded;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default;

public abstract class Compatibility {

  private static Compatibility compatibility;

  public static Compatibility get() {
    return compatibility;
  }

  private final Launcher launcher;
  private final Application.ApplicationType applicationType;

  protected Compatibility(Launcher launcher, Application.ApplicationType applicationType) {
    if (compatibility != null) throw new IllegalStateException();
    this.launcher = launcher;
    this.applicationType = applicationType;
  }

  public void preInit() {
    EventBus.getGlobalEventBus().register(this);
  }

  public void init() {

  }

  public void postInit() {

  }

  public void sideInit(Side side) {
    if (side != null) Side.getSidedEventBus().register(this);
  }

  public final Launcher getLauncher() {
    return launcher;
  }

  public final ApplicationType getApplicationType() {
    return applicationType;
  }

  public FileHandle getBaseFolder() {
    return Gdx.files.absolute(System.getProperty("user.dir"));
  }

  public void makeBaseFolder() {
    FileHandle baseFolder = getBaseFolder();
    baseFolder.mkdirs();
    if (!baseFolder.isDirectory()) {
      FileHandle parent = baseFolder.parent();
      if (!parent.isDirectory()) Log.error("Parent directory '" + parent.path() + "' doesn't exist!");
      throw new CubesException("Failed to make Cubes folder! '" + baseFolder.path() + "'");
    }
  }
  
  public void nomedia(FileHandle folder) {
  
  }

  /**
   * Gets assets using FileHandles by default
   */
  public void setupAssets() {
    AssetFinder.findAssets(Gdx.files.internal("assets"), Assets.CORE);
  }

  public void logEnvironment() {

  }

  public boolean isTouchScreen() {
    return false;
  }

  public void startCubes() {
    compatibility = this;

    Thread.setDefaultUncaughtExceptionHandler(UncaughtExceptionHandler.instance);
    Thread.currentThread().setUncaughtExceptionHandler(UncaughtExceptionHandler.instance);

    try {
      if (applicationType == ApplicationType.HeadlessDesktop) {
        run(new ServerAdapter());
      } else {
        run(new ClientAdapter());
      }
    } catch (Exception e) {
      try {
        Log.error("Failed to start", CubesException.get(e));
      } catch (Exception ex) {
        if (ex instanceof CubesException) {
          throw (CubesException) ex;
        } else {
          throw CubesException.get(e);
        }
      }
    }
  }

  protected abstract void run(ApplicationListener applicationListener);

  public abstract ModLoader getModLoader();

  public void update() {

  }

  public abstract int getFreeMemory();

  public boolean handleCrash(Throwable throwable) {
    return true;
  }

  public abstract boolean functionModifier();

  public LogWriter getCustomLogWriter() {
    return null;
  }

  public String[] getCommandLineArgs() {
    return new String[]{};
  }
  
  public Loaded load(Unloaded unloaded) {
    return unloaded.load(getClass().getClassLoader(), Default.INJECTION);
  }

  public void setupCmdLineOptions(CmdLineParser cmdLineParser) {

  }

  public void parseCmdLineOptions(CmdLineParser cmdLineParser) {

  }
}
