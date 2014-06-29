package ethanjones.modularworld.core.logging;


import com.badlogic.gdx.utils.Disposable;

public interface LogWriter extends Disposable {

  public void log(LogLevel level, String tag, String message);

  public void log(LogLevel level, String tag, String message, Throwable throwable);

}
