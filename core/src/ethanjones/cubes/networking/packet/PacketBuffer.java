package ethanjones.cubes.networking.packet;

import com.badlogic.gdx.utils.Array;

import ethanjones.cubes.core.logging.Log;

public class PacketBuffer {

  private final Array<Packet> packets;

  public PacketBuffer() {
    this.packets = new Array<Packet>();
  }

  public void addPacket(Packet packet) {
    synchronized (packets) {
      packets.add(packet);
    }
  }

  public void process() {
    synchronized (packets) {
      int size = packets.size;
      for (int i = 0; i < size; i++) {
        Packet packet = null;
        try {
          packet = packets.pop();
          packet.handlePacket();
        } catch (Exception e) {
          if (packet != null) {
            Log.error("Failed to handle packet " + packet.toString(), e);
          } else {
            Log.error("Failed to handle packet", e);
          }
        }
      }
    }
  }

}
