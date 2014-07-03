package ethanjones.modularworld.core.logging;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import ethanjones.modularworld.ModularWorld;
import ethanjones.modularworld.core.ModularWorldException;

import java.io.File;

public class Log {

  private static LogWriter output;
  private static LogWriter file;

  static {
    output = getLogWriter();
    try {
      file = new FileLogWriter(new File(ModularWorld.instance.baseFolder.file(), "log.txt"));
    } catch (Exception e) {
      file = new FileLogWriter(new File(System.getProperty("user.dir"), "log.txt"));
    }
  }

  public static void log(LogLevel level, String tag, String message) {
    output.log(level, tag, message);
    file.log(level, tag, message);
  }

  public static void log(LogLevel level, String tag, String message, Throwable throwable) {
    output.log(level, tag, message, throwable);
    file.log(level, tag, message, throwable);

    if (level == LogLevel.error && throwable instanceof ModularWorldException) {
      throw (ModularWorldException) throwable;
    }
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

  public static void error(Throwable throwable) {
    log(LogLevel.error, getTagClass(), "", throwable);
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

  public static void warning(Throwable throwable) {
    log(LogLevel.warning, getTagClass(), "", throwable);
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

  public static void info(Throwable throwable) {
    log(LogLevel.info, getTagClass(), "", throwable);
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

  public static void debug(Throwable throwable) {
    log(LogLevel.debug, getTagClass(), "", throwable);
  }

  //HELPER METHODS

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
      c = Class.forName(stackTrace[3].getClassName());
      return c.getSimpleName();
    } catch (ClassNotFoundException e) {

    }

    return "";
  }

  public static void dispose() {
    output.dispose();
    file.dispose();
  }
}
