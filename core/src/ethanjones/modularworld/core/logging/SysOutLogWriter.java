package ethanjones.modularworld.core.logging;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SysOutLogWriter implements LogWriter {

  private final static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy HH:mm:ss");

  @Override
  public void log(LogLevel level, String tag, String message) {
    System.out.println(getString(level, tag, message));
  }

  @Override
  public void log(LogLevel level, String tag, String message, Throwable throwable) {
    log(level, tag, message);
    throwable.printStackTrace(System.out);
  }

  protected static String getString(LogLevel level, String tag, String message) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(dateFormat.format(new Date()));
    stringBuilder.append(" [");
    stringBuilder.append(level.name().toUpperCase());
    stringBuilder.append("] [");
    stringBuilder.append(tag);
    stringBuilder.append("] ");
    stringBuilder.append(message);
    return stringBuilder.toString();
  }

  @Override
  public void dispose() {

  }
}
