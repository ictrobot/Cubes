package ethanjones.cubes.world.save;

import ethanjones.cubes.core.id.IDManager;
import ethanjones.cubes.core.util.UUID;
import ethanjones.cubes.entity.living.player.Player;
import ethanjones.cubes.networking.server.ClientIdentifier;
import ethanjones.cubes.world.generator.smooth.Cave;
import ethanjones.cubes.world.reference.AreaReference;
import ethanjones.cubes.world.storage.Area;
import ethanjones.cubes.world.storage.AreaMap;
import ethanjones.cubes.world.thread.WorldTasks;

import com.badlogic.gdx.files.FileHandle;

public class Save {
  public final String name;
  public final FileHandle fileHandle;
  public final boolean readOnly;
  private SaveOptions saveOptions;

  public Save(String name, FileHandle fileHandle) {
    this(name, fileHandle, true);
  }

  public Save(String name, FileHandle fileHandle, boolean readOnly) {
    this.name = name;
    this.fileHandle = fileHandle;
    this.readOnly = true;
  }

  public boolean writeArea(Area area) {
    return false;
  }

  public void writeAreas(AreaMap areas) {
    WorldTasks.save(this, areas);
  }

  public Area readArea(int x, int z) {
    return null;
  }

  public void writePlayer(Player player) {

  }

  public void writePlayers() {

  }

  public Player readPlayer(UUID uuid, ClientIdentifier clientIdentifier) {
    return null;
  }

  public void writeCave(AreaReference areaReference, Cave cave) {

  }

  public Cave readCave(AreaReference areaReference) {
    return null;
  }

  public synchronized SaveOptions readSaveOptions() {
    return saveOptions;
  }

  public synchronized SaveOptions getSaveOptions() {
    if (saveOptions == null) {
      saveOptions = new SaveOptions();
    }
    return saveOptions;
  }

  public synchronized SaveOptions writeSaveOptions() {
    return saveOptions;
  }

  public synchronized SaveOptions setSaveOptions(SaveOptions saveOptions) {
    this.saveOptions = saveOptions;
    writeSaveOptions();
    return this.saveOptions;
  }

  @Override
  public String toString() {
    return name;
  }

  public void readIDManager() {
    SaveOptions saveOptions = getSaveOptions();
    IDManager.resetMapping();
    if (saveOptions.idManager.size() == 0) {
      IDManager.generateDefaultMappings();
    } else {
      IDManager.readMapping(saveOptions.idManager);
    }
    saveOptions.idManager = IDManager.writeMapping();
    writeSaveOptions();
  }
}
