package ethanjones.cubes.world.save;

import ethanjones.cubes.core.logging.Log;
import ethanjones.data.Data;
import ethanjones.data.DataGroup;

import com.badlogic.gdx.files.FileHandle;

public class Save {
  public final String name;
  public final FileHandle fileHandle;
  private SaveOptions saveOptions;

  public Save(String name, FileHandle fileHandle) {
    this.name = name;
    this.fileHandle = fileHandle;
    this.fileHandle.mkdirs();
  }

  public SaveOptions getSaveOptions() {
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

  public SaveOptions writeSaveOptions() {
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

  public SaveOptions setSaveOptions(SaveOptions saveOptions) {
    this.saveOptions = saveOptions;
    writeSaveOptions();
    return this.saveOptions;
  }

  @Override
  public String toString() {
    return name;
  }
}
