package ethanjones.cubes.core.events;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.system.CubesException;
import ethanjones.cubes.side.Sided;

public class Event {

  private final boolean cancelable;
  private boolean canceled;

  public Event(boolean cancelable) {
    this.cancelable = cancelable;
    this.canceled = false;
  }

  public boolean post() {
    return Sided.getEventBus().post(this);
  }

  public boolean isCancelable() {
    return cancelable;
  }

  public boolean isCanceled() {
    return canceled;
  }

  public void setCanceled(boolean canceled) {
    if (!cancelable) {
      Log.error(new CubesException("Event is not cancelable"));
    }
    this.canceled = canceled;
  }

}
