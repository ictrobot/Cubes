package ethanjones.modularworld.networking.common.packet;

import ethanjones.modularworld.core.ModularWorldException;
import ethanjones.modularworld.core.data.ByteBase;
import ethanjones.modularworld.core.data.ByteData;
import ethanjones.modularworld.core.logging.Log;
import ethanjones.modularworld.networking.common.socket.SocketMonitor;

public final class PacketManager {

  public static void process(ByteBase byteBase, SocketMonitor socketMonitor, PacketHandler packetHandler) {
    if (!(byteBase instanceof ByteData) || byteBase == null) {
      return;
    }
    Log.info(byteBase.toString());
    Packet packet = PacketManager.getPacket((ByteData) byteBase);
    if (packet == null) {
      Log.info(new ModularWorldException("Failed to read packet"));
      return;
    }
    packet.setSocketMonitor(socketMonitor);
    packetHandler.received(packet);
  }

  public static Packet getPacket(ByteData byteData) {
    Packet packet;
    try {
      packet = (Packet) Class.forName(byteData.getString("class")).newInstance();
    } catch (Exception e) {
      Log.error("Received unknown packet: " + byteData.getString("class"), e);
      return null;
    }
    packet.read(byteData.getBase("data"));
    return packet;
  }

  public static ByteData getPayload(Packet packet) {
    ByteData byteData = new ByteData();
    byteData.setString("class", packet.getClass().getName());
    byteData.setBase("data", packet.write());
    return byteData;
  }

}
