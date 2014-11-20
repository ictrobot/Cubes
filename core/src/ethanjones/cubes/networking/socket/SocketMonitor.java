package ethanjones.cubes.networking.socket;

import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.utils.Disposable;
import java.util.concurrent.atomic.AtomicBoolean;

import ethanjones.cubes.networking.Networking;
import ethanjones.cubes.networking.packet.Packet;
import ethanjones.cubes.side.Side;

public class SocketMonitor implements Disposable {

  protected final AtomicBoolean running;
  private final Networking networking;
  private final Side side;
  private final Socket socket;
  private final SocketInput socketInput;
  private final SocketOutput socketOutput;

  public SocketMonitor(Socket socket, Networking networking, Side side) {
    this.socket = socket;
    this.networking = networking;
    this.side = side;
    running = new AtomicBoolean(true);
    socketInput = new SocketInput(this);
    socketOutput = new SocketOutput(this);
    socketInput.start("Socket Input: " + socket.getRemoteAddress());
    socketOutput.start("Socket Output: " + socket.getRemoteAddress());
  }

  @Override
  public void dispose() {
    running.set(false);
    socketInput.dispose();
    socketOutput.dispose();
    socket.dispose();
  }

  public Networking getNetworking() {
    return networking;
  }

  public Side getSide() {
    return side;
  }

  public Socket getSocket() {
    return socket;
  }

  public SocketInput getSocketInput() {
    return socketInput;
  }

  public SocketOutput getSocketOutput() {
    return socketOutput;
  }
}
