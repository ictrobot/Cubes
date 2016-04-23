package ethanjones.cubes.core.platform.android;

import ethanjones.cubes.core.logging.LogLevel;
import ethanjones.cubes.core.logging.LogWriter;

import android.util.Log;

public class AndroidLogWriter implements LogWriter {

  @Override
  public void log(LogLevel level, String message) {
    switch (level) {
      case error:
        Log.e(Thread.currentThread().getName(), message);
        break;
      case warning:
        Log.w(Thread.currentThread().getName(), message);
        break;
      case info:
        Log.i(Thread.currentThread().getName(), message);
        break;
      case debug:
        Log.d(Thread.currentThread().getName(), message);
        break;
    }
  }

  @Override
  public void log(LogLevel level, String message, Throwable throwable) {
    switch (level) {
      case error:
        Log.e(Thread.currentThread().getName(), message, throwable);
        break;
      case warning:
        Log.w(Thread.currentThread().getName(), message, throwable);
        break;
      case info:
        Log.i(Thread.currentThread().getName(), message, throwable);
        break;
      case debug:
        Log.d(Thread.currentThread().getName(), message, throwable);
        break;
    }
  }

  @Override
  public void log(LogLevel level, Throwable throwable) {
    switch (level) {
      case error:
        Log.e(Thread.currentThread().getName(), "", throwable);
        break;
      case warning:
        Log.w(Thread.currentThread().getName(), "", throwable);
        break;
      case info:
        Log.i(Thread.currentThread().getName(), "", throwable);
        break;
      case debug:
        Log.d(Thread.currentThread().getName(), "", throwable);
        break;
    }
  }

  @Override
  public void dispose() {

  }
}
