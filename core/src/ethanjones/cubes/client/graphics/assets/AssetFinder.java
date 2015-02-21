package ethanjones.cubes.client.graphics.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import java.net.URL;
import java.security.CodeSource;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import ethanjones.cubes.common.logging.Log;
import ethanjones.cubes.Cubes;

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

  /**
   * Extracts assets from the jar
   */
  public static AssetManager extractAssets(URL jar, String assetManagerName) {
    AssetManager assetManager = new AssetManager(assetManagerName);
    extractAssets(jar, assetManager);
    addAssetManager(assetManager);
    return assetManager;
  }

  private static void extractAssets(URL jar, AssetManager assetManager) {
    String assets = "assets";
    try {
      CodeSource src = Cubes.class.getProtectionDomain().getCodeSource();

      if (src != null) {
        ZipInputStream zip = new ZipInputStream(jar.openStream());
        ZipEntry ze;
        while ((ze = zip.getNextEntry()) != null) {
          String name = ze.getName().replace("\\", "/");
          if (name.startsWith(assets) && !ze.isDirectory()) {
            name = name.substring(ze.getName().lastIndexOf(assets) + assets.length() + 1);
            assetManager.assets.put(name, new Asset(assetManager, name, Gdx.files.internal(ze.getName())));
          }
        }
        zip.close();
      }
    } catch (Exception e) {
      Log.error("Failed to extract assets", e);
    }
  }

  private static void addAssetManager(AssetManager assetManager) {
    Assets.assetManagers.put(assetManager.getName(), assetManager);
  }

  public static void extractCoreAssets() {
    AssetManager assetManager = new AssetManager(Assets.CORE);
    URL jar = AssetFinder.class.getProtectionDomain().getCodeSource().getLocation();
    extractAssets(jar, assetManager);
    addAssetManager(assetManager);
  }
}
