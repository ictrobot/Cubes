package ethanjones.cubes.core.event.world.save;

import ethanjones.cubes.core.event.world.WorldEvent;

public class SaveEvent extends WorldEvent {
  public SaveEvent(boolean cancelable) {
    super(cancelable);
  }
}
