package ethanjones.modularworld.networking.packets;

import ethanjones.modularworld.core.data.ByteBase;
import ethanjones.modularworld.core.data.ByteMode;
import ethanjones.modularworld.core.data.ByteString;
import ethanjones.modularworld.core.settings.Settings;
import ethanjones.modularworld.networking.common.packet.Packet;

public class PacketConnect extends Packet {
  @Override
  public ByteBase write() {
    return new ByteString(new ByteMode.Normal(), Settings.username.getStringSetting().getString());
  }

  @Override
  public void read(ByteBase bytebase) {

  }
}
