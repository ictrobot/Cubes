package ethanjones.cubes.core.compatibility;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import ethanjones.cubes.core.adapter.GraphicalAdapter;
import ethanjones.cubes.core.adapter.HeadlessAdapter;
import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.mod.ModLoader;
import ethanjones.cubes.core.system.CubesException;
import ethanjones.cubes.core.system.Debug;
import ethanjones.cubes.graphics.assets.AssetFinder;
import ethanjones.cubes.graphics.assets.Assets;
import ethanjones.cubes.side.Side;
import ethanjones.cubes.side.Sided;

public abstract class Compatibility {

  private static Compatibility compatibility;

  public static Compatibility get() {
    return compatibility;
  }
  public final Application.ApplicationType applicationType;

  protected Compatibility(Application.ApplicationType applicationType) {
    this.applicationType = applicationType;
  }

  public void init(Side side) {
    if (side != null) Sided.getEventBus().register(this);
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

    Debug.UncaughtExceptionHandler uncaughtExceptionHandler = new Debug.UncaughtExceptionHandler();
    Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler);
    Thread.currentThread().setUncaughtExceptionHandler(uncaughtExceptionHandler);

    try {
      if (isHeadless()) {
        run(new HeadlessAdapter());
      } else {
        run(new GraphicalAdapter());
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

  public boolean isHeadless() {
    return false;
  }

  protected abstract void run(ApplicationListener applicationListener);

  public abstract ModLoader getModLoader();
}
