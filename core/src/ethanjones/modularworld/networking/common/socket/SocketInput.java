package ethanjones.modularworld.networking.common.socket;

import ethanjones.modularworld.core.data.Data;
import ethanjones.modularworld.core.data.DataTools;
import ethanjones.modularworld.networking.common.packet.PacketHandler;
import ethanjones.modularworld.networking.common.packet.PacketManager;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class SocketInput extends SocketIO {

  private final InputStream inputStream;
  private final DataInputStream dataInputStream;
  private final PacketHandler packetHandler;

  public SocketInput(SocketMonitor socketMonitor, InputStream inputStream, PacketHandler packetHandler) {
    super(socketMonitor);
    this.inputStream = inputStream;
    this.dataInputStream = new DataInputStream(inputStream);
    this.packetHandler = packetHandler;
  }

  @Override
  public void run() {
    while (socketMonitor.running.get()) {
      try {
        Data read = DataTools.read(dataInputStream);
        //Log.info(Thread.currentThread().getName(), read.toString());
        PacketManager.process(read, socketMonitor, packetHandler);
      } catch (Exception e) {
        socketMonitor.running.set(false);
        socketMonitor.networking.disconnected(socketMonitor, e);
        return;
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
