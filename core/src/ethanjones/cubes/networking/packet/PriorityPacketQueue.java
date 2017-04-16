package ethanjones.cubes.networking.packet;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import ethanjones.cubes.core.gwt.FakeAtomic.AtomicLong;

public class PriorityPacketQueue extends PacketQueue {
  private PriorityBlockingQueue<FIFOEntry> queue = new PriorityBlockingQueue<FIFOEntry>();

  public PriorityPacketQueue() {
    queue = new PriorityBlockingQueue<FIFOEntry>(16);
  }

  @Override
  public void add(Packet packet) {
    queue.add(new FIFOEntry(packet));
  }

  @Override
  public Packet get() {
    FIFOEntry entry = queue.poll();
    return entry == null ? null : entry.packet;
  }

  @Override
  public Packet waitAndGet() {
    try {
      FIFOEntry entry = queue.poll(5, TimeUnit.SECONDS);
      return entry == null ? null : entry.packet;
    } catch (InterruptedException e) {
      return null;
    }
  }

  private static final class FIFOEntry implements Comparable<FIFOEntry> {
    private static final AtomicLong sequence = new AtomicLong(0L);

    public final long num;
    public final Packet packet;

    private FIFOEntry(Packet packet) {
      this.num = sequence.incrementAndGet();
      this.packet = packet;
    }

    public int compareTo(FIFOEntry other) {
      int priority = PacketPriority.get(this.packet.getClass()).compareTo(PacketPriority.get(other.packet.getClass()));
      if (priority == 0 && other.packet != this.packet)
        priority = (num < other.num ? -1 : 1);
      return priority;
    }
  }
}
