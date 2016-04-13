package ethanjones.cubes.networking.packet;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

public class PacketQueue {

  private BlockingQueue<Packet> queue;

  public PacketQueue(boolean priority) {
    if (priority) {
      queue = new PriorityBlockingQueue<Packet>(16, PacketComparator.instance);
    } else {
      queue = new LinkedBlockingQueue<Packet>();
    }
  }

  public void add(Packet packet) {
    if (packet == null || !packet.shouldSend()) return;
    queue.add(packet);
  }

  public Packet get() {
    return queue.poll();
  }

  public Packet waitAndGet() {
    try {
      return queue.poll(1, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      return null;
    }
  }
}
