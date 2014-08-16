package ethanjones.modularworld.core.logging;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import ethanjones.modularworld.core.ModularWorldException;
import ethanjones.modularworld.side.common.ModularWorld;

import java.io.File;

public class Log {

  private static LogWriter output;
  private static LogWriter file;

  static {
    try {
      output = getLogWriter();
    } catch (Exception e) {

    }
    try {
      file = new FileLogWriter(new File(ModularWorld.baseFolder.file(), "log.txt"));
    } catch (Exception e) {
      try {
        file = new FileLogWriter(new File(System.getProperty("user.dir"), "log.txt"));
      } catch (Exception ex) {

      }
    }
  }

  public static synchronized void log(LogLevel level, String tag, String message) {
    try {
      output.log(level, tag, message);
    } catch (Exception e) {

    }
    try {
      file.log(level, tag, message);
    } catch (Exception e) {

    }
  }

  public static synchronized void log(LogLevel level, String tag, String message, Throwable throwable) {
    try {
      output.log(level, tag, message, throwable);
    } catch (Exception e) {

    }
    try {
      file.log(level, tag, message, throwable);
    } catch (Exception e) {

    }

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
    try {
      StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

      Class<?> c;
      try {
        c = Class.forName(stackTrace[3].getClassName());
        return c.getSimpleName();
      } catch (ClassNotFoundException e) {

      }

      return "";
    } catch (Exception e) {
      return "";
    }
  }

  public static void dispose() {
    try {
      output.dispose();
      file.dispose();
    } catch (Exception e) {

    }
  }
}
