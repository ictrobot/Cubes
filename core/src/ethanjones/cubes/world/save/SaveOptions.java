package ethanjones.cubes.world.save;

import ethanjones.cubes.core.system.Branding;
import ethanjones.cubes.core.system.CubesException;
import ethanjones.cubes.world.World;
import ethanjones.data.DataGroup;
import ethanjones.data.DataParser;

import com.badlogic.gdx.math.MathUtils;

public class SaveOptions implements DataParser {

  public long worldSeed = MathUtils.random.nextLong();
  public String worldSeedString = String.valueOf(worldSeed);
  public int worldTime = World.MAX_TIME / 4;
  public int worldPlayingTime = 0;
  public String worldType = "core:smooth";
  public Gamemode worldGamemode = Gamemode.survival;
  public DataGroup idManager = new DataGroup();
  
  public long lastOpenedTime = 0;
  public int lastVersionMajor, lastVersionMinor, lastVersionPoint, lastVersionBuild;
  public String lastVersionHash;

  @Override
  public DataGroup write() {
    DataGroup dataGroup = new DataGroup();
    dataGroup.put("saveVersion", 0);
    dataGroup.put("worldSeed", worldSeed);
    dataGroup.put("worldSeedStr", worldSeedString);
    dataGroup.put("worldTime", worldTime);
    dataGroup.put("worldPlayingTime", worldPlayingTime);
    dataGroup.put("worldType", worldType);
    dataGroup.put("worldGamemode", worldGamemode.name());
    dataGroup.put("idManager", idManager);
  
    dataGroup.put("lastOpenedTime", System.currentTimeMillis());
  
    DataGroup version = dataGroup.getGroup("lastVersion");
    version.put("major", Branding.VERSION_MAJOR);
    version.put("minor", Branding.VERSION_MINOR);
    version.put("point", Branding.VERSION_POINT);
    version.put("build", Branding.VERSION_BUILD);
    version.put("hash", Branding.VERSION_HASH);
    
    return dataGroup;
  }

  @Override
  public void read(DataGroup dataGroup) {
    if (dataGroup.getInteger("saveVersion") != 0) throw new CubesException("Invalid save version");
    worldSeed = dataGroup.getLong("worldSeed");
    worldSeedString = dataGroup.getString("worldSeedStr");
    worldTime = dataGroup.getInteger("worldTime");
    worldPlayingTime = dataGroup.getInteger("worldPlayingTime");
    worldType = dataGroup.getString("worldType");
    worldGamemode = Gamemode.valueOf(dataGroup.getString("worldGamemode"));
    idManager = dataGroup.getGroup("idManager");
  
    lastOpenedTime = dataGroup.getLong("lastOpenedTime");
  
    DataGroup version = dataGroup.getGroup("lastVersion");
    lastVersionMajor = version.getInteger("major");
    lastVersionMinor = version.getInteger("minor");
    lastVersionPoint = version.getInteger("point");
    lastVersionBuild = version.getInteger("build");
    lastVersionHash = version.getString("hash");
  }
  
  public void setWorldSeed(String seedString) {
    long seed = 0;
    try {
      seed = Long.parseLong(seedString);
    } catch (NumberFormatException e) {
      if (seedString.isEmpty()) {
        seed = MathUtils.random.nextLong();
      } else {
        seed = seedString.hashCode();
      }
    }
    this.worldSeed = seed;
    this.worldSeedString = seedString;
  }
}
