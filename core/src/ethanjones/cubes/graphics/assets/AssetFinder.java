package ethanjones.cubes.graphics.assets;

import ethanjones.cubes.core.logging.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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
      ZipInputStream zip = new ZipInputStream(getInputStream(jar));
      ZipEntry ze;
      while ((ze = zip.getNextEntry()) != null) {
        String name = ze.getName().replace("\\", "/");
        if (name.startsWith(assets) && !ze.isDirectory()) {
          name = name.substring(ze.getName().lastIndexOf(assets) + assets.length() + 1);
          assetManager.assets.put(name, new Asset(assetManager, name, Gdx.files.internal(ze.getName())));
        }
      }
      zip.close();
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

  private static InputStream getInputStream(URL jar) throws IOException {
    //to fix offset caused by launch4j
    InputStream is = jar.openStream();
    Log.debug("Scanning for jar...");
    long offset = 0;
    boolean found = false;
    try {
      while (true) {
        if (getUnsignedInt(is) == 0x04034b50L) {
          found = true;
          break;
        }
        offset += 4;
      }
    } catch (IOException ignored) {
    }
    is.close();

    InputStream finalIS = jar.openStream();
    if (!found) {
      Log.debug("Failed to find start");
    } else {
      Log.debug("Skipping " + offset + " bytes until start of jar [" + finalIS.skip(offset) + "]");
      if (finalIS.markSupported()) finalIS.mark(Integer.MAX_VALUE);
    }
    return finalIS;
  }

  private static long getUnsignedInt(InputStream inputStream) throws IOException {
    long a = inputStream.read();
    if (a == -1) throw new IOException();
    long b = inputStream.read();
    if (b == -1) throw new IOException();
    long c = inputStream.read();
    if (c == -1) throw new IOException();
    long d = inputStream.read();
    if (d == -1) throw new IOException();

    b <<= 8;
    c <<= 16;
    d <<= 24;
    return a | b | c | d;
  }
}
