package ethanjones.modularworld.core.compatibility;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import ethanjones.modularworld.ModularWorld;
import ethanjones.modularworld.graphics.asset.AssetFinder;
import ethanjones.modularworld.graphics.asset.AssetManager;

public abstract class Compatibility {

  public final Application.ApplicationType applicationType;

  protected Compatibility(Application.ApplicationType applicationType) {
    this.applicationType = applicationType;
  }

  public void init() {
    ModularWorld.instance.eventBus.register(this);
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

}
