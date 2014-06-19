package ethanjones.modularworld.core.settings;

import java.util.ArrayList;

public abstract class Setting<E> {

  public static ArrayList<Setting> restoreFailed = new ArrayList<Setting>();

  protected String name;
  protected SettingGroup parent;
  protected E e;

  public Setting(SettingGroup parent, String name, E e) {
    this.name = name;
    this.parent = parent;
    addToParent();
    setValue(e);
  }

  public void addToParent() {
    if (this.parent != null) {
      this.parent.setSetting(this);
    }
  }

  public void restoreFailed() {
    restoreFailed.add(this);
  }


  public String getName() {
    return name;
  }

  public E getValue() {
    return e;
  }

  public void setValue(E o) {
    this.e = o;
  }

  /**
   * @return new value
   */
  public abstract E next();

  /**
   * @return new value
   */
  public abstract E previous();

  public abstract String getString();

  public abstract void restore(String string);
}
