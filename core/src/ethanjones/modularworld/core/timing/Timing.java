package ethanjones.modularworld.core.timing;


import ethanjones.modularworld.core.ModularWorldException;
import ethanjones.modularworld.core.logging.Log;

import java.util.ArrayList;

public class Timing {

  private final long timeStarted;
  private TimeHandlerList[] handlers = new TimeHandlerList[1000];
  private long lastTime;

  public Timing() {
    for (int i = 0; i < 1000; i++) {
      handlers[i] = new TimeHandlerList();
    }
    timeStarted = System.currentTimeMillis();
    lastTime = System.currentTimeMillis();
  }

  public void addHandler(TimeHandler timeHandler, int... millisecondIntervals) {
    for (int ms : millisecondIntervals) {
      TimeHandlerWrapper timeHandlerWrapper = new TimeHandlerWrapper(timeHandler, ms);
      if (ms <= 0 || ms > 1000) {
        Log.error(new ModularWorldException("millisecondInterval has to between 0 and 999"));
      }
      int t = 0;
      while (t < 1000) {
        handlers[t].add(timeHandlerWrapper);
        t += ms;
      }
    }
  }

  public void update() {
    long time = System.currentTimeMillis();
    run(lastTime, time);
    lastTime = time;
  }

  public void run(long start, long end) {
    for (long i = start; i < end; i++) {
      long msl = i % 1000l;
      int ms = (int) msl;
      for (TimeHandlerWrapper th : handlers[ms]) {
        th.timeHandler.time(th.ms, ms);
      }
    }
  }

  private class TimeHandlerList extends ArrayList<TimeHandlerWrapper> {
    public TimeHandlerList() {
      super();
    }
  }

  private class TimeHandlerWrapper {
    public final TimeHandler timeHandler;
    public final int ms;


    private TimeHandlerWrapper(TimeHandler timeHandler, int ms) {
      this.timeHandler = timeHandler;
      this.ms = ms;
    }
  }

}
