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
  
  @Override
  protected void println(String string) {
    console(string);
  }
  
  public static native void console(String text)
/*-{
    console.log(text);
}-*/;
  
  @Override
  public void dispose() {
    
  }
}
