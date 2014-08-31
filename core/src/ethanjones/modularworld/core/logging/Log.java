package ethanjones.modularworld.core.logging;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import ethanjones.modularworld.core.logging.loggers.FileLogWriter;
import ethanjones.modularworld.core.logging.loggers.GdxAppLogWriter;
import ethanjones.modularworld.core.logging.loggers.SysOutLogWriter;
import ethanjones.modularworld.side.common.ModularWorld;

import java.io.File;

public class Log {

  private static LogWriter output;
  private static LogWriter file;

  static {
    try {
      if (Application.ApplicationType.Android == Gdx.app.getType()) {
        output = new GdxAppLogWriter();
      } else {
        output = new SysOutLogWriter();
      }
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

  public static void log(LogLevel level, String message) {
    try {
      synchronized (output) {
        output.log(level, message);
      }
    } catch (Exception e) {

    }
    try {
      synchronized (file) {
        file.log(level, message);
      }
    } catch (Exception e) {

    }
  }

  public static void log(LogLevel level, String message, Throwable throwable) {
    try {
      synchronized (output) {
        output.log(level, message, throwable);
      }
    } catch (Exception e) {

    }
    try {
      synchronized (file) {
        file.log(level, message, throwable);
      }
    } catch (Exception e) {

    }
  }

  public static void log(LogLevel level, Throwable throwable) {
    try {
      synchronized (output) {
        output.log(level, throwable);
      }
    } catch (Exception e) {

    }
    try {
      synchronized (file) {
        file.log(level, throwable);
      }
    } catch (Exception e) {

    }
  }

  //ERROR
  public static void error(String message) {
    log(LogLevel.error, message);
  }

  public static void error(String message, Throwable throwable) {
    log(LogLevel.error, message, throwable);
  }

  public static void error(Throwable throwable) {
    log(LogLevel.error, throwable);
  }

  //WARNING
  public static void warning(String message) {
    log(LogLevel.warning, message);
  }

  public static void warning(String message, Throwable throwable) {
    log(LogLevel.warning, message, throwable);
  }

  public static void warning(Throwable throwable) {
    log(LogLevel.warning, throwable);
  }

  //INFO
  public static void info(String message) {
    log(LogLevel.info, message);
  }

  public static void info(String message, Throwable throwable) {
    log(LogLevel.info, message, throwable);
  }

  public static void info(Throwable throwable) {
    log(LogLevel.info, "", throwable);
  }

  //DEBUG
  public static void debug(String message) {
    log(LogLevel.debug, message);
  }

  public static void debug(String message, Throwable throwable) {
    log(LogLevel.debug, message, throwable);
  }

  public static void debug(Throwable throwable) {
    log(LogLevel.debug, throwable);
  }

  public static void dispose() {
    try {
      output.dispose();
    } catch (Exception e) {

    }
    try {
      file.dispose();
    } catch (Exception e) {

    }
  }
}
