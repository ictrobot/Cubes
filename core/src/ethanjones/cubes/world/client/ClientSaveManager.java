package ethanjones.cubes.world.client;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.world.save.Gamemode;
import ethanjones.cubes.world.save.Save;
import ethanjones.cubes.world.save.SaveOptions;
import ethanjones.cubes.world.storage.WorldStorage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.MathUtils;
import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;

public class ClientSaveManager {
  
  public static Save[] getSaves() {
    try {
      Preferences preferences = Gdx.app.getPreferences("ethanjones-cubes-savelist");
      String str = preferences.getString("list", "[]");
      JsonArray array = Json.parse(str).asArray();
      Save[] saves = new Save[array.size()];
      for (int i = 0; i < saves.length; i++) {
        saves[i] = new Save(array.get(i).asString());
      }
      return saves;
    } catch (Exception e) {
      Log.warning("Failed to read save list", e);
    }
    return new Save[0];
  }
  
  public static Save createSave(String name, String generatorID, Gamemode gamemode, String seedString) {
    if (name != null) name = name.trim();
    if (name == null || name.isEmpty()) name = "world-" + Integer.toHexString(MathUtils.random.nextInt());
  
    try {
      Preferences preferences = Gdx.app.getPreferences("ethanjones-cubes-savelist");
      String str = preferences.getString("list", "[]");
      JsonArray array = Json.parse(str).asArray();
      array.add(name);
      preferences.putString("list", array.toString());
      preferences.flush();
    } catch (Exception e) {
      Log.warning("Failed to update save list", e);
    }
    
    Save s = new Save(name);
    
    SaveOptions options = new SaveOptions();
    options.setWorldSeed(seedString);
    options.worldType = generatorID;
    options.worldGamemode = gamemode;
    s.setSaveOptions(options);
    
    return s;
  }
  
  public static void deleteSave(Save save) {
    WorldStorage.deleteSave(save.name);
    try {
      Preferences preferences = Gdx.app.getPreferences("ethanjones-cubes-savelist");
      String str = preferences.getString("list");
      JsonArray array = Json.parse(str).asArray();
      int idx = -1;
      for (int i = 0; i < array.size(); i++) {
        if (array.get(i).asString().equals(save.name)) {
          idx = i;
          break;
        }
      }
      if (idx != -1) array.remove(idx);
      preferences.putString("list", array.toString());
      preferences.flush();
    } catch (Exception e) {
      Log.warning("Failed to update save list", e);
    }
  }
  
  public static Save createTemporarySave(String generatorID, Gamemode gamemode, String seedString) {
    Save s = new Save("temp", true);
  
    SaveOptions options = new SaveOptions();
    options.setWorldSeed(seedString);
    options.worldType = generatorID;
    options.worldGamemode = gamemode;
    s.setSaveOptions(options);
    
    return s;
  }
}
