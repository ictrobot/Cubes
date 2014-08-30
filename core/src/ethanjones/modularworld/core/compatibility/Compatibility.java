package ethanjones.modularworld.core.compatibility;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import ethanjones.modularworld.core.ModularWorldException;
import ethanjones.modularworld.core.adapter.GraphicalAdapter;
import ethanjones.modularworld.core.adapter.HeadlessAdapter;
import ethanjones.modularworld.core.logging.Log;
import ethanjones.modularworld.core.logging.UncaughtExceptionHandler;
import ethanjones.modularworld.graphics.asset.AssetFinder;
import ethanjones.modularworld.graphics.asset.AssetManager;
import ethanjones.modularworld.side.common.ModularWorld;

public abstract class Compatibility {

  public final Application.ApplicationType applicationType;

  protected Compatibility(Application.ApplicationType applicationType) {
    this.applicationType = applicationType;
  }

  public void init() {
    ModularWorld.eventBus.register(this);
  }

  public boolean isHeadless() {
    return false;
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
  public void getAssets(AssetManager assetManager) {
    AssetFinder.findAssets(Gdx.files.internal("assets"), assetManager.assets, "");
  }

  public void logEnvironment() {

  }

  protected abstract void run(ApplicationListener applicationListener);

  public void startModularWorld() {
    ModularWorld.compatibility = this;

    UncaughtExceptionHandler uncaughtExceptionHandler = new UncaughtExceptionHandler();
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
        Log.error("Failed to start", ModularWorldException.getModularWorldException(e));
      } catch (Exception ex) {
        if (ex instanceof ModularWorldException) {
          throw (ModularWorldException) ex;
        } else {
          throw ModularWorldException.getModularWorldException(e);
        }
      }
    }
  }
}
