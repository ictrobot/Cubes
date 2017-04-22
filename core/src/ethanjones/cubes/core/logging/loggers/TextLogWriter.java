package ethanjones.cubes.core.logging.loggers;

import ethanjones.cubes.core.gwt.Task;
import ethanjones.cubes.core.logging.LogLevel;
import ethanjones.cubes.core.logging.LogWriter;
import ethanjones.cubes.core.platform.Compatibility;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;

public abstract class TextLogWriter implements LogWriter {
  
  private final ByteArrayOutputStream baos = new ByteArrayOutputStream();
  
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
  
  protected String getString(LogLevel level, Throwable throwable) {
    baos.reset();
    PrintStream ps = new PrintStream(baos);
    throwable.printStackTrace(ps);
    return new String(baos.toByteArray(), Charset.forName("UTF-8"));
  }
  
  protected abstract void println(String string);
  
  private synchronized String getString(LogLevel level, String message) {
    String string = Compatibility.get().timestamp() + " [" + level.name().toUpperCase() + "] [" + Task.currentTaskName() + "] " + message;
    return string;
  }
}
