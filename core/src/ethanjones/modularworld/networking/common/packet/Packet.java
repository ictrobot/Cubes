package ethanjones.modularworld.networking.common.packet;

import ethanjones.modularworld.core.data.ByteBase;
import ethanjones.modularworld.core.data.ByteParser;

public abstract class Packet implements ByteParser<ByteBase> {

  private final PacketPriority priority;

  public Packet() {
    this(PacketPriority.MEDIUM);
  }

  public Packet(PacketPriority priority) {
    this.priority = priority;
  }

  public PacketPriority getPriority() {
    return priority;
  }
}
