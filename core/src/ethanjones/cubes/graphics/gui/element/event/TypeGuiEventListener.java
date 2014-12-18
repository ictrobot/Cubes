package ethanjones.cubes.graphics.gui.element.event;

public abstract class TypeGuiEventListener<T extends GuiEvent> implements GuiEventListener {

  private final Class<? extends T> tClass;
  
  public TypeGuiEventListener(Class<? extends T> tClass) {
    this.tClass = tClass;
  }

  @Override
  public boolean onEvent(GuiEvent event) {
    if (tClass.isAssignableFrom(event.getClass())) {
      return onTypeEvent((T) event);
    }
    return false;
  }

  public abstract boolean onTypeEvent(T t);
}
