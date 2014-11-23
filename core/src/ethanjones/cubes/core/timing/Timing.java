package ethanjones.cubes.core.timing;

import com.badlogic.gdx.utils.Disposable;
import java.util.ArrayList;

public class Timing implements Disposable {

  private ArrayList<TimeWrapper> handlers;
  private long lastTime;

  public Timing() {
    handlers = new ArrayList<TimeWrapper>();
    lastTime = System.currentTimeMillis();
  }

  public void addHandler(TimeHandler timeHandler, int... millisecondIntervals) {
    for (int ms : millisecondIntervals) {
      handlers.add(new TimeWrapper(timeHandler, ms));
    }
  }

  public void update() {
    long time = System.currentTimeMillis();
    int deltaMS = (int) (time - lastTime);
    for (TimeWrapper handler : handlers) {
      handler.update(deltaMS);
    }
    lastTime = time;
  }

  @Override
  public void dispose() {
    handlers.clear();
  }
}
