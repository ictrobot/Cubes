package ethanjones.cubes.core.events;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.system.CubesException;

public class EventBus {

  public static class EventHandlerWrapper {

    public final Method method;
    public final Object instance;
    private boolean valid;

    public EventHandlerWrapper(Method method, Object instance) {
      this.method = method;
      this.instance = instance;
      this.valid = true;
    }

    public boolean run(Event event) throws ReflectiveOperationException {
      if (valid) {
        method.invoke(instance, event);
      }
      return !valid;
    }

    public boolean isValid() {
      return valid;
    }

    public void invalidate() {
      this.valid = false;
    }
  }

  private HashMap<Class<? extends Event>, List<EventHandlerWrapper>> data = new HashMap<Class<? extends Event>, List<EventHandlerWrapper>>();

  public EventBus register(Object instance) {
    try {
      for (Method method : instance.getClass().getDeclaredMethods()) {
        EventHandler eventHandler = method.getAnnotation(EventHandler.class);
        if (eventHandler != null) {
          Class<?>[] parameterTypes = method.getParameterTypes();
          if (parameterTypes.length == 1 && Event.class.isAssignableFrom(parameterTypes[0])) {
            register(parameterTypes[0], new EventHandlerWrapper(method, instance));
          } else {
            Log.error(new CubesException("Invalid EventHandler method parameters"));
          }
        }
      }
    } catch (Exception e) {

    }
    return this;
  }

  public EventBus register(Class<?> event, EventHandlerWrapper eventHandlerWrapper) {
    synchronized (this) {
      List<EventHandlerWrapper> eventHandlerWrappers = data.get(event);
      if (eventHandlerWrappers == null) {
        eventHandlerWrappers = new ArrayList<EventHandlerWrapper>();
        data.put((Class<? extends Event>) event, eventHandlerWrappers);
      }
      eventHandlerWrappers.add(eventHandlerWrapper);
      return this;
    }
  }

  public boolean post(Event event) {
    synchronized (this) {
      Class<? extends Event> eventClass = event.getClass();
      while (eventClass != null) {
        List<EventHandlerWrapper> eventHandlerWrappers = data.get(eventClass);
        if (eventHandlerWrappers != null && !eventHandlerWrappers.isEmpty()) {
          Iterator<EventHandlerWrapper> iterator = eventHandlerWrappers.iterator();
          while (iterator.hasNext()) {
            EventHandlerWrapper eventHandlerWrapper = iterator.next();
            try {
              if (eventHandlerWrapper.run(event)) {
                iterator.remove();
              }
            } catch (Exception exception) {
              Log.error(exception);
            }
          }
        }
        Class<?> superclass = eventClass.getSuperclass();
        if (Event.class.isAssignableFrom(superclass)) {
          eventClass = (Class<? extends Event>) superclass;
        } else {
          break;
        }
      }
      return !event.isCanceled();
    }
  }

}
