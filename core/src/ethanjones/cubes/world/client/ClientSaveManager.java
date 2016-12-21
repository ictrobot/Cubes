package ethanjones.cubes.world.client;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.platform.Compatibility;
import ethanjones.cubes.world.save.Gamemode;
import ethanjones.cubes.world.save.Save;
import ethanjones.cubes.world.save.SaveOptions;
import ethanjones.cubes.world.save.SaveState;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;

public class ClientSaveManager {

  public static FileHandle getSavesFolder() {
    return Compatibility.get().getBaseFolder().child("saves");
  }

  public static Save[] getSaves() {
    FileHandle clientSavesFolder = getSavesFolder();
    if (!clientSavesFolder.isDirectory()) return new Save[0];
    FileHandle[] list = clientSavesFolder.list();
    Save[] saves = new Save[list.length];
    for (int i = 0; i < list.length; i++) {
      saves[i] = new Save(list[i].name(), list[i]);
    }
    return saves;
  }

  public static Save createSave(String name, String generatorID, Gamemode gamemode, String stringSeed) {
    if (name != null) name = name.trim();
    if (name == null || name.isEmpty()) name = "world-" + Integer.toHexString(MathUtils.random.nextInt());
    FileHandle folder = getSavesFolder();
    FileHandle handle = folder.child(name);
    handle.mkdirs();
    Save s = new Save(name, handle);

    long seed = 0;
    try {
      seed = Long.parseLong(stringSeed);
    } catch (NumberFormatException e) {
      if (stringSeed.isEmpty()) {
        seed = MathUtils.random.nextLong();
      } else {
        seed = stringSeed.hashCode();
      }
    }

    SaveOptions options = new SaveOptions();
    options.worldSeed = seed;
    options.worldType = generatorID;
    options.worldGamemode = gamemode;
    s.setSaveOptions(options);
  
    SaveState state = new SaveState();
    s.setSaveState(state);

    return s;
  }

  public static void deleteSave(Save save) {
    if (save.fileHandle.deleteDirectory()) {
      Log.info("Deleted save " + save.name);
    } else {
      Log.warning("Failed to delete save " + save.name);
    }
  }
}
