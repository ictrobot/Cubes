package ethanjones.modularworld.core.compatibility;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import ethanjones.modularworld.core.Branding;
import ethanjones.modularworld.core.ModularWorldException;
import ethanjones.modularworld.core.ModularWorldWrapper;
import ethanjones.modularworld.core.logging.Log;
import ethanjones.modularworld.graphics.asset.AssetFinder;
import ethanjones.modularworld.graphics.asset.AssetManager;
import ethanjones.modularworld.networking.NetworkingManager;
import ethanjones.modularworld.side.common.ModularWorld;

public abstract class Compatibility {

  public final Application.ApplicationType applicationType;

  protected Compatibility(Application.ApplicationType applicationType) {
    this.applicationType = applicationType;
  }

  public void init() {
    ModularWorld.eventBus.register(this);
  }

  public void setNetworkParameter() {

  }

  public boolean isHeadless() {
    return false;
  }

  public boolean graphics() {
    return !isHeadless() && (NetworkingManager.hasAddressToConnectTo() || NetworkingManager.clientNetworking != null);
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
    setNetworkParameter();
    ModularWorld.compatibility = this;

    try {
      run(new ModularWorldWrapper());
    } catch (Exception e) {
      try {
        Log.error(Branding.NAME, "", ModularWorldException.getModularWorldException(e));
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
