package ethanjones.cubes.world.save;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.world.storage.Area;
import ethanjones.data.Data;
import ethanjones.data.DataGroup;

import com.badlogic.gdx.files.FileHandle;

import java.io.*;

public class Save {
  public final String name;
  public final FileHandle fileHandle;
  private SaveOptions saveOptions;
  private SaveAreaList saveAreaList;
  private int saveAreaListModCount = 0;

  public Save(String name, FileHandle fileHandle) {
    this.name = name;
    this.fileHandle = fileHandle;
    this.fileHandle.mkdirs();
    folderArea().mkdirs();
    folderAreaList().mkdirs();
  }

  public boolean writeArea(Area area) {
    return SaveAreaIO.write(this, area);
  }

  public Area readArea(int x, int z) {
    SaveAreaList saveAreaList = getSaveAreaList();
    byte[] hash = saveAreaList.getArea(x, z);
    if (hash == null) return null;
    return SaveAreaIO.read(this, x, z, hash);
  }

  public synchronized SaveOptions getSaveOptions() {
    if (saveOptions == null) {
      saveOptions = new SaveOptions();
      try {
        DataGroup dataGroup = (DataGroup) Data.input(fileHandle.child("options").file());
        saveOptions.read(dataGroup);
      } catch (Exception e) {
        Log.warning("Failed to read save options", e);
        writeSaveOptions();
      }
    }
    return saveOptions;
  }

  public synchronized SaveOptions writeSaveOptions() {
    if (saveOptions != null) {
      try {
        DataGroup dataGroup = saveOptions.write();
        Data.output(dataGroup, fileHandle.child("options").file());
      } catch (Exception e) {
        Log.warning("Failed to write save options", e);
      }
    }
    return saveOptions;
  }

  public synchronized SaveOptions setSaveOptions(SaveOptions saveOptions) {
    this.saveOptions = saveOptions;
    writeSaveOptions();
    return this.saveOptions;
  }

  public synchronized SaveAreaList getSaveAreaList() {
    if (saveAreaList == null) {
      saveAreaList = new SaveAreaList();
      FileHandle tagFile = fileHandle.child("areatag");
      if (tagFile.exists()) {
        String tag = tagFile.readString("UTF-8");
        FileHandle file = folderAreaList().child(tag);
        try {
          BufferedInputStream stream = file.read(8192);
          saveAreaList.read(new DataInputStream(stream));
          stream.close();
        } catch (Exception e) {
          Log.warning("Failed to read save area list", e);
        }
      }
    }
    return saveAreaList;
  }

  public synchronized SaveAreaList writeSaveAreaList(String tag) {
    if (tag == null) {
      if (saveAreaListModCount == saveAreaList.getModCount()) return saveAreaList;
      tag = Long.toString(System.currentTimeMillis());
    }
    saveAreaListModCount = saveAreaList.getModCount();
    FileHandle file = folderAreaList().child(tag);
    try {
      OutputStream outputStream = file.write(false, 8192);
      saveAreaList.write(new DataOutputStream(outputStream));
      outputStream.close();

      fileHandle.child("areatag").writeString(tag, false, "UTF-8");
    } catch (Exception e) {
      Log.warning("Failed to write save area list", e);
    }
    return saveAreaList;
  }

  public FileHandle folderArea() {
    return fileHandle.child("area");
  }

  public FileHandle folderAreaList() {
    return fileHandle.child("arealist");
  }

  @Override
  public String toString() {
    return name;
  }
}
