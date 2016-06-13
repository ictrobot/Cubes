package ethanjones.cubes.core.event.world.save;

import ethanjones.data.DataGroup;

public class SaveOptionsReadEvent extends SaveOptionsEvent {
  public SaveOptionsReadEvent(DataGroup dataGroup) {
    super(dataGroup);
  }
}
