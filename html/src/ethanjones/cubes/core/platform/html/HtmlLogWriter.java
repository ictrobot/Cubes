package ethanjones.cubes.core.platform.html;

import ethanjones.cubes.core.gwt.Task;
import ethanjones.cubes.core.logging.LogLevel;
import ethanjones.cubes.core.logging.loggers.TextLogWriter;

import com.badlogic.gdx.Gdx;

public class HtmlLogWriter extends TextLogWriter {
  
  @Override
  public void log(LogLevel level, String message) {
    super.log(level, message);
    switch (level) {
      case error:
        Gdx.app.error(Task.currentTaskName(), message);
        break;
      case warning:
        Gdx.app.log(Task.currentTaskName(), message);
        break;
      case info:
        Gdx.app.log(Task.currentTaskName(), message);
        break;
      case debug:
        Gdx.app.debug(Task.currentTaskName(), message);
        break;
    }
  }
  
  @Override
  public void log(LogLevel level, String message, Throwable throwable) {
    super.log(level, message, throwable);
    switch (level) {
      case error:
        Gdx.app.error(Task.currentTaskName(), message, throwable);
        break;
      case warning:
        Gdx.app.log(Task.currentTaskName(), message, throwable);
        break;
      case info:
        Gdx.app.log(Task.currentTaskName(), message, throwable);
        break;
      case debug:
        Gdx.app.debug(Task.currentTaskName(), message, throwable);
        break;
    }
  }
  
  @Override
  public void log(LogLevel level, Throwable throwable) {
    super.log(level, throwable);
    switch (level) {
      case error:
        Gdx.app.error(Task.currentTaskName(), "", throwable);
        break;
      case warning:
        Gdx.app.log(Task.currentTaskName(), "", throwable);
        break;
      case info:
        Gdx.app.log(Task.currentTaskName(), "", throwable);
        break;
      case debug:
        Gdx.app.debug(Task.currentTaskName(), "", throwable);
        break;
    }
  }
  
  protected String getString(LogLevel level, Throwable throwable) {
    StringBuilder str = new StringBuilder();
    while (throwable != null) {
      if (throwable instanceof com.google.gwt.event.shared.UmbrellaException) {
        for (Throwable t : ((com.google.gwt.event.shared.UmbrellaException) throwable).getCauses()) {
          if (str.length() > 0) str.append("\nCaused by: ");
          str.append(t.toString());
          str.append("\n  at ").append(getString(level, t));
        }
      } else if (throwable instanceof com.google.web.bindery.event.shared.UmbrellaException) {
        for (Throwable t : ((com.google.web.bindery.event.shared.UmbrellaException) throwable).getCauses()) {
          if (str.length() > 0) str.append("\nCaused by: ");
          str.append(t.toString());
          str.append("\n  at ").append(getString(level, t));
        }
      } else {
        if (str.length() > 0) str.append("\nCaused by: ");
        str.append(throwable.toString());
        for (StackTraceElement stackTraceElement : throwable.getStackTrace()) {
          str.append("\n  at ").append(stackTraceElement);
        }
      }
      throwable = throwable.getCause();
    }
    return str.toString();
  }
  
  @Override
  protected void println(String string) {
    console(string);
  }
  
  public static native void console(String text)
/*-{
    $wnd.console.log(text);
}-*/;
  
  @Override
  public void dispose() {
    
  }
}
