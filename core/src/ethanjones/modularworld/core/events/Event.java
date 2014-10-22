package ethanjones.modularworld.core.events;

import ethanjones.modularworld.core.logging.Log;
import ethanjones.modularworld.core.system.ModularWorldException;
import ethanjones.modularworld.side.Sided;

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
      Log.error(new ModularWorldException("Event is not cancelable"));
    }
    this.canceled = canceled;
  }

}
