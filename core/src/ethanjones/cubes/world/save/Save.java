package ethanjones.cubes.world.save;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.entity.living.player.Player;
import ethanjones.cubes.networking.server.ClientIdentifier;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.world.reference.AreaReference;
import ethanjones.cubes.world.storage.Area;
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
  private SaveAreaList saveAreaList;
  private int saveAreaListModCount = 0;

  public Save(String name, FileHandle fileHandle) {
    this(name, fileHandle, false);
  }

  public Save(String name, FileHandle fileHandle, boolean readOnly) {
    this.name = name;
    this.fileHandle = fileHandle;
    this.fileHandle.mkdirs();
    folderArea().mkdirs();
    folderAreaList().mkdirs();
    folderPlayer().mkdirs();
    this.readOnly = readOnly;
  }

  public boolean writeArea(Area area) {
    if (readOnly) return false;
    return SaveAreaIO.write(this, area);
  }

  public boolean writeAreas(Area[] areas) {
    int total = 0, written = 0;
    for (Area area : areas) {
      if (writeArea(area)) written++;
      total++;
    }
    Log.debug("Saving areas: wrote " + written + " total " + total);
    return written != 0;
  }

  public Area readArea(int x, int z) {
    SaveAreaList saveAreaList = getSaveAreaList();
    byte[] hash = saveAreaList.getArea(x, z);
    if (hash == null) return null;
    return SaveAreaIO.read(this, x, z, hash);
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
    if (readOnly) return saveAreaList;
    long time = System.currentTimeMillis();
    if (tag == null) {
      if (saveAreaListModCount == saveAreaList.getModCount()) return saveAreaList;
      tag = Long.toString(time);
    }
    saveAreaListModCount = saveAreaList.getModCount();
    FileHandle file = folderAreaList().child(tag);
    try {
      OutputStream outputStream = file.write(false, 8192);
      saveAreaList.write(new DataOutputStream(outputStream), time);
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

  public FileHandle folderPlayer() {
    return fileHandle.child("player");
  }

  @Override
  public String toString() {
    return name;
  }
}
