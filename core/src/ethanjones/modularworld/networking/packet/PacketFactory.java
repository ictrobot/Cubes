package ethanjones.modularworld.networking.packet;

import ethanjones.modularworld.core.ModularWorldException;
import ethanjones.modularworld.core.data.ByteBase;
import ethanjones.modularworld.core.data.ByteData;
import ethanjones.modularworld.core.data.ByteMode;
import ethanjones.modularworld.core.logging.Log;

import java.util.HashMap;

public class PacketFactory {

  private static HashMap<String, PacketProvider> providers = new HashMap<String, PacketProvider>();

  public static final Packet getPacket(ByteData byteData) {
    if (!(byteData.mode instanceof ByteMode.Named)) {
      Log.info(new ModularWorldException("Invalid packet mode"));
      return null;
    }
    ByteBase data = byteData.getBase("data");
    return providers.get(((ByteMode.Named) byteData.mode).name).getPacket(data);
  }

  public static interface PacketProvider {
    public Packet getPacket(ByteBase byteBase);
  }

}
