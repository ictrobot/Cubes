package ethanjones.cubes.networking.singleplayer;

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

public class PacketCloneThread extends Thread {

  public final AtomicBoolean running = new AtomicBoolean(true);
  public final PacketQueue input;
  public final PacketQueue output;
  private final Side sideIn;
  private final Side sideOut;

  public PacketCloneThread(PacketQueue output, Side sideOut) {
    this.input = new PacketQueue();
    this.output = output;
    this.sideIn = sideOut == Side.Client ? Side.Server : Side.Client;
    this.sideOut = sideOut;
    setName("PacketCloneThread-" + sideIn.name() + "To" + sideOut.name());
  }

  @Override
  public void run() {
    while (running.get()) {
      try {
        Packet packet = input.waitAndGet();
        if (packet == null) continue;

        if (!packet.shouldSend()) continue;

        Sided.setSide(sideIn);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        packet.write(new DataOutputStream(byteArrayOutputStream));

        Sided.setSide(sideOut);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        Packet n = packet.getClass().newInstance();
        n.read(new DataInputStream(byteArrayInputStream));

        output.add(n);
      } catch (Exception e) {
        Debug.crash(e);
      }
    }
  }
}
