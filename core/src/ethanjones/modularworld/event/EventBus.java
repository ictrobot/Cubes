package ethanjones.modularworld.event;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

public class EventBus {

  private HashMap<Class<? extends Event>, List<Method>> data = new HashMap<Class<? extends Event>, List<Method>>();

  public void register(Class<?> c) {
    try {
      for (Method method : c.getDeclaredMethods()) {
      }
    } catch (Exception e) {

    }
  }

}
