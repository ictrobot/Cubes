package ethanjones.cubes.core.event;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.system.CubesException;
import ethanjones.cubes.side.common.Side;

public class Event {

  private final boolean cancelable;
  private final boolean sided;
  private boolean canceled;

  public Event(boolean cancelable, boolean sided) {
    this.cancelable = cancelable;
    this.sided = sided;
    this.canceled = false;
  }

  public Event post() {
    if (sided) return Side.getSidedEventBus().post(this);
    return EventBus.GLOBAL_EVENTBUS.post(this);
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
