package ethanjones.modularworld.graphics.assets;

import com.badlogic.gdx.files.FileHandle;

public class Asset {
  private final AssetManager assetManager;
  private final String path;
  private final FileHandle fileHandle;

  protected Asset(AssetManager assetManager, String path, FileHandle fileHandle) {
    this.assetManager = assetManager;
    this.path = path;
    this.fileHandle = fileHandle;
  }

  public AssetManager getAssetManager() {
    return assetManager;
  }

  public String getPath() {
    return path;
  }

  public FileHandle getFileHandle() {
    return fileHandle;
  }

  public String toString() {
    return path;
  }
}
