package ethanjones.modularworld.core.compatibility;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import ethanjones.modularworld.ModularWorld;
import ethanjones.modularworld.graphics.AssetManager;

import java.io.File;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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

  public void getAssets(AssetManager assetManager) {
    findAssets(Gdx.files.internal("assets"), assetManager.assets, "");
  }

  protected void findAssets(FileHandle parent, AssetManager.AssetFolder parentFolder, String path) {
    for (FileHandle fileHandle : parent.list()) {
      if (fileHandle.isDirectory()) {
        AssetManager.AssetFolder folder = new AssetManager.AssetFolder(fileHandle.name(), parentFolder);
        parentFolder.addFolder(folder);
        findAssets(fileHandle, folder, path + fileHandle.name() + "/");
      } else if (fileHandle.name().endsWith(".png")) {
        parentFolder.addFile(new AssetManager.Asset(fileHandle, path + fileHandle.name(), parentFolder));
      }
    }
  }

  /**
   * Extracts from jar
   */
  protected void extractAssets(AssetManager assetManager) {
    String assets = "assets";
    try {
      CodeSource src = Compatibility.class.getProtectionDomain().getCodeSource();
      List<String> list = new ArrayList<String>();

      if (src != null) {
        URL jar = src.getLocation();
        ZipInputStream zip = new ZipInputStream(jar.openStream());
        ZipEntry ze = null;

        while ((ze = zip.getNextEntry()) != null) {
          String name = ze.getName();
          if (name.startsWith(assets)) {
            name = name.substring(ze.getName().lastIndexOf(assets) + assets.length() + 1);
            AssetManager.AssetFolder assetFolder = getAssetFolder(name.substring(0, name.lastIndexOf(File.separator)), assetManager.assets);
            assetFolder.addFile(new AssetManager.Asset(Gdx.files.internal(name), name, assetFolder));
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private AssetManager.AssetFolder getAssetFolder(String folder, AssetManager.AssetFolder parent) {
    if (parent == null) return null;
    if (folder.isEmpty()) return parent;
    String n = folder.substring(0, folder.indexOf(File.separator));
    String f = folder.substring(folder.indexOf(File.separator) + 1);
    return getAssetFolder(f, parent.folders.get(n));
  }

}
