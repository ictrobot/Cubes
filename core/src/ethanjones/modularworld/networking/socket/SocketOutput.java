package ethanjones.modularworld.networking.socket;

import ethanjones.modularworld.core.logging.Log;
import ethanjones.modularworld.networking.packet.Packet;
import ethanjones.modularworld.networking.packet.PacketIDDatabase;
import ethanjones.modularworld.networking.packet.PacketQueue;
import ethanjones.modularworld.networking.packet.environment.PacketSendingType;
import ethanjones.modularworld.side.Side;
import ethanjones.modularworld.side.Sided;

import java.io.DataOutputStream;
import java.io.IOException;

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
    Sided.setSide(socketMonitor.networking.getSide());
    while (socketMonitor.running.get()) {
      try {
        if (packetQueue.isEmpty()) {
          packetQueue.waitForPacket();
        }

        Packet packet = packetQueue.getPacket();
        if (packet == null) continue;

        PacketSendingType sendType = packet.getPacketEnvironment().getSending().getSendingType();
        Class<? extends Packet> packetClass = packet.getClass();
        if (sendType == null) {
          if (packetIDDatabase.contains(packetClass)) {
            sendType = PacketSendingType.ID;
          } else {
            sendType = PacketSendingType.NAME;
          }
        }

        if (sendType == PacketSendingType.ID) {
          dataOutputStream.writeByte(0);
          dataOutputStream.writeInt(packetIDDatabase.get(packetClass));
        } else if (sendType == PacketSendingType.NAME) {
          dataOutputStream.writeByte(1);
          dataOutputStream.writeUTF(packetClass.getName());
          if (socketMonitor.getNetworking().getSide() == Side.Server) {
            packetIDDatabase.sendID(packetClass, socketMonitor);
          }
        }

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
