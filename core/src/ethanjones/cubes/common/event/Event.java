package ethanjones.cubes.common.event;

import ethanjones.cubes.common.logging.Log;
import ethanjones.cubes.common.CubesException;
import ethanjones.cubes.common.Sided;

public class Event {

  private final boolean cancelable;
  private final boolean threaded;
  private boolean canceled;

  public Event(boolean cancelable, boolean threaded) {
    this.cancelable = cancelable;
    this.threaded = threaded;
    this.canceled = false;
  }

  public Event post() {
    return Sided.getEventBus().post(this);
  }

  public boolean isCancelable() {
    return cancelable;
  }

  public boolean isThreaded() {
    return threaded;
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
