package ethanjones.cubes.platform;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import ethanjones.cubes.common.logging.Log;
import ethanjones.cubes.common.mod.ModLoader;
import ethanjones.cubes.common.CubesException;
import ethanjones.cubes.common.Debug.UncaughtExceptionHandler;
import ethanjones.cubes.client.graphics.assets.AssetFinder;
import ethanjones.cubes.client.graphics.assets.Assets;
import ethanjones.cubes.common.Side;
import ethanjones.cubes.common.Sided;

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

  public void init(Side side) {
    if (side != null) Sided.getEventBus().register(this);
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

  public FileHandle getWorkingFolder() {
    return Gdx.files.absolute(System.getProperty("user.dir"));
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

  public void render() {

  }

  public abstract int getFreeMemory();

  public boolean handleCrash(Throwable throwable) {
    return true;
  }
}
