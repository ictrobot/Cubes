package ethanjones.modularworld.networking.common.socket;

import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.utils.Disposable;
import ethanjones.modularworld.networking.common.Networking;
import ethanjones.modularworld.networking.common.packet.Packet;

import java.util.concurrent.atomic.AtomicBoolean;

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
    socketInput = new SocketInput(this, socket.getInputStream(), networking);
    socketOutput = new SocketOutput(this, socket.getOutputStream());
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

  public void queue(Packet packet) {
    socketOutput.getPacketQueue().addPacket(packet);
  }
}
