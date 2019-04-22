package ethanjones.cubes.networking.singleplayer;

import com.badlogic.gdx.utils.reflect.ClassReflection;
import ethanjones.cubes.core.gwt.FakeAtomic.AtomicBoolean;
import ethanjones.cubes.core.gwt.Task;
import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.system.Debug;
import ethanjones.cubes.networking.packet.Packet;
import ethanjones.cubes.networking.packet.PacketQueue;
import ethanjones.cubes.networking.packet.PriorityPacketQueue;
import ethanjones.cubes.networking.stream.PairedStreams;
import ethanjones.cubes.side.common.Side;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import static ethanjones.cubes.networking.Networking.NETWORKING_DEBUG;

public class SingleplayerNetworkingTask extends Task {

  public final AtomicBoolean running = new AtomicBoolean(true);
  public final PacketQueue input;
  public final PacketQueue output;
  private final Side sideIn;
  private final Side sideOut;

  PairedStreams pair;
  DataOutputStream dataOutputStream;
  DataInputStream dataInputStream;

  public SingleplayerNetworkingTask(PacketQueue output, Side sideOut) {
    super(1);
    this.input = new PriorityPacketQueue();
    this.output = output;
    this.sideIn = sideOut == Side.Client ? Side.Server : Side.Client;
    this.sideOut = sideOut;
    setName("SingleplayerNetworkingTask-" + sideIn.name() + "To" + sideOut.name());

    pair = new PairedStreams();
    dataOutputStream = new DataOutputStream(pair.output);
    dataInputStream = new DataInputStream(pair.input);
  }

  @Override
  public void run() {
    if (!running.get()) stop();

    while (running.get()) {
      checkTime();

      try {
        Packet packet = input.get();
        if (packet == null) throw new TimelimitException();
        if (!packet.shouldSend()) continue;

        setSide(sideOut);
        Packet copy = packet.copy();
        if (copy == packet) throw new IllegalStateException(packet.getClass().getName());

        if (copy == null) {
          setSide(sideIn);
          pair.reset();
          packet.write(dataOutputStream);

          setSide(sideOut);
          pair.updateInput();
          copy = ClassReflection.newInstance(packet.getClass());
          copy.read(dataInputStream);
        }

        if (NETWORKING_DEBUG) Log.debug(packet.toString());

        output.add(copy);
      } catch (Exception e) {
        if (e instanceof TimelimitException) throw (TimelimitException) e;
        Debug.crash(e);
      }
    }
  }

}
