package ethanjones.cubes.server.integrated;

import ethanjones.cubes.common.networking.packets.PacketConnect;
import ethanjones.cubes.common.networking.server.ClientIdentifier;

public class SingleplayerClientIdentifier extends ClientIdentifier {
  
  public SingleplayerClientIdentifier() {
    super(null, new PacketConnect());
  }
}
