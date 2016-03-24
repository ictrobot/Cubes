package ethanjones.cubes.networking.socket;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.networking.packet.Packet;
import ethanjones.cubes.side.Side;
import ethanjones.cubes.side.Sided;

import java.io.*;
import java.util.zip.Inflater;

import static ethanjones.cubes.networking.Networking.NETWORKING_DEBUG;

public class SocketInput extends SocketIO {

  private final InputStream socketInputStream;
  private final DataInputStream dataInputStream;

  private final Inflater inflater;
  private final ByteArrayOutputStream byteArrayOutputStream;
  private final byte[] compressedBuffer = new byte[1024]; //just a buffer;
  private final byte[] inflateBuffer = new byte[1024];

  public SocketInput(SocketMonitor socketMonitor) {
    super(socketMonitor);
    this.socketInputStream = socketMonitor.getSocket().getInputStream();
    this.dataInputStream = new DataInputStream(socketInputStream) {
      @Override
      public void close() throws IOException {
        //prevents being closed by packets
      }
    };

    this.inflater = new Inflater(SocketOutput.COMPRESSION_NOWRAP);
    this.byteArrayOutputStream = new ByteArrayOutputStream(16384);
  }

  @Override
  public void run() {
    Sided.setSide(socketMonitor.getSide());
    while (socketMonitor.running.get()) {
      try {
        Class<? extends Packet> packetClass;

        int b = dataInputStream.readByte();
        if (b == 0 || b == 2) {
          packetClass = socketMonitor.getPacketIDDatabase().get(dataInputStream.readInt());
        } else {
          packetClass = Class.forName(dataInputStream.readUTF()).asSubclass(Packet.class);
          if (socketMonitor.getSide() == Side.Server) {
            socketMonitor.getPacketIDDatabase().sendID(packetClass, socketMonitor);
          }
        }

        Packet packet = packetClass.newInstance();
        packet.setSocketMonitor(socketMonitor);
        if (b == 2 || b == 3) {
          //Reset
          byteArrayOutputStream.reset();
          inflater.reset();
          //Read compressed and uncompressed lengths
          int compressedLength = dataInputStream.readInt();
          int uncompressedLength = dataInputStream.readInt();
          //Read in compressed version
          int offset = 0;
          while (offset < compressedLength) {
            offset += dataInputStream.read(compressedBuffer, offset, compressedLength - offset);
          }
          //Inflate
          inflater.setInput(compressedBuffer, 0, compressedLength);
          int length;
          while ((length = inflater.inflate(inflateBuffer, 0, inflateBuffer.length)) > 0) {
            byteArrayOutputStream.write(inflateBuffer, 0, length);
          }
          byte[] uncompressed = byteArrayOutputStream.toByteArray();
          if (uncompressed.length != uncompressedLength) {
            Log.error("Uncompressed length should be " + uncompressedLength + " but is " + uncompressed.length + " [" + inflater.needsInput() + "," + inflater.needsDictionary() + "]");
            return;
          }
          //Packet read in
          packet.read(new DataInputStream(new ByteArrayInputStream(uncompressed)));
        } else {
          packet.read(dataInputStream);
        }
        if (NETWORKING_DEBUG) Log.debug(socketMonitor.getSide() + " receive " + packet.toString());
        packetQueue.add(packet);
      } catch (IOException e) {
        socketMonitor.getNetworking().disconnected(socketMonitor, e);
        return;
      } catch (Exception e) {
        Log.info("Failed to read packet", e);
      }
    }

  }

  @Override
  public void dispose() {
    try {
      socketInputStream.close();
      inflater.end();
    } catch (IOException e) {

    }
    getThread().interrupt();
  }
}
