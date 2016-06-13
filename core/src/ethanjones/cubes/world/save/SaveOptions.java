package ethanjones.cubes.world.save;

import ethanjones.cubes.core.event.world.save.SaveOptionsReadEvent;
import ethanjones.cubes.core.event.world.save.SaveOptionsWriteEvent;
import ethanjones.data.DataGroup;
import ethanjones.data.DataParser;

public class SaveOptions implements DataParser {

  public String worldSeed;
  public String worldType;

  @Override
  public DataGroup write() {
    DataGroup dataGroup = new DataGroup();
    dataGroup.put("worldSeed", worldSeed);
    dataGroup.put("worldType", worldType);
    new SaveOptionsWriteEvent(dataGroup).post();
    return dataGroup;
  }

  @Override
  public void read(DataGroup dataGroup) {
    worldSeed = dataGroup.getString("worldSeed");
    worldType = dataGroup.getString("worldType");
    new SaveOptionsReadEvent(dataGroup).post();
  }
}
