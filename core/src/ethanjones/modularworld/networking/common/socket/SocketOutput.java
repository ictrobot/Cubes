package ethanjones.modularworld.networking.common.socket;

import ethanjones.modularworld.core.data.DataGroup;
import ethanjones.modularworld.core.data.DataTools;
import ethanjones.modularworld.core.logging.Log;
import ethanjones.modularworld.networking.common.packet.PacketManager;
import ethanjones.modularworld.networking.common.packet.PacketQueue;

import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;

public class SocketOutput extends SocketIO {

  private final OutputStream outputStream;
  private final DataOutputStream dataOutputStream;
  private final PacketQueue packetQueue;

  public SocketOutput(SocketMonitor socketMonitor, OutputStream outputStream) {
    super(socketMonitor);
    this.outputStream = outputStream;
    this.dataOutputStream = new DataOutputStream(outputStream);
    this.packetQueue = new PacketQueue();
  }

  @Override
  public void run() {
    while (socketMonitor.running.get()) {
      try {
        if (packetQueue.isEmpty()) {
          packetQueue.waitForPacket();
        }
        DataGroup payload = PacketManager.getPayload(packetQueue.getPacket());
        //Log.info(Thread.currentThread().getName(), payload.toString());
        DataTools.write(payload, dataOutputStream);
      } catch (Exception e) {
        if (e instanceof EOFException) return;
        if (socketMonitor.running.get()) Log.error(e);
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
