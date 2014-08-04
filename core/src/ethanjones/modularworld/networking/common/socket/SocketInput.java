package ethanjones.modularworld.networking.common.socket;

import ethanjones.modularworld.core.data.ByteBase;
import ethanjones.modularworld.core.logging.Log;
import ethanjones.modularworld.networking.common.packet.PacketHandler;
import ethanjones.modularworld.networking.common.packet.PacketManager;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class SocketInput extends SocketIO {

  private final InputStream inputStream;
  private final DataInputStream dataInputStream;
  private final SocketMonitor socketMonitor;
  private final PacketHandler packetHandler;

  public SocketInput(InputStream inputStream, SocketMonitor socketMonitor, PacketHandler packetHandler) {
    this.inputStream = inputStream;
    this.dataInputStream = new DataInputStream(inputStream);
    this.socketMonitor = socketMonitor;
    this.packetHandler = packetHandler;
  }

  @Override
  public void run() {
    while (running.get()) {
      try {
        PacketManager.process(ByteBase.decompress(dataInputStream, false), socketMonitor, packetHandler);
      } catch (Exception e) {
        if (running.get()) Log.error(e);
      }
    }
  }

  @Override
  public void dispose() {
    running.set(false);
    try {
      dataInputStream.close();
    } catch (IOException e) {

    }
    getThread().interrupt();
  }
}
