package ethanjones.cubes.core.gwt;

import com.badlogic.gdx.utils.SnapshotArray;
import ethanjones.cubes.core.system.Debug;
import ethanjones.cubes.side.common.Side;

public class Task {
  private static SnapshotArray<Task> tasks = new SnapshotArray<Task>();
  private static FakeAtomic.AtomicInteger counter = new FakeAtomic.AtomicInteger(0);
  private static Task currentTask = null;

  private long runStartMS;
  private int limitMS;
  private Runnable runnable;
  private String name = "Task-" + counter.getAndIncrement();
  private Side side = Side.Client;

  public Task() {
    this.limitMS = Integer.MAX_VALUE;
    this.runnable = null;
  }

  public Task(int limitMS) {
    this.limitMS = limitMS;
    this.runnable = null;
  }

  public Task(Runnable runnable) {
    this.limitMS = Integer.MAX_VALUE;
    this.runnable = runnable;
  }

  public Task(int limitMS, Runnable runnable) {
    this.limitMS = limitMS;
    this.runnable = runnable;
  }

  public void run() {
    if (this.runnable != null) this.runnable.run();
  }

  public void start() {
    if (!tasks.contains(this, true)) tasks.add(this);
  }

  public void stop() {
    tasks.removeValue(this, true);
  }

  public void checkTime() {
    if (System.currentTimeMillis() - this.runStartMS > this.limitMS) throw new TimelimitException();
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }

  public void setSide(Side side) {
    this.side = side;
  }

  public Side getSide() {
    return this.side;
  }

  public boolean isRunning() {
    return currentTask == this;
  }

  public static void runTasks() {
    tasks.begin();
    for (Task t : tasks) {
      currentTask = t;
      t.runStartMS = System.currentTimeMillis();
      try {
        try {
          t.run();
        } catch (TimelimitException ignored) {

        }
      } catch (Exception e) {
        Debug.crash(e);
      }
    }
    currentTask = null;
    tasks.end();
  }

  public static boolean taskRunning() {
    return currentTask != null;
  }

  public static String currentTaskName() {
    return currentTask == null ? "Main" : currentTask.getName();
  }

  public static Side getCurrentSide(){
    if (currentTask == null) return null;
    return currentTask.getSide();
  }

  public static class TimelimitException extends RuntimeException {

  }
}
