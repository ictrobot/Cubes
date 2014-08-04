package ethanjones.modularworld.networking.common.socket;

import ethanjones.modularworld.core.data.ByteBase;
import ethanjones.modularworld.core.logging.Log;
import ethanjones.modularworld.networking.common.packet.PacketManager;
import ethanjones.modularworld.networking.common.packet.PacketQueue;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class SocketOutput extends SocketIO {

  private final OutputStream outputStream;
  private final DataOutputStream dataOutputStream;
  private final PacketQueue packetQueue;

  public SocketOutput(OutputStream outputStream) {
    this.outputStream = outputStream;
    this.dataOutputStream = new DataOutputStream(outputStream);
    this.packetQueue = new PacketQueue();
  }

  @Override
  public void run() {
    while (running.get()) {
      try {
        if (packetQueue.isEmpty()) {
          packetQueue.waitForPacket();
        }
        ByteBase.compress(PacketManager.getPayload(packetQueue.getPacket()), dataOutputStream, false);
      } catch (Exception e) {
        if (running.get()) Log.error(e);
      }
    }
  }

  @Override
  public void dispose() {
    running.set(false);
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
