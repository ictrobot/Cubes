package ethanjones.cubes.common.networking.socket;

import com.badlogic.gdx.utils.Disposable;

import ethanjones.cubes.common.networking.packet.PacketQueue;

public abstract class SocketIO implements Runnable, Disposable {

  protected final SocketMonitor socketMonitor;
  protected final PacketQueue packetQueue;
  private Thread thread;

  public SocketIO(SocketMonitor socketMonitor) {
    this.socketMonitor = socketMonitor;
    this.packetQueue = new PacketQueue();
  }

  public Thread start(String name) {
    if (thread != null) return thread;
    thread = new Thread(this);
    thread.setDaemon(true);
    thread.setName(name);
    thread.start();
    return thread;
  }

  protected Thread getThread() {
    return thread;
  }

  public PacketQueue getPacketQueue() {
    return packetQueue;
  }
}
