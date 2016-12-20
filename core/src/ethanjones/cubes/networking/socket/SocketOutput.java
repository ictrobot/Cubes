package ethanjones.cubes.networking.socket;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.networking.packet.Packet;
import ethanjones.cubes.networking.packet.PacketQueue;
import ethanjones.cubes.networking.stream.DirectByteArrayOutputStream;
import ethanjones.cubes.networking.stream.NoCloseDataOutputStream;
import ethanjones.cubes.side.common.Side;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.Deflater;

import static ethanjones.cubes.networking.Networking.NETWORKING_DEBUG;

public class SocketOutput extends SocketIO {

  public static final int COMPRESSION_LEVEL = Deflater.DEFAULT_COMPRESSION;
  public static final boolean COMPRESSION_NOWRAP = false;

  private final OutputStream socketOutputStream;
  private final DataOutputStream dataOutputStream;

  private final Deflater deflater;
  private final DirectByteArrayOutputStream uncompressedOutput;
  private final DataOutputStream uncompressedDataOutput;
  private final DirectByteArrayOutputStream compressionOutput;

  private final byte[] deflateBuffer = new byte[16384];

  public SocketOutput(SocketMonitor socketMonitor) {
    super(socketMonitor);
    this.socketOutputStream = socketMonitor.getSocket().getOutputStream();
    this.dataOutputStream = new NoCloseDataOutputStream(socketOutputStream);

    this.deflater = new Deflater(COMPRESSION_LEVEL, COMPRESSION_NOWRAP);
    this.uncompressedOutput = new DirectByteArrayOutputStream();
    this.uncompressedDataOutput = new NoCloseDataOutputStream(uncompressedOutput);
    this.compressionOutput = new DirectByteArrayOutputStream();
  }

  @Override
  public void run() {
    Side.setSide(socketMonitor.getSide());
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
          uncompressedOutput.reset();
          compressionOutput.reset();
          deflater.reset();
          //Write packet
          packet.write(uncompressedDataOutput);
          //Deflate
          deflater.setInput(uncompressedOutput.buffer(), 0, uncompressedOutput.count());
          deflater.finish();
          int length;
          while ((length = deflater.deflate(deflateBuffer, 0, deflateBuffer.length)) > 0) {
            compressionOutput.write(deflateBuffer, 0, length);
          }
          //Write to outputstream
          dataOutputStream.writeInt(compressionOutput.count());
          dataOutputStream.writeInt(uncompressedOutput.count());
          dataOutputStream.write(compressionOutput.buffer(), 0, compressionOutput.count());
        } else {
          packet.write(dataOutputStream);
        }
        if (NETWORKING_DEBUG) Log.debug(socketMonitor.getSide() + " send " + packet.toString());
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
