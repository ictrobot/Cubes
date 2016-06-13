package ethanjones.cubes.core.event.world.save;

import ethanjones.data.DataGroup;

public class SaveOptionsEvent extends SaveEvent {
  private final DataGroup dataGroup;

  public SaveOptionsEvent(DataGroup dataGroup) {
    super(false);
    this.dataGroup = dataGroup;
  }

  public DataGroup getDataGroup() {
    return dataGroup;
  }

}
