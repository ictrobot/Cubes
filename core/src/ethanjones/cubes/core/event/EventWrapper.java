package ethanjones.cubes.core.event;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.system.CubesException;
import ethanjones.cubes.core.system.Debug;

import java.lang.reflect.Method;

class EventWrapper {

  public final Method method;
  public final Object instance;
  private final EventHandler eventHandler;

  public EventWrapper(Method method, Object instance, EventHandler eventHandler) {
    this.method = method;
    this.instance = instance;
    this.eventHandler = eventHandler;
  }

  public boolean run(Event event) {
    try {
      method.invoke(instance, event);
      return true;
    } catch (Exception e) {
      String msg = "EventHandler " + instance.getClass().getSimpleName() + " throw an error while handling " + event.getClass().getSimpleName();
      if (eventHandler.critical()) {
        Debug.crash(new CubesException(msg + " and is critical", e));
      } else {
        Log.error(msg + " and will be removed", e);
      }
      return false;
    }
  }
}
