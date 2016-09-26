package ethanjones.cubes.world.save;

import ethanjones.cubes.world.World;
import ethanjones.data.DataGroup;
import ethanjones.data.DataParser;

public class SaveState implements DataParser {

  public int worldTime = World.MAX_TIME / 4;

  @Override
  public DataGroup write() {
    DataGroup dataGroup = new DataGroup();
    dataGroup.put("worldTime", worldTime);
    return dataGroup;
  }

  @Override
  public void read(DataGroup dataGroup) {
    worldTime = dataGroup.getInteger("worldTime");
  }
}
