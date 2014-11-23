package ethanjones.cubes.core.event;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

public class EventCallable implements Callable<Event> {

  private final EventBus eventBus;
  private final Event event;

  public EventCallable(EventBus eventBus, Event event) {
    this.eventBus = eventBus;
    this.event = event;
  }
  
  @Override
  public Event call() throws Exception {
    run(eventBus, event);
    return event;
  }

  public static void run(EventBus eventBus, Event event) {
    Class<?> c = event.getClass();
    final List<EventWrapper> posted = new ArrayList<EventWrapper>(); //Prevents being posted multiple times to same EventHandler
    while (c != null && Event.class.isAssignableFrom(c)) {
      Class<? extends Event> eventClass = c.asSubclass(Event.class);
      final List<EventWrapper> list = eventBus.getList(eventClass);
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
  }
}
