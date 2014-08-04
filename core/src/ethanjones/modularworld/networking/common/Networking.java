package ethanjones.modularworld.networking.common;

import ethanjones.modularworld.core.ModularWorldException;
import ethanjones.modularworld.core.data.ByteBase;
import ethanjones.modularworld.core.data.ByteData;
import ethanjones.modularworld.core.logging.Log;
import ethanjones.modularworld.networking.common.packet.Packet;
import ethanjones.modularworld.networking.common.packet.PacketFactory;

import java.io.IOException;

public abstract class Networking {

  public final int port;

  public Networking(int port) {
    this.port = port;
  }

  public synchronized void received(ByteBase byteBase, SocketMonitor socketMonitor) {
    if (!(byteBase instanceof ByteData) || byteBase == null) {
      Log.info(new ModularWorldException("Received data is not ByteData"));
    }
    Packet packet = PacketFactory.getPacket((ByteData) byteBase);
    if (packet == null) {
      Log.info(new ModularWorldException("Failed to read packet"));
    }
    packet.process(socketMonitor);
  }

  public synchronized void send(Packet packet, SocketMonitor socketMonitor) {
    send(packet.getPacketData(), socketMonitor);
  }

  protected synchronized void send(ByteBase byteBase, SocketMonitor socketMonitor) {
    try {
      socketMonitor.send(ByteBase.compress(byteBase));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public abstract void start();

  public abstract void stop();

}
