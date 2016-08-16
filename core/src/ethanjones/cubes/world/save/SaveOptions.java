package ethanjones.cubes.world.save;

import ethanjones.data.DataGroup;
import ethanjones.data.DataParser;

import com.badlogic.gdx.math.MathUtils;

public class SaveOptions implements DataParser {

  public long worldSeed = MathUtils.random.nextLong();
  public String worldType = "core:smooth";
  public Gamemode worldGamemode = Gamemode.survival;

  @Override
  public DataGroup write() {
    DataGroup dataGroup = new DataGroup();
    dataGroup.put("worldSeed", worldSeed);
    dataGroup.put("worldType", worldType);
    dataGroup.put("worldGamemode", worldGamemode.name());
    return dataGroup;
  }

  @Override
  public void read(DataGroup dataGroup) {
    worldSeed = dataGroup.getLong("worldSeed");
    worldType = dataGroup.getString("worldType");
    worldGamemode = Gamemode.valueOf(dataGroup.getString("worldGamemode"));
  }
}
