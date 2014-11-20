package ethanjones.cubes.networking.socket;

import java.io.DataOutputStream;
import java.io.IOException;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.networking.packet.Packet;
import ethanjones.cubes.networking.packet.PacketIDDatabase;
import ethanjones.cubes.networking.packet.PacketQueue;
import ethanjones.cubes.side.Side;
import ethanjones.cubes.side.Sided;

public class SocketOutput extends SocketIO {

  private final DataOutputStream dataOutputStream;
  private final PacketQueue packetQueue;
  private final PacketIDDatabase packetIDDatabase;

  public SocketOutput(SocketMonitor socketMonitor) {
    super(socketMonitor);
    this.dataOutputStream = new DataOutputStream(socketMonitor.getSocket().getOutputStream());
    this.packetQueue = new PacketQueue();
    this.packetIDDatabase = socketMonitor.getNetworking().getPacketIDDatabase();
  }

  @Override
  public void run() {
    Sided.setSide(socketMonitor.getSide());
    while (socketMonitor.running.get()) {
      try {
        if (packetQueue.isEmpty()) {
          packetQueue.waitForPacket();
        }

        Packet packet = packetQueue.getPacket();
        if (packet == null) continue;

        Class<? extends Packet> packetClass = packet.getClass();
        boolean sendID = false;
        if (packetIDDatabase.contains(packetClass)) {
          sendID = true;
        }

        if (sendID) {
          dataOutputStream.writeByte(0);
          dataOutputStream.writeInt(packetIDDatabase.get(packetClass));
        } else {
          dataOutputStream.writeByte(1);
          dataOutputStream.writeUTF(packetClass.getName());
          if (socketMonitor.getSide() == Side.Server) {
            packetIDDatabase.sendID(packetClass, socketMonitor);
          }
        }

        packet.write(dataOutputStream);

      } catch (IOException e) {
        socketMonitor.getNetworking().disconnected(socketMonitor, e);
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
