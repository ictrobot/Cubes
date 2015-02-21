package ethanjones.cubes.common.event;

import java.lang.reflect.Method;

import ethanjones.cubes.common.logging.Log;

class EventWrapper {

  public final Method method;
  public final Object instance;

  public EventWrapper(Method method, Object instance) {
    this.method = method;
    this.instance = instance;
  }

  public boolean run(Event event) {
    try {
      method.invoke(instance, event);
      return true;
    } catch (Exception e) {
      Log.warning("EventHandler " + instance.getClass().getSimpleName() + " throw an error while handling " + event.getClass().getSimpleName() + " and will be removed", e);
      return false;
    }
  }
}
