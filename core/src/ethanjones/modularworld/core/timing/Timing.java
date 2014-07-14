package ethanjones.modularworld.core.timing;

import java.util.ArrayList;

public class Timing {

  private ArrayList<TimeHandlerWrapper> handlers;
  private long lastTime;

  public Timing() {
    handlers = new ArrayList<TimeHandlerWrapper>();
    lastTime = System.currentTimeMillis();
  }

  public void addHandler(TimeHandler timeHandler, int... millisecondIntervals) {
    for (int ms : millisecondIntervals) {
      handlers.add(new TimeHandlerWrapper(timeHandler, ms));
    }
  }

  public void update() {
    long time = System.currentTimeMillis();
    int deltaMS = (int) (time - lastTime);
    for (TimeHandlerWrapper handler : handlers) {
      handler.update(deltaMS);
    }
    lastTime = time;
  }

}
