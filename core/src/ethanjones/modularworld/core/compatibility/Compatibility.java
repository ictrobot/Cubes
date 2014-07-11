package ethanjones.modularworld.core.compatibility;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import ethanjones.modularworld.ModularWorld;
import ethanjones.modularworld.graphics.AssetManager;

public abstract class Compatibility {

  public final Application.ApplicationType applicationType;

  protected Compatibility(Application.ApplicationType applicationType) {
    this.applicationType = applicationType;
  }

  protected static void findAssets(FileHandle parent, AssetManager.AssetFolder parentFolder, String path) {
    for (FileHandle fileHandle : parent.list()) {
      if (fileHandle.isDirectory()) {
        AssetManager.AssetFolder folder = new AssetManager.AssetFolder(fileHandle.name(), parentFolder);
        parentFolder.addFolder(folder);
        findAssets(fileHandle, folder, path + fileHandle.name() + "/");
      } else if (fileHandle.name().endsWith(".png")) {
        parentFolder.addFile(new AssetManager.Asset(fileHandle, path + fileHandle.name(), fileHandle.readBytes(), parentFolder));
      }
    }
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

  public void getAssets(AssetManager assetManager) {
    findAssets(Gdx.files.internal("."), assetManager.assets, "");
  }

}
