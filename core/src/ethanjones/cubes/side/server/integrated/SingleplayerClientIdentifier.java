package ethanjones.cubes.side.server.integrated;

import ethanjones.cubes.networking.packets.PacketConnect;
import ethanjones.cubes.networking.server.ClientIdentifier;

public class SingleplayerClientIdentifier extends ClientIdentifier {
  
  public SingleplayerClientIdentifier() {
    super(null, new PacketConnect());
  }
}
