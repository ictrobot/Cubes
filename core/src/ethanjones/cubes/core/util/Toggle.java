package ethanjones.cubes.core.util;

public abstract class Toggle {

  private boolean enabled = false;

  public boolean isEnabled() {
    return enabled;
  }

  public boolean setEnabled(boolean b) {
    if (b != enabled) {
      toggle();
      return true;
    }
    return false;
  }

  public boolean toggle() {
    if (enabled) {
      doDisable();
    } else {
      doEnable();
    }
    enabled = !enabled;
    return enabled;
  }

  public boolean enable() {
    return setEnabled(true);
  }

  public boolean disable() {
    return setEnabled(false);
  }

  protected abstract void doEnable();

  protected abstract void doDisable();
}
