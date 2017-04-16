package ethanjones.cubes.core.platform.android;

import ethanjones.cubes.core.gwt.Task;
import ethanjones.cubes.core.logging.LogLevel;
import ethanjones.cubes.core.logging.LogWriter;

import android.util.Log;

public class AndroidLogWriter implements LogWriter {

  @Override
  public void log(LogLevel level, String message) {
    switch (level) {
      case error:
        Log.e(Task.currentTaskName(), message);
        break;
      case warning:
        Log.w(Task.currentTaskName(), message);
        break;
      case info:
        Log.i(Task.currentTaskName(), message);
        break;
      case debug:
        Log.d(Task.currentTaskName(), message);
        break;
    }
  }

  @Override
  public void log(LogLevel level, String message, Throwable throwable) {
    switch (level) {
      case error:
        Log.e(Task.currentTaskName(), message, throwable);
        break;
      case warning:
        Log.w(Task.currentTaskName(), message, throwable);
        break;
      case info:
        Log.i(Task.currentTaskName(), message, throwable);
        break;
      case debug:
        Log.d(Task.currentTaskName(), message, throwable);
        break;
    }
  }

  @Override
  public void log(LogLevel level, Throwable throwable) {
    switch (level) {
      case error:
        Log.e(Task.currentTaskName(), "", throwable);
        break;
      case warning:
        Log.w(Task.currentTaskName(), "", throwable);
        break;
      case info:
        Log.i(Task.currentTaskName(), "", throwable);
        break;
      case debug:
        Log.d(Task.currentTaskName(), "", throwable);
        break;
    }
  }

  @Override
  public void dispose() {

  }
}
