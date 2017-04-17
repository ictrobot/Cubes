package ethanjones.cubes.networking.packet;

import ethanjones.cubes.core.gwt.FakeAtomic.AtomicLong;

import java.util.PriorityQueue;

public class PriorityPacketQueue extends PacketQueue {
  private PriorityQueue<FIFOEntry> queue = new PriorityQueue<FIFOEntry>();

  public PriorityPacketQueue() {
    queue = new PriorityQueue<FIFOEntry>(16);
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
