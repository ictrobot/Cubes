package ethanjones.cubes.networking.socket;

import ethanjones.cubes.networking.Networking;
import ethanjones.cubes.side.common.Side;

import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.utils.Disposable;

import ethanjones.cubes.core.gwt.FakeAtomic.AtomicBoolean;

public class SocketMonitor implements Disposable {
  
  public final String remoteAddress;
  protected final AtomicBoolean running;
  private final Networking networking;
  private final Side side;
  private final Socket socket;

  public SocketMonitor(Socket socket, Networking networking, Side side) {
    this.socket = socket;
    this.networking = networking;
    this.side = side;
    this.remoteAddress = socket.getRemoteAddress();
    running = new AtomicBoolean(true);
  }

  @Override
  public void dispose() {
    running.set(false);
    socket.dispose();
  }

  public Networking getNetworking() {
    return networking;
  }

  public Side getSide() {
    return side;
  }

  public Socket getSocket() {
    return null;
  }
}
