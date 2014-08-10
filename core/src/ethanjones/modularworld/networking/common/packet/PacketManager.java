package ethanjones.modularworld.networking.common.packet;

import ethanjones.modularworld.core.ModularWorldException;
import ethanjones.modularworld.core.data.Data;
import ethanjones.modularworld.core.data.core.DataGroup;
import ethanjones.modularworld.core.logging.Log;
import ethanjones.modularworld.networking.common.socket.SocketMonitor;

public final class PacketManager {

  public static void process(Data data, SocketMonitor socketMonitor, PacketHandler packetHandler) {
    if (!(data instanceof DataGroup) || data == null) {
      return;
    }
    Packet packet = PacketManager.getPacket((DataGroup) data);
    if (packet == null) {
      Log.info(new ModularWorldException("Failed to read packet"));
      return;
    }
    packet.setSocketMonitor(socketMonitor);
    packetHandler.received(packet);
  }

  public static Packet getPacket(DataGroup dataGroup) {
    Packet packet;
    try {
      packet = (Packet) Class.forName(dataGroup.getString("class")).newInstance();
    } catch (Exception e) {
      Log.error("Received unknown packet: " + dataGroup.getString("class"), e);
      return null;
    }
    packet.read(dataGroup.getGroup("data"));
    return packet;
  }

  public static DataGroup getPayload(Packet packet) {
    DataGroup dataGroup = new DataGroup();
    dataGroup.setString("class", packet.getClass().getName());
    dataGroup.setValue("data", packet.write());
    return dataGroup;
  }

}
