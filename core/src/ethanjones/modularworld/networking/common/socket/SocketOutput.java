package ethanjones.modularworld.networking.common.socket;

import ethanjones.modularworld.core.logging.Log;
import ethanjones.modularworld.networking.common.packet.Packet;
import ethanjones.modularworld.networking.common.packet.PacketQueue;
import ethanjones.modularworld.side.Sided;

import java.io.DataOutputStream;
import java.io.IOException;

public class SocketOutput extends SocketIO {

  private final DataOutputStream dataOutputStream;
  private final PacketQueue packetQueue;

  public SocketOutput(SocketMonitor socketMonitor) {
    super(socketMonitor);
    this.dataOutputStream = new DataOutputStream(socketMonitor.getSocket().getOutputStream());
    this.packetQueue = new PacketQueue();
  }

  @Override
  public void run() {
    Sided.setSide(socketMonitor.networking.getSide());
    while (socketMonitor.running.get()) {
      try {
        if (packetQueue.isEmpty()) {
          packetQueue.waitForPacket();
        }
        //Log.info("Packets to send: " + packetQueue.size());
        Packet packet = packetQueue.getPacket();
        if (packet == null) continue;
        dataOutputStream.writeUTF(packet.getClass().getName());
        packet.write(dataOutputStream);
      } catch (IOException e) {
        socketMonitor.networking.disconnected(socketMonitor, e);
        return;
      } catch (Exception e) {
        Log.info("Failed to write packet", e);
      }
    }
  }

  @Override
  public void dispose() {
    try {
      dataOutputStream.close();
    } catch (IOException e) {

    }
    getThread().interrupt();
  }

  public PacketQueue getPacketQueue() {
    return packetQueue;
  }
}
