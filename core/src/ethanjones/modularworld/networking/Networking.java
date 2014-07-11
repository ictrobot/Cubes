package ethanjones.modularworld.networking;

import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.SocketHints;
import ethanjones.modularworld.core.ModularWorldException;
import ethanjones.modularworld.core.data.ByteBase;
import ethanjones.modularworld.core.data.ByteData;
import ethanjones.modularworld.core.logging.Log;
import ethanjones.modularworld.networking.packet.Packet;
import ethanjones.modularworld.networking.packet.PacketFactory;

import java.io.IOException;

public abstract class Networking {

  protected final static ServerSocketHints serverSocketHints;
  protected final static SocketHints socketHints;
  protected final static Net.Protocol protocol = Net.Protocol.TCP;
  protected final static int mainPort = 8080;

  static {
    serverSocketHints = new ServerSocketHints();
    serverSocketHints.acceptTimeout = 0;
    socketHints = new SocketHints();
    socketHints.keepAlive = true;
    socketHints.connectTimeout = 30000;

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

}
