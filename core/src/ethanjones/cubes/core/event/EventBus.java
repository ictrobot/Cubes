package ethanjones.cubes.core.event;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import ethanjones.cubes.core.logging.Log;

public class EventBus {

  private HashMap<Class<? extends Event>, List<EventHandler>> data = new HashMap<Class<? extends Event>, List<EventHandler>>();

  public void register(Object instance) {
    if (instance == null) return;
    try {
      Class<?> instanceClass = instance.getClass();
      Class<?>[] interfaces = instanceClass.getInterfaces();
      Type[] genericInterfaces = instanceClass.getGenericInterfaces();
      for (int i = 0; i < interfaces.length; i++) {
        Class<?> anInterface = interfaces[0];
        Type type = genericInterfaces[0];
        if (anInterface == EventHandler.class) {
          if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type actual = parameterizedType.getActualTypeArguments()[0];
            if (actual instanceof Class) {
              Class<? extends Event> aClass = ((Class) actual).asSubclass(Event.class);
              final List<EventHandler> list = getList(aClass);
              synchronized (list) {
                list.add((EventHandler) instance);
              }
            } else {
              Log.warning("Unsupported type for EventHandler method " + type.getClass().getSimpleName() + " in class " + instance.getClass().getSimpleName());
            }
          } else {
            Log.warning("Unsupported type for EventHandler method " + type.getClass().getSimpleName() + " in class " + instance.getClass().getSimpleName());
          }
        }
      }
    } catch (Exception e) {
      Log.warning("Exception while registering object " + instance.getClass().getSimpleName(), e);
    }
  }

  public List<EventHandler> getList(Class<? extends Event> eventClass) {
    synchronized (this) {
      List<EventHandler> eventHandlers = data.get(eventClass);
      if (eventHandlers == null) {
        eventHandlers = new ArrayList<EventHandler>();
        data.put(eventClass, eventHandlers);
      }
      return eventHandlers;
    }
  }

  public <E extends Event> E post(E event) {
    if (event == null) return null;
    Class<?> c = event.getClass();
    final List<EventHandler> posted = new ArrayList<EventHandler>(); //Prevents being posted multiple times to same EventHandler
    while (c != null && Event.class.isAssignableFrom(c)) {
      Class<? extends Event> eventClass = c.asSubclass(Event.class);
      final List<EventHandler> list = getList(eventClass);
      synchronized (list) {
        Iterator<EventHandler> iterator = list.iterator();
        while (iterator.hasNext()) {
          EventHandler eventHandler = iterator.next();
          if (posted.contains(eventHandler)) continue;
          posted.add(eventHandler);
          try {
            eventHandler.onEvent(event);
          } catch (Exception e) {
            iterator.remove();
            Log.warning("EventHandler " + eventHandler.getClass().getSimpleName() + " has been removed as it throw an exception while handling " + event.getClass().getSimpleName());
          }
        }
      }
      c = c.getSuperclass();
    }
    return event;
  }
}
