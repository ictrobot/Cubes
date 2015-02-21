package ethanjones.cubes.common.core.timing;

public class TimeWrapper {

  private final TimeHandler timeHandler;
  private final int maxMS;
  private int currentMS;

  public TimeWrapper(TimeHandler timeHandler, int MS) {
    this.timeHandler = timeHandler;
    this.maxMS = MS;
    this.currentMS = MS;
  }

  public void update(int deltaMS) {
    currentMS -= deltaMS;
    if (currentMS < 0) {
      int repeats = (Math.abs(currentMS) / maxMS) + 1;
      for (int r = 0; r < repeats; r++) {
        timeHandler.time(maxMS);
      }
      currentMS = maxMS - (Math.abs(currentMS) % maxMS);
    }
  }

}
