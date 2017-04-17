package ethanjones.cubes.networking.packet;

import java.util.ArrayDeque;

public class PacketQueue {

  private ArrayDeque<Packet> queue;

  public PacketQueue() {
    queue = new ArrayDeque<Packet>();
  }

  public void add(Packet packet) {
    if (packet == null || !packet.shouldSend()) return;
    queue.add(packet);
  }

  public Packet get() {
    return queue.poll();
  }

}
