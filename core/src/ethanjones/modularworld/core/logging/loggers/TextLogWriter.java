package ethanjones.modularworld.core.logging.loggers;

import ethanjones.modularworld.core.logging.LogLevel;
import ethanjones.modularworld.core.logging.LogWriter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class TextLogWriter implements LogWriter {

  private final static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy HH:mm:ss");

  @Override
  public void log(LogLevel level, String message) {
    println(getString(level, message));
  }

  @Override
  public void log(LogLevel level, String message, Throwable throwable) {
    println(getString(level, message));
    println(getString(level, throwable));
  }

  @Override
  public void log(LogLevel level, Throwable throwable) {
    println(getString(level, throwable));
  }

  private static String getString(LogLevel level, String message) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(dateFormat.format(new Date()));
    stringBuilder.append(" [");
    stringBuilder.append(level.name().toUpperCase());
    stringBuilder.append("] [");
    stringBuilder.append(Thread.currentThread().getName());
    stringBuilder.append("] ");
    stringBuilder.append(message);
    return stringBuilder.toString();
  }

  private static String getString(LogLevel level, Throwable throwable) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    throwable.printStackTrace(pw);
    return sw.toString();
  }

  protected abstract void println(String string);
}
