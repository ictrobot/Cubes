package ethanjones.modularworld.networking.common;

import com.badlogic.gdx.utils.Disposable;

public abstract class SocketMonitorBase implements Runnable, Disposable {

  private final String addressName;
  public boolean running;
  private Thread thread;

  public SocketMonitorBase(String addressName) {
    this.addressName = addressName;
    running = true;
  }

  public Thread start() {
    if (thread != null) return thread;
    thread = new Thread(this);
    thread.setName("Socket Monitor: " + addressName);
    thread.start();
    return thread;
  }

  protected Thread getThread() {
    return thread;
  }
}
