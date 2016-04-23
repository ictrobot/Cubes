package ethanjones.cubes.core.logging.loggers;

import ethanjones.cubes.core.logging.LogLevel;
import ethanjones.cubes.core.logging.LogWriter;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;

public class GdxAppLogWriter implements LogWriter {

  public GdxAppLogWriter() {
    Gdx.app.setLogLevel(Application.LOG_DEBUG);
  }

  @Override
  public void log(LogLevel level, String message) {
    Gdx.app.log(Thread.currentThread().getName(), message);
  }

  @Override
  public void log(LogLevel level, String message, Throwable throwable) {
    Gdx.app.log(Thread.currentThread().getName(), message, throwable);
  }

  @Override
  public void log(LogLevel level, Throwable throwable) {
    log(level, "", throwable);
  }

  public int getLevel(LogLevel level) {
    switch (level) {
      case error:
        return Application.LOG_ERROR;
      case warning:
        return Application.LOG_ERROR;
      case info:
        return Application.LOG_INFO;
      case debug:
        return Application.LOG_DEBUG;
      default:
        return Application.LOG_DEBUG;
    }
  }

  @Override
  public void dispose() {

  }
}
