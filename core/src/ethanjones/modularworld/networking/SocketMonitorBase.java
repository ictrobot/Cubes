package ethanjones.modularworld.networking;

import com.badlogic.gdx.utils.Disposable;

public abstract class SocketMonitorBase implements Runnable, Disposable {

  private final String addressName;
  public boolean running;
  private Thread thread;

  public SocketMonitorBase(String addressName) {
    this.addressName = addressName;
    running = true;
  }

  protected Thread start() {
    thread = new Thread(this);
    thread.setName("Socket Monitor: " + addressName);
    thread.start();
    return thread;
  }

  protected Thread getThread() {
    return thread;
  }
}
