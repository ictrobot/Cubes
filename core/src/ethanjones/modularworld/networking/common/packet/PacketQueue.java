package ethanjones.modularworld.networking.common.packet;

import com.badlogic.gdx.utils.Array;

public class PacketQueue {

  private Object sync;
  private Array<Packet> high;
  private Array<Packet> medium;
  private Array<Packet> low;

  public PacketQueue() {
    sync = new Object();
    high = new Array<Packet>();
    medium = new Array<Packet>();
    low = new Array<Packet>();
  }

  public void addPacket(Packet packet) {
    Array<Packet> array = getArray(packet.getPriority());
    synchronized (array) {
      array.add(packet);
    }
    synchronized (sync) {
      sync.notifyAll();
    }
  }

  public Packet getPacket() {
    Packet packet;
    if ((packet = getPacket(PacketPriority.HIGH)) != null) return packet;
    if ((packet = getPacket(PacketPriority.MEDIUM)) != null) return packet;
    if ((packet = getPacket(PacketPriority.LOW)) != null) return packet;
    return null;
  }

  private Packet getPacket(PacketPriority priority) {
    Array<Packet> array = getArray(priority);
    if (array.size <= 0) return null;
    synchronized (array) {
      return array.removeIndex(0);
    }
  }

  public void waitForPacket() {
    try {
      synchronized (sync) {
        sync.wait();
      }
    } catch (Exception e) {
      waitForPacket();
    }
  }

  public int size() {
    synchronized (high) {
      synchronized (medium) {
        synchronized (low) {
          return high.size + medium.size + low.size;
        }
      }
    }
  }

  public boolean isEmpty() {
    return size() == 0;
  }

  private Array<Packet> getArray(PacketPriority priority) {
    switch (priority) {
      case HIGH:
        return high;
      case MEDIUM:
        return medium;
      default:
        return low;
    }
  }
}
