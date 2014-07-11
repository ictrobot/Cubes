package ethanjones.modularworld.graphics;

import com.badlogic.gdx.files.FileHandle;

import java.util.HashMap;

public class AssetManager {

  public AssetFolder assets;

  public AssetManager() {
    assets = new AssetFolder("assets", null);
  }

  public static class AssetFolder {
    public HashMap<String, AssetFolder> folders;
    public HashMap<String, Asset> files;
    private String name;
    private AssetFolder parent;

    public AssetFolder(String name, AssetFolder parent) {
      this.name = name;
      this.parent = parent;
      folders = new HashMap<String, AssetFolder>();
      files = new HashMap<String, Asset>();
    }

    public void addFolder(AssetFolder assetFolder) {
      folders.put(assetFolder.name, assetFolder);
    }

    public void addFile(Asset asset) {
      files.put(asset.fileHandle.name(), asset);
    }
  }

  public static class Asset {
    public FileHandle fileHandle;
    public String path;
    public byte[] bytes;
    public AssetFolder parent;

    public Asset(FileHandle fileHandle, String path, AssetFolder parent) {
      this(fileHandle, path, fileHandle.readBytes(), parent);
    }


    public Asset(FileHandle fileHandle, String path, byte[] bytes, AssetFolder parent) {
      this.fileHandle = fileHandle;
      this.path = path;
      this.bytes = bytes;
      this.parent = parent;
    }
  }
}
