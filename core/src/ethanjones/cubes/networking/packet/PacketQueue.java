package ethanjones.cubes.networking.packet;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

public class PacketQueue {

  private BlockingQueue<Packet> queue;

  public PacketQueue() {
    queue = new LinkedBlockingQueue<Packet>();
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
      return queue.poll(5, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      return null;
    }
  }

}
