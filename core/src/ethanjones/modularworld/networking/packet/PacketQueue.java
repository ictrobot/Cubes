package ethanjones.modularworld.networking.packet;

import com.badlogic.gdx.utils.Array;
import ethanjones.modularworld.networking.packet.environment.PacketPriority;

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
    if (!packet.shouldSend()) return;
    Array<Packet> array = getArray(packet.getPacketEnvironment().getSending().getPacketPriority());
    synchronized (array) {
      array.add(packet);
    }
    synchronized (sync) {
      sync.notifyAll();
    }
  }

  public Packet getPacket() {
    Packet packet;

    if ((packet = getPacket(PacketPriority.High)) != null) return packet;
    if ((packet = getPacket(PacketPriority.Medium)) != null) return packet;
    if ((packet = getPacket(PacketPriority.Low)) != null) return packet;
    return null;
  }

  private Packet getPacket(PacketPriority priority) {
    Array<Packet> array = getArray(priority);
    if (array.size <= 0) return null;
    Packet packet;
    synchronized (array) {
      packet = array.removeIndex(0);
    }
    if (packet.shouldSend()) return packet;
    return getPacket(priority);
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
      case High:
        return high;
      case Medium:
        return medium;
      default:
        return low;
    }
  }
}
