package ethanjones.cubes.core.event;

import com.badlogic.gdx.utils.reflect.Annotation;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Method;
import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.system.CubesException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class EventBus {

  static final EventBus GLOBAL_EVENTBUS = new EventBus();

  public static EventBus getGlobalEventBus() {
    return GLOBAL_EVENTBUS;
  }

  private HashMap<Class<? extends Event>, List<EventWrapper>> data = new HashMap<Class<? extends Event>, List<EventWrapper>>();

  public EventBus register(Object instance) {
    try {
      for (Method method : ClassReflection.getMethods(instance.getClass())) {
        Annotation eventHandler = method.getDeclaredAnnotation(EventHandler.class);
        if (eventHandler != null) {
          Class<?>[] parameterTypes = method.getParameterTypes();
          if (parameterTypes.length == 1 && ClassReflection.isAssignableFrom(Event.class, parameterTypes[0])) {
            final List<EventWrapper> list = getList(parameterTypes[0]);
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

  List<EventWrapper> getList(Class eventClass) {
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
    Class c = event.getClass();
    final List<EventWrapper> posted = new ArrayList<EventWrapper>(); //Prevents being posted multiple times to same EventHandler
    while (c != null && ClassReflection.isAssignableFrom(Event.class, c)) {
      Class eventClass = c;
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

}
