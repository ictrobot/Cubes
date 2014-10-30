package ethanjones.cubes.networking.socket;

import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.utils.Disposable;
import java.util.concurrent.atomic.AtomicBoolean;

import ethanjones.cubes.networking.Networking;
import ethanjones.cubes.networking.packet.Packet;

public class SocketMonitor implements Disposable {

  protected final AtomicBoolean running;
  protected final String remoteAddress;
  protected final Networking networking;
  private final Socket socket;
  private final SocketInput socketInput;
  private final SocketOutput socketOutput;

  public SocketMonitor(Socket socket, Networking networking) {
    this.socket = socket;
    this.networking = networking;
    remoteAddress = socket.getRemoteAddress();
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

  public Socket getSocket() {
    return socket;
  }

  public SocketInput getSocketInput() {
    return socketInput;
  }

  public SocketOutput getSocketOutput() {
    return socketOutput;
  }

  public String getRemoteAddress() {
    return remoteAddress;
  }

  public Networking getNetworking() {
    return networking;
  }

  public void queue(Packet packet) {
    socketOutput.getPacketQueue().addPacket(packet);
  }
}
