package ethanjones.cubes.world.client;

import ethanjones.cubes.world.save.Gamemode;
import ethanjones.cubes.world.save.Save;
import ethanjones.cubes.world.save.SaveOptions;

public class ClientSaveManager {
  
  public static Save createTemporarySave(String generatorID, Gamemode gamemode, String seedString) {
    Save s = new Save("Temporary", null, true);
    
    SaveOptions options = new SaveOptions();
    options.setWorldSeed(seedString);
    options.worldType = generatorID;
    options.worldGamemode = gamemode;
    s.setSaveOptions(options);
    
    return s;
  }
}
