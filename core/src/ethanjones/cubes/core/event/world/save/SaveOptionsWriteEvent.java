package ethanjones.cubes.core.event.world.save;

import ethanjones.data.DataGroup;

public class SaveOptionsWriteEvent extends SaveOptionsEvent {
  public SaveOptionsWriteEvent(DataGroup dataGroup) {
    super(dataGroup);
  }
}
