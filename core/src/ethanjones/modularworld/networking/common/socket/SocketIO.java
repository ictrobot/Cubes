package ethanjones.modularworld.networking.common.socket;

import com.badlogic.gdx.utils.Disposable;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class SocketIO implements Runnable, Disposable {

  private Thread thread;
  public AtomicBoolean running;

  public SocketIO() {
    running = new AtomicBoolean(true);
  }

  public Thread start(String name) {
    if (thread != null) return thread;
    thread = new Thread(this);
    thread.setName(name);
    thread.start();
    return thread;
  }

  protected Thread getThread() {
    return thread;
  }

}
