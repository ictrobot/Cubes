package ethanjones.modularworld.event;

import ethanjones.modularworld.core.exception.CustomException;

public class Event {

  private final boolean cancelable;
  private boolean canceled;

  public Event(boolean cancelable) {
    this.cancelable = cancelable;
    this.canceled = false;
  }

  public boolean isCancelable() {
    return cancelable;
  }

  public void setCanceled(boolean canceled) {
    if (!cancelable) {
      throw new CustomException("Event is not cancelable");
    }
    this.canceled = canceled;
  }

  public boolean isCanceled() {
    return canceled;
  }
}
