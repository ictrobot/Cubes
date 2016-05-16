package ethanjones.cubes.networking.singleplayer;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.system.Debug;
import ethanjones.cubes.networking.packet.Packet;
import ethanjones.cubes.networking.packet.PacketQueue;
import ethanjones.cubes.networking.packet.PriorityPacketQueue;
import ethanjones.cubes.networking.stream.PairedStreams;
import ethanjones.cubes.side.Side;
import ethanjones.cubes.side.Sided;

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
    this.input = new PriorityPacketQueue();
    this.output = output;
    this.sideIn = sideOut == Side.Client ? Side.Server : Side.Client;
    this.sideOut = sideOut;
    setName("SingleplayerNetworkingThread-" + sideIn.name() + "To" + sideOut.name());
    this.setDaemon(true);
    this.setPriority(Thread.NORM_PRIORITY - 1);
  }

  @Override
  public void run() {
    PairedStreams pair = new PairedStreams();
    DataOutputStream dataOutputStream = new DataOutputStream(pair.output);
    DataInputStream dataInputStream = new DataInputStream(pair.input);

    while (running.get()) {
      try {
        Packet packet = input.waitAndGet();
        if (packet == null) continue;
        if (!packet.shouldSend()) continue;

        Sided.setSide(sideOut);
        Packet copy = packet.copy();
        if (copy == packet) throw new IllegalStateException(packet.getClass().getName());

        if (copy == null) {
          Sided.setSide(sideIn);
          pair.reset();
          packet.write(dataOutputStream);

          Sided.setSide(sideOut);
          pair.updateInput();
          copy = packet.getClass().newInstance();
          copy.read(dataInputStream);
        }

        if (NETWORKING_DEBUG) Log.debug(packet.toString());

        output.add(copy);
      } catch (Exception e) {
        Debug.crash(e);
      }
    }
  }

}
