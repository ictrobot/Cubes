package ethanjones.modularworld.networking.common.packet;

import com.badlogic.gdx.utils.Array;
import ethanjones.modularworld.core.logging.Log;
import ethanjones.modularworld.side.Side;

public class PacketBuffer {

  private Array<Packet> packets;

  public PacketBuffer() {
    this.packets = new Array<Packet>();
  }

  public void addPacket(Packet packet, Side side) {
    packet.setSide(side);
    synchronized (packets) {
      packets.add(packet);
    }
  }

  public void process() {
    synchronized (packets) {
      int size = packets.size;
      for (int i = 0; i < size; i++) {
        packets.pop().handlePacket();
      }
    }
  }
}
