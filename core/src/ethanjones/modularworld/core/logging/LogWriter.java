package ethanjones.modularworld.core.logging;

public interface LogWriter {

  public void log(LogLevel level, String tag, String message);

  public void log(LogLevel level, String tag, String message, Throwable throwable);

}
