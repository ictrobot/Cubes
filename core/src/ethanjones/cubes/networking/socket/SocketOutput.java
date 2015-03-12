package ethanjones.cubes.networking.socket;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.Deflater;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.networking.packet.Packet;
import ethanjones.cubes.networking.packet.PacketQueue;
import ethanjones.cubes.side.Side;
import ethanjones.cubes.side.Sided;

public class SocketOutput extends SocketIO {

  public static final int COMPRESSION_LEVEL = Deflater.BEST_COMPRESSION;
  public static final boolean COMPRESSION_NOWRAP = false;

  private final OutputStream socketOutputStream;
  private final DataOutputStream dataOutputStream;

  private final Deflater deflater;
  private final ByteArrayOutputStream byteArrayOutputStream;
  private final DataOutputStream byteDataOutputStream;
  private final byte[] deflateBuffer = new byte[1024];

  public SocketOutput(SocketMonitor socketMonitor) {
    super(socketMonitor);
    this.socketOutputStream = socketMonitor.getSocket().getOutputStream();
    this.dataOutputStream = new DataOutputStream(socketOutputStream) {
      @Override
      public void close() throws IOException {
        //prevents being closed by packets
      }
    };

    this.deflater = new Deflater(COMPRESSION_LEVEL, COMPRESSION_NOWRAP);
    this.byteArrayOutputStream = new ByteArrayOutputStream(16384); //can't be closed
    this.byteDataOutputStream = new DataOutputStream(byteArrayOutputStream);
  }

  @Override
  public void run() {
    Sided.setSide(socketMonitor.getSide());
    while (socketMonitor.running.get()) {
      try {
        Packet packet = packetQueue.waitAndGet();
        if (packet == null) continue;

        Class<? extends Packet> packetClass = packet.getClass();
        boolean compress = packet.shouldCompress();
        if (socketMonitor.getPacketIDDatabase().contains(packetClass)) {
          if (compress) {
            dataOutputStream.writeByte(2);
          } else {
            dataOutputStream.writeByte(0);
          }
          dataOutputStream.writeInt(socketMonitor.getPacketIDDatabase().get(packetClass));
        } else {
          if (compress) {
            dataOutputStream.writeByte(3);
          } else {
            dataOutputStream.writeByte(1);
          }
          dataOutputStream.writeUTF(packetClass.getName());
          if (socketMonitor.getSide() == Side.Server) {
            socketMonitor.getPacketIDDatabase().sendID(packetClass, socketMonitor);
          }
        }

        if (compress) {
          //Reset
          byteArrayOutputStream.reset();
          deflater.reset();
          //Write packet and reset byteArrayOutputStream again
          packet.write(byteDataOutputStream);
          byte[] uncompressed = byteArrayOutputStream.toByteArray();
          byteArrayOutputStream.reset();
          //Deflate
          deflater.setInput(uncompressed);
          deflater.finish();
          int length;
          while ((length = deflater.deflate(deflateBuffer, 0, deflateBuffer.length, Deflater.SYNC_FLUSH)) > 0) {
            byteArrayOutputStream.write(deflateBuffer, 0, length);
          }
          byte[] compressed = byteArrayOutputStream.toByteArray();
          //Write to outputstream
          dataOutputStream.writeInt(compressed.length);
          dataOutputStream.writeInt(uncompressed.length);
          dataOutputStream.write(compressed);
        } else {
          packet.write(dataOutputStream);
        }

      } catch (IOException e) {
        socketMonitor.getNetworking().disconnected(socketMonitor, e);
        return;
      } catch (Exception e) {
        Log.info("Failed to write packet", e);
      }
    }
  }

  @Override
  public void dispose() {
    try {
      socketOutputStream.close();
      deflater.end();
    } catch (IOException e) {

    }
    getThread().interrupt();
  }

  public PacketQueue getPacketQueue() {
    return packetQueue;
  }
}
