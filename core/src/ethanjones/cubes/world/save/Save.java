package ethanjones.cubes.world.save;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Base64Coder;
import ethanjones.cubes.core.gwt.UUID;
import ethanjones.cubes.core.id.IDManager;
import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.entity.living.player.Player;
import ethanjones.cubes.networking.server.ClientIdentifier;
import ethanjones.cubes.world.storage.Area;
import ethanjones.cubes.world.storage.AreaMap;
import ethanjones.cubes.world.storage.WorldStorage;
import ethanjones.cubes.world.thread.WorldTasks;
import ethanjones.data.Data;
import ethanjones.data.DataGroup;

import java.io.IOException;

public class Save {
  public final String name;
  public final boolean readOnly;
  private SaveOptions saveOptions;

  public Save(String name) {
    this.name = name;
    this.readOnly = WorldStorage.getInterface() == null;
  }

  public Save(String name, boolean readOnly) {
    this.name = name;
    this.readOnly = readOnly || WorldStorage.getInterface() == null;
  }

  public void writeAreas(AreaMap areas) {
    if (readOnly) return;
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
    SaveOptions s = getSaveOptions();
    if (s != null && s.player != null && s.player.size() > 0) {
      try {
        Player player = new Player(s.player.getString("username"), uuid, clientIdentifier);
        player.read(s.player);
        return player;
      } catch (Exception e) {
        Log.warning("Failed to read player", e);
      }
    }
    return null;
  }

  public synchronized SaveOptions getSaveOptions() {
    if (saveOptions == null) {
      saveOptions = new SaveOptions();
      try {
        Preferences preferences = Gdx.app.getPreferences("ethanjones-cubes-saves");
        String str = preferences.getString(name);
        byte[] b = Base64Coder.decode(str, Base64Coder.urlsafeMap);
        DataGroup dg = (DataGroup) Data.input(b);
        saveOptions.read(dg);
      } catch (IOException e) {
        Log.warning("Failed to read save options", e);
      }
    }
    return saveOptions;
  }

  public synchronized SaveOptions writeSaveOptions() {
    if (!readOnly) {
      try {
        DataGroup dg = saveOptions.write();
        byte[] b = Data.output(dg);
        char[] encode = Base64Coder.encode(b, Base64Coder.urlsafeMap);
        String str = new String(encode);
        Preferences preferences = Gdx.app.getPreferences("ethanjones-cubes-saves");
        preferences.putString(name, str);
        preferences.flush();
      } catch (IOException e) {
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
