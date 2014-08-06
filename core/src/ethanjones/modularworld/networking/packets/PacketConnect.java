package ethanjones.modularworld.networking.packets;

import ethanjones.modularworld.core.data.Data;
import ethanjones.modularworld.core.data.basic.DataString;
import ethanjones.modularworld.core.settings.Settings;
import ethanjones.modularworld.networking.common.packet.Packet;

public class PacketConnect extends Packet {
  @Override
  public Data write() {
    return new DataString(Settings.username.getStringSetting().getString());
  }

  @Override
  public void read(Data data) {

  }

  @Override
  public void handlePacket() {

  }
}
