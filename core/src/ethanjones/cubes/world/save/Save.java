package ethanjones.cubes.world.save;

import ethanjones.cubes.core.IDManager;
import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.entity.living.player.Player;
import ethanjones.cubes.networking.server.ClientIdentifier;
import ethanjones.cubes.side.Sided;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.world.generator.smooth.Cave;
import ethanjones.cubes.world.storage.AreaMap;
import ethanjones.cubes.world.reference.AreaReference;
import ethanjones.cubes.world.storage.Area;
import ethanjones.cubes.world.thread.WorldTasks;
import ethanjones.data.Data;
import ethanjones.data.DataGroup;

import com.badlogic.gdx.files.FileHandle;

import java.io.*;
import java.util.List;
import java.util.UUID;

public class Save {
  public final String name;
  public final FileHandle fileHandle;
  public final boolean readOnly;
  private SaveOptions saveOptions;
  private SaveState saveState;

  public Save(String name, FileHandle fileHandle) {
    this(name, fileHandle, false);
  }

  public Save(String name, FileHandle fileHandle, boolean readOnly) {
    this.name = name;
    this.fileHandle = fileHandle;
    this.readOnly = readOnly;

    if (!this.readOnly) {
      this.fileHandle.mkdirs();
      folderArea().mkdirs();
      folderPlayer().mkdirs();
      folderCave().mkdirs();
    }
  }

  public boolean writeArea(Area area) {
    if (readOnly) return false;
    return SaveAreaIO.write(this, area);
  }

  public void writeAreas(AreaMap areas) {
    WorldTasks.save(this, areas);
  }

  public Area readArea(int x, int z) {
    return SaveAreaIO.read(this, x, z);
  }

  public void writePlayer(Player player) {
    if (readOnly) return;
    FileHandle folder = folderPlayer();
    FileHandle file = folder.child(player.uuid.toString());
    DataGroup data = player.write();
    try {
      Data.output(data, file.file());
    } catch (Exception e) {
      Log.warning("Failed to write player", e);
    }
  }

  public void writePlayers() {
    List<ClientIdentifier> clients = Cubes.getServer().getAllClients();
    for (ClientIdentifier client : clients) {
      writePlayer(client.getPlayer());
    }
  }

  public Player readPlayer(UUID uuid) {
    FileHandle folder = folderPlayer();
    FileHandle file = folder.child(uuid.toString());
    if (!file.exists()) return null;
    try {
      DataGroup data = (DataGroup) Data.input(file.file());
      Player player = new Player(data.getString("username"), uuid);
      player.read(data);
      return player;
    } catch (Exception e) {
      Log.warning("Failed to read player", e);
      return null;
    }
  }

  public void writeCave(AreaReference areaReference, Cave cave) {
    if (readOnly) return;

    FileHandle folder = folderCave();
    FileHandle file = folder.child(areaReference.areaX + "_" + areaReference.areaZ);
    try {
      OutputStream write = file.write(false);
      DataOutputStream dataOutputStream = new DataOutputStream(write);
      cave.write(dataOutputStream);
      write.close();
    } catch (IOException e) {
      Log.warning("Failed to write cave", e);
    }
  }

  public Cave readCave(AreaReference areaReference) {
    FileHandle folder = folderCave();
    FileHandle file = folder.child(areaReference.areaX + "_" + areaReference.areaZ);
    if (!file.exists()) return null;
    try {
      InputStream read = file.read();
      DataInputStream dataInputStream = new DataInputStream(read);
      Cave cave = Cave.read(dataInputStream);
      read.close();
      return cave;
    } catch (IOException e) {
      Log.warning("Failed to read cave", e);
      return null;
    }
  }

  public synchronized SaveOptions readSaveOptions() {
    if (saveOptions == null) {
      saveOptions = new SaveOptions();
      try {
        DataGroup dataGroup = (DataGroup) Data.input(fileHandle.child("options").file());
        saveOptions.read(dataGroup);
        return saveOptions;
      } catch (Exception e) {
        saveOptions = null;
      }
    }
    return saveOptions;
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
    if (!readOnly && saveOptions != null) {
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


  public synchronized SaveState getSaveState() {
    if (saveState == null) {
      saveState = new SaveState();
      try {
        DataGroup dataGroup = (DataGroup) Data.input(fileHandle.child("state").file());
        saveState.read(dataGroup);
      } catch (Exception e) {
        Log.warning("Failed to read save state", e);
        writeSaveState();
      }
    }
    return saveState;
  }

  public synchronized SaveState writeSaveState() {
    if (!readOnly && saveState != null) {
      try {
        DataGroup dataGroup = saveState.write();
        Data.output(dataGroup, fileHandle.child("state").file());
      } catch (Exception e) {
        Log.warning("Failed to write save state", e);
      }
    }
    return saveState;
  }

  public synchronized SaveState setSaveState(SaveState saveState) {
    this.saveState = saveState;
    writeSaveState();
    return this.saveState;
  }

  public FileHandle folderArea() {
    return fileHandle.child("area");
  }

  public FileHandle folderCave() {
    return fileHandle.child("cave");
  }

  public FileHandle folderPlayer() {
    return fileHandle.child("player");
  }

  @Override
  public String toString() {
    return name;
  }

  public void readIDManager() {
    SaveOptions saveOptions = getSaveOptions();
    IDManager idManager = Sided.getIDManager();
    if (saveOptions.idManager.size() == 0) {
      idManager.generateDefault();
    } else {
      idManager.read(saveOptions.idManager);
    }
    saveOptions.idManager = idManager.write();
    writeSaveOptions();
  }
}
