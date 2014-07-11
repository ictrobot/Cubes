package ethanjones.modularworld.graphics;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

import java.io.File;

public class AssetManager {

  public AssetFolder assets;

  public AssetManager() {
    assets = new AssetFolder();
  }

  public static class AssetFolder {
    public Array<AssetFolder> folders;
    public Array<Asset> files;

    public AssetFolder() {
      folders = new Array<AssetFolder>();
      files = new Array<Asset>();
    }
  }

  public static class Asset {
    public FileHandle fileHandle;
    public File file;

    public Asset(FileHandle fileHandle) {
      this.fileHandle = fileHandle;
      this.file = fileHandle.file();
    }
  }
}
