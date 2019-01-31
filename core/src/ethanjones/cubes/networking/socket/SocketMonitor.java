package ethanjones.cubes.networking.socket;

import ethanjones.cubes.networking.Networking;
import ethanjones.cubes.networking.packet.PacketIDDatabase;
import ethanjones.cubes.side.common.Side;

import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.utils.Disposable;

import java.util.concurrent.atomic.AtomicBoolean;

public class SocketMonitor implements Disposable {
  
  public final String remoteAddress;
  protected final AtomicBoolean running;
  private final Networking networking;
  private final Side side;
  private final Socket socket;
  private final SocketInput socketInput;
  private final SocketOutput socketOutput;
  private final PacketIDDatabase packetIDDatabase;

  public SocketMonitor(Socket socket, Networking networking, Side side) {
    this.socket = socket;
    this.networking = networking;
    this.side = side;
    this.remoteAddress = socket.getRemoteAddress();
    this.packetIDDatabase = new PacketIDDatabase();
    running = new AtomicBoolean(true);
    socketInput = new SocketInput(this);
    socketOutput = new SocketOutput(this);
    socketInput.start("Socket Input: " + remoteAddress);
    socketOutput.start("Socket Output: " + remoteAddress);
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

  public PacketIDDatabase getPacketIDDatabase() {
    return packetIDDatabase;
  }
}
