package ethanjones.cubes.core.platform;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.logging.LogWriter;
import ethanjones.cubes.core.settings.Settings;
import ethanjones.cubes.core.system.CubesException;
import ethanjones.cubes.core.system.Debug.UncaughtExceptionHandler;
import ethanjones.cubes.graphics.assets.AssetFinder;
import ethanjones.cubes.graphics.assets.Assets;
import ethanjones.cubes.side.common.Side;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;

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

  }

  public void init() {

  }

  public void postInit() {

  }

  public void sideInit(Side side) {
    if (side != null) Side.getEventBus().register(this);
  }

  public final Launcher getLauncher() {
    return launcher;
  }

  public final ApplicationType getApplicationType() {
    return applicationType;
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
    return Settings.getBooleanSettingValue(Settings.INPUT_TOUCH);
  }
  
  public boolean guessTouchScreen() {
    return false;
  }

  public void startCubes() {
    compatibility = this;

    Thread.setDefaultUncaughtExceptionHandler(UncaughtExceptionHandler.instance);

    try {
      run(new ClientAdapter());
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
  
  public abstract void _exit(int status);
  
  public abstract String timestamp();
  
  public abstract String line_separator();
}
