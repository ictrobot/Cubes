package ethanjones.modularworld.networking.common.socket;

import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.utils.Disposable;
import ethanjones.modularworld.networking.common.packet.Packet;
import ethanjones.modularworld.networking.common.packet.PacketHandler;

import java.util.concurrent.atomic.AtomicBoolean;

public class SocketMonitor implements Disposable {

  protected final AtomicBoolean running;
  private final Socket socket;
  private final SocketInput socketInput;
  private final SocketOutput socketOutput;

  public SocketMonitor(Socket socket, PacketHandler packetHandler) {
    this.socket = socket;
    running = new AtomicBoolean(true);
    socketInput = new SocketInput(this, socket.getInputStream(), packetHandler);
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

  public void queue(Packet packet) {
    socketOutput.getPacketQueue().addPacket(packet);
  }
}
