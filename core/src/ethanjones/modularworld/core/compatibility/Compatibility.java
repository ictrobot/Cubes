package ethanjones.modularworld.core.compatibility;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import ethanjones.modularworld.ModularWorld;
import ethanjones.modularworld.core.logging.Log;
import ethanjones.modularworld.graphics.AssetManager;

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
        parentFolder.addFile(new AssetManager.Asset(fileHandle, path + fileHandle.name(), fileHandle.readBytes(), parentFolder));
      }
    }
  }

  /**
   * Extracts from jar
   */
  protected void extractAssets(AssetManager assetManager) {
    try {
      CodeSource src = Compatibility.class.getProtectionDomain().getCodeSource();
      List<String> list = new ArrayList<String>();

      if (src != null) {
        URL jar = src.getLocation();
        ZipInputStream zip = new ZipInputStream(jar.openStream());
        ZipEntry ze = null;

        while ((ze = zip.getNextEntry()) != null) {
          if (ze.getName().startsWith("assets")) {
            String name = ze.getName().substring(ze.getName().lastIndexOf("assets"));
            Log.info(name);
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
