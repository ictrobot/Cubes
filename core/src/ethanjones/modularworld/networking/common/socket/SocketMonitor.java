package ethanjones.modularworld.networking.common.socket;

import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.utils.Disposable;
import ethanjones.modularworld.networking.common.packet.Packet;
import ethanjones.modularworld.networking.common.packet.PacketHandler;

public class SocketMonitor implements Disposable {

  private final Socket socket;
  private final SocketInput socketInput;
  private final SocketOutput socketOutput;

  public SocketMonitor(Socket socket, PacketHandler packetHandler) {
    this.socket = socket;
    socketInput = new SocketInput(socket.getInputStream(), this, packetHandler);
    socketOutput = new SocketOutput(socket.getOutputStream());
    socketInput.start("Socket Input: " + socket.getRemoteAddress());
    socketOutput.start("Socket Output: " + socket.getRemoteAddress());
  }

  @Override
  public void dispose() {
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
