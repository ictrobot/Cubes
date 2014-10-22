package ethanjones.modularworld.networking.common.socket;

import ethanjones.modularworld.core.logging.Log;
import ethanjones.modularworld.networking.common.packet.Packet;
import ethanjones.modularworld.side.Sided;

import java.io.DataInputStream;
import java.io.IOException;

public class SocketInput extends SocketIO {

  private final DataInputStream dataInputStream;

  public SocketInput(SocketMonitor socketMonitor) {
    super(socketMonitor);
    this.dataInputStream = new DataInputStream(socketMonitor.getSocket().getInputStream());
  }

  @Override
  public void run() {
    Sided.setSide(socketMonitor.networking.getSide());
    while (socketMonitor.running.get()) {
      try {//TODO: Stop sending class name
        Packet packet = packet = Class.forName(dataInputStream.readUTF()).asSubclass(Packet.class).newInstance();
        packet.read(dataInputStream);
        packet.setSocketMonitor(socketMonitor);
        socketMonitor.networking.received(packet);
      } catch (IOException e) {
        socketMonitor.networking.disconnected(socketMonitor, e);
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
