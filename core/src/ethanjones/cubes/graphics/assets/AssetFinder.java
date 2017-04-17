package ethanjones.cubes.graphics.assets;

import com.badlogic.gdx.files.FileHandle;

public class AssetFinder {

  /**
   * Uses FileHandles to find assets
   */
  public static AssetManager findAssets(FileHandle parent, String assetManagerName) {
    AssetManager assetManager = new AssetManager(assetManagerName);
    findAssets(parent, assetManager, "");
    addAssetManager(assetManager);
    return assetManager;
  }

  private static void findAssets(FileHandle parent, AssetManager assetManager, String path) {
    for (FileHandle fileHandle : parent.list()) {
      if (fileHandle.isDirectory()) {
        findAssets(fileHandle, assetManager, path + fileHandle.name() + "/");
      } else {
        String name = path + fileHandle.name();
        assetManager.assets.put(name, new Asset(assetManager, name, fileHandle));
      }
    }
  }
  
  public static void addAssetManager(AssetManager assetManager) {
    Assets.assetManagers.put(assetManager.getName(), assetManager);
  }
}
