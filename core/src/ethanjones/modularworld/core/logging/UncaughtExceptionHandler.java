package ethanjones.modularworld.core.logging;

public class UncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
  @Override
  public void uncaughtException(Thread t, Throwable e) {
    Log.info("CRASH", t.getName(), e);
  }
}
