package ethanjones.cubes.common.event;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ethanjones.cubes.common.logging.Log;
import ethanjones.cubes.common.CubesException;
import ethanjones.cubes.common.util.Executor;

public class EventBus {

  private HashMap<Class<? extends Event>, List<EventWrapper>> data = new HashMap<Class<? extends Event>, List<EventWrapper>>();

  public EventBus register(Object instance) {
    try {
      for (Method method : instance.getClass().getDeclaredMethods()) {
        EventHandler eventHandler = method.getAnnotation(EventHandler.class);
        if (eventHandler != null) {
          Class<?>[] parameterTypes = method.getParameterTypes();
          if (parameterTypes.length == 1 && Event.class.isAssignableFrom(parameterTypes[0])) {
            Class<? extends Event> eventClass = parameterTypes[0].asSubclass(Event.class);
            final List<EventWrapper> list = getList(eventClass);
            synchronized (list) {
              list.add(new EventWrapper(method, instance));
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

  public List<EventWrapper> getList(Class<? extends Event> eventClass) {
    synchronized (this) {
      List<EventWrapper> eventHandlers = data.get(eventClass);
      if (eventHandlers == null) {
        eventHandlers = new ArrayList<EventWrapper>();
        data.put(eventClass, eventHandlers);
      }
      return eventHandlers;
    }
  }

  public <E extends Event> E post(E event) {
    if (event == null) return null;
    if (event.isThreaded()) {
      Executor.execute(new EventCallable(this, event));
      return null; //Still being called
    } else {
      EventCallable.run(this, event);
      return event;
    }
  }

}
