package ethanjones.cubes.core.event;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.system.CubesException;
import ethanjones.cubes.side.common.Side;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class EventBus {

  static final EventBus GLOBAL_EVENTBUS = new EventBus(null);

  public static EventBus getGlobalEventBus() {
    return GLOBAL_EVENTBUS;
  }

  private HashMap<Class<? extends Event>, List<EventWrapper>> data = new HashMap<Class<? extends Event>, List<EventWrapper>>();
  private final Side side;

  public EventBus(Side side) {
    this.side = side;
  }

  public EventBus register(Object instance) {
    try {
      for (Method method : instance.getClass().getMethods()) {
        EventHandler eventHandler = method.getAnnotation(EventHandler.class);
        if (eventHandler != null) {
          Class<?>[] parameterTypes = method.getParameterTypes();
          if (parameterTypes.length == 1 && Event.class.isAssignableFrom(parameterTypes[0])) {
            Class<? extends Event> eventClass = parameterTypes[0].asSubclass(Event.class);
            final List<EventWrapper> list = getList(eventClass);
            synchronized (list) {
              list.add(new EventWrapper(method, instance, eventHandler));
            }
          } else {
            Log.error(new CubesException("Invalid EventHandler method parameters"));
          }
        }
      }
    } catch (Exception e) {
      Log.warning("Exception while registering object " + instance.getClass().getSimpleName(), e);
    }
    return this;
  }

  List<EventWrapper> getList(Class<? extends Event> eventClass) {
    synchronized (this) {
      List<EventWrapper> eventHandlers = data.get(eventClass);
      if (eventHandlers == null) {
        eventHandlers = new ArrayList<EventWrapper>();
        data.put(eventClass, eventHandlers);
      }
      return eventHandlers;
    }
  }

  <E extends Event> E post(E event) {
    event.setSide(side);
    Class<?> c = event.getClass();
    final List<EventWrapper> posted = new ArrayList<EventWrapper>(); //Prevents being posted multiple times to same EventHandler
    while (c != null && Event.class.isAssignableFrom(c)) {
      Class<? extends Event> eventClass = c.asSubclass(Event.class);
      final List<EventWrapper> list = getList(eventClass);
      synchronized (list) {
        Iterator<EventWrapper> iterator = list.iterator();
        while (iterator.hasNext()) {
          EventWrapper wrapper = iterator.next();
          if (posted.contains(wrapper)) continue;
          posted.add(wrapper);
          if (!wrapper.run(event)) {
            iterator.remove();
          }
        }
      }
      c = c.getSuperclass();
    }
    return event;
  }

  public Side getSide() {
    return side;
  }
}
