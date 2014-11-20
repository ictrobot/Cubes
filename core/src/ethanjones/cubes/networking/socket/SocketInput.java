package ethanjones.cubes.networking.socket;

import java.io.DataInputStream;
import java.io.IOException;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.networking.packet.Packet;
import ethanjones.cubes.networking.packet.PacketIDDatabase;
import ethanjones.cubes.side.Side;
import ethanjones.cubes.side.Sided;

public class SocketInput extends SocketIO {

  private final DataInputStream dataInputStream;
  private final PacketIDDatabase packetIDDatabase;

  public SocketInput(SocketMonitor socketMonitor) {
    super(socketMonitor);
    this.dataInputStream = new DataInputStream(socketMonitor.getSocket().getInputStream());
    this.packetIDDatabase = socketMonitor.getNetworking().getPacketIDDatabase();
  }

  @Override
  public void run() {
    Sided.setSide(socketMonitor.getSide());
    while (socketMonitor.running.get()) {
      try {
        Class<? extends Packet> packetClass;

        int b = dataInputStream.readByte();
        if (b == 0) {
          packetClass = packetIDDatabase.get(dataInputStream.readInt());
        } else {
          packetClass = Class.forName(dataInputStream.readUTF()).asSubclass(Packet.class);
          if (socketMonitor.getSide() == Side.Server) {
            packetIDDatabase.sendID(packetClass, socketMonitor);
          }
        }

        Packet packet = packetClass.newInstance();
        packet.setSocketMonitor(socketMonitor);
        packet.read(dataInputStream);
        socketMonitor.getNetworking().received(socketMonitor, packet);
      } catch (IOException e) {
        socketMonitor.getNetworking().disconnected(socketMonitor, e);
        return;
      } catch (Exception e) {
        Log.info("Failed to read packet", e);
      }
    }
  }

  @Override
  public void dispose() {
    try {
      dataInputStream.close();
    } catch (IOException e) {

    }
    getThread().interrupt();
  }
}
