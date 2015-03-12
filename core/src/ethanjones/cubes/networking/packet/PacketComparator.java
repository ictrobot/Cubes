package ethanjones.cubes.networking.packet;

import java.util.Comparator;

public class PacketComparator implements Comparator<Packet> {

  public static final PacketComparator instance = new PacketComparator();

  private PacketComparator() {
  }
  
  @Override
  public int compare(Packet first, Packet second) {
    int firstPriority = first.getPacketPriority().ordinal();
    int secondPriority = second.getPacketPriority().ordinal();
    return firstPriority - secondPriority;
  }
}
