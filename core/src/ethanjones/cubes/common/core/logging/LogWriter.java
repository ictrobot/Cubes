package ethanjones.cubes.common.core.logging;

import com.badlogic.gdx.utils.Disposable;

public interface LogWriter extends Disposable {

  public void log(LogLevel level, String message);

  public void log(LogLevel level, String message, Throwable throwable);

  public void log(LogLevel level, Throwable throwable);

}
