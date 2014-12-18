package ethanjones.cubes.graphics.gui.element;

public abstract class TypeGuiElement<T> extends SimpleGuiElement {

  protected T t;

  public TypeGuiElement(T t) {
    this.t = t;
  }

  public T get() {
    return t;
  }

  public void set(T t) {
    this.t = t;
  }
}
