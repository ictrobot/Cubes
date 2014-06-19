package ethanjones.modularworld.core.logging;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;

public class Log {

  private static LogWriter logWriter;

  static {
    logWriter = getLogWriter();
  }

  public static void log(LogLevel level, String tag, String message) {
    logWriter.log(level, tag, message);
  }

  public static void log(LogLevel level, String tag, String message, Throwable throwable) {
    logWriter.log(level, tag, message, throwable);
  }

  //ERROR
  public static void error(String tag, String message) {
    log(LogLevel.error, tag, message);
  }

  public static void error(String tag, String message, Throwable throwable) {
    log(LogLevel.error, tag, message, throwable);
  }

  public static void error(String message) {
    log(LogLevel.error, getTagClass(), message);
  }

  public static void error(String message, Throwable throwable) {
    log(LogLevel.error, getTagClass(), message, throwable);
  }

  //WARNING
  public static void warning(String tag, String message) {
    log(LogLevel.warning, tag, message);
  }

  public static void warning(String tag, String message, Throwable throwable) {
    log(LogLevel.warning, tag, message, throwable);
  }

  public static void warning(String message) {
    log(LogLevel.warning, getTagClass(), message);
  }

  public static void warning(String message, Throwable throwable) {
    log(LogLevel.warning, getTagClass(), message, throwable);
  }

  //INFO
  public static void info(String tag, String message) {
    log(LogLevel.info, tag, message);
  }

  public static void info(String tag, String message, Throwable throwable) {
    log(LogLevel.info, tag, message, throwable);
  }

  public static void info(String message) {
    log(LogLevel.info, getTagClass(), message);
  }

  public static void info(String message, Throwable throwable) {
    log(LogLevel.info, getTagClass(), message, throwable);
  }

  //DEBUG
  public static void debug(String tag, String message) {
    log(LogLevel.debug, tag, message);
  }

  public static void debug(String tag, String message, Throwable throwable) {
    log(LogLevel.debug, tag, message, throwable);
  }

  public static void debug(String message) {
    log(LogLevel.debug, getTagClass(), message);
  }

  public static void debug(String message, Throwable throwable) {
    log(LogLevel.debug, getTagClass(), message, throwable);
  }


  private static LogWriter getLogWriter() {
    if (Application.ApplicationType.Android == Gdx.app.getType()) {
      return new GdxAppLogWriter();
    } else {
      return new SysOutLogWriter();
    }
  }

  private static String getTagClass() {
    StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

    Class<?> c;
    try {
      c = Class.forName(stackTrace[2].getClassName());
      return c.getSimpleName();
    } catch (ClassNotFoundException e) {

    }

    return "";
  }
}
