package ethanjones.cubes.networking.singleplayer;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.system.Debug;
import ethanjones.cubes.networking.packet.Packet;
import ethanjones.cubes.networking.packet.PacketQueue;
import ethanjones.cubes.side.Side;
import ethanjones.cubes.side.Sided;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.concurrent.atomic.AtomicBoolean;

import static ethanjones.cubes.networking.Networking.NETWORKING_DEBUG;

public class SingleplayerNetworkingThread extends Thread {

  public final AtomicBoolean running = new AtomicBoolean(true);
  public final PacketQueue input;
  public final PacketQueue output;
  private final Side sideIn;
  private final Side sideOut;

  public SingleplayerNetworkingThread(PacketQueue output, Side sideOut) {
    this.input = new PacketQueue();
    this.output = output;
    this.sideIn = sideOut == Side.Client ? Side.Server : Side.Client;
    this.sideOut = sideOut;
    setName("SingleplayerNetworkingThread-" + sideIn.name() + "To" + sideOut.name());
  }

  @Override
  public void run() {
    SingleplayerOutputStream outputStream = new SingleplayerOutputStream();
    SingleplayerInputStream inputStream = new SingleplayerInputStream(outputStream);
    DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
    DataInputStream dataInputStream = new DataInputStream(inputStream);

    while (running.get()) {
      try {
        Packet packet = input.waitAndGet();
        if (packet == null) continue;
        if (!packet.shouldSend()) continue;

        Sided.setSide(sideIn);
        outputStream.reset();
        packet.write(dataOutputStream);
        if (NETWORKING_DEBUG) Log.debug(sideIn + " send " + packet.toString());

        Sided.setSide(sideOut);
        inputStream.update();
        Packet n = packet.getClass().newInstance();
        n.read(dataInputStream);
        if (NETWORKING_DEBUG) Log.debug(sideOut + " recieve " + packet.toString());

        output.add(n);
      } catch (Exception e) {
        Debug.crash(e);
      }
    }
  }

  public static class SingleplayerOutputStream extends ByteArrayOutputStream {

    public SingleplayerOutputStream() {
      super(16777216); //16 mb
    }

    public byte[] buffer() {
      return buf;
    }

    public int count() {
      return count;
    }
  }

  public static class SingleplayerInputStream extends ByteArrayInputStream {
    private final SingleplayerOutputStream os;

    public SingleplayerInputStream(SingleplayerOutputStream os) {
      super(os.buffer(), 0, os.count());
      this.os = os;
    }

    public void update() {
      pos = 0;
      mark = 0;
      buf = os.buffer();
      count = os.count();
    }
  }
}
