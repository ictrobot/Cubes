package ethanjones.modularworld.core.logging;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;

public class GdxAppLogWriter implements LogWriter {
  @Override
  public void log(LogLevel level, String tag, String message) {
    Gdx.app.setLogLevel(getLevel(level));
    Gdx.app.log(tag, message);
  }

  @Override
  public void log(LogLevel level, String tag, String message, Throwable throwable) {
    Gdx.app.setLogLevel(getLevel(level));
    Gdx.app.log(tag, message, throwable);
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
        return Application.LOG_NONE;
    }
  }
}
