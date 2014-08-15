package ethanjones.modularworld.graphics.asset;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import ethanjones.modularworld.side.common.ModularWorld;

import java.net.URL;
import java.security.CodeSource;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class AssetFinder {


  /**
   * Uses FileHandles to find assets
   */
  public static void findAssets(FileHandle parent, AssetManager.AssetFolder parentFolder, String path) {
    for (FileHandle fileHandle : parent.list()) {
      if (fileHandle.isDirectory()) {
        AssetManager.AssetFolder folder = new AssetManager.AssetFolder(fileHandle.name(), parentFolder);
        parentFolder.addFolder(folder);
        findAssets(fileHandle, folder, path + fileHandle.name() + "/");
      } else {
        parentFolder.addFile(new AssetManager.Asset(fileHandle, path + fileHandle.name(), parentFolder));
      }
    }
  }

  /**
   * Extracts assets from the jar
   */
  public static void extractAssets(AssetManager assetManager) {
    String assets = "assets";
    try {
      CodeSource src = ModularWorld.class.getProtectionDomain().getCodeSource();

      if (src != null) {
        URL jar = src.getLocation();
        ZipInputStream zip = new ZipInputStream(jar.openStream());
        ZipEntry ze;
        while ((ze = zip.getNextEntry()) != null) {
          String name = ze.getName();
          if (name.startsWith(assets) && !ze.isDirectory()) {
            name = name.substring(ze.getName().lastIndexOf(assets) + assets.length() + 1);
            int index = Math.max(name.lastIndexOf("/"), name.lastIndexOf("\\"));
            if (index == -1) continue;
            System.out.println(name.substring(0, index));
            AssetManager.AssetFolder assetFolder = getAssetFolder(name.substring(0, index), assetManager.assets);
            assetFolder.addFile(new AssetManager.Asset(Gdx.files.internal(ze.getName()), name, assetFolder));
          }
        }
        zip.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static AssetManager.AssetFolder getAssetFolder(String folder, AssetManager.AssetFolder parent) {
    if (parent == null) return null;
    if (folder.isEmpty()) return parent;
    int index = Math.max(folder.lastIndexOf("/"), folder.lastIndexOf("\\"));
    if (index == -1) return parent;
    String n = folder.substring(0, index);
    String f = folder.substring(index + 1);
    return getAssetFolder(f, parent.folders.get(n));
  }

}
