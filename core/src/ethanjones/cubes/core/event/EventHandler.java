package ethanjones.cubes.core.event;

public interface EventHandler<E extends Event> {

  public void onEvent(E event);
  
}
