package ethanjones.modularworld.networking.packets;

import ethanjones.modularworld.core.data.DataGroup;
import ethanjones.modularworld.core.settings.Settings;
import ethanjones.modularworld.networking.common.packet.DataPacket;
import ethanjones.modularworld.networking.common.packet.PacketPriority;
import ethanjones.modularworld.side.server.PlayerManager;

public class PacketConnect extends DataPacket {

  public int renderDistance;
  public String username;

  public PacketConnect() {
    super(PacketPriority.HIGH);
  }

  @Override
  public DataGroup write() {
    DataGroup dataGroup = new DataGroup();
    dataGroup.setString("username", Settings.username.getStringSetting().getString());
    dataGroup.setInteger("renderDistance", Settings.renderer_block_viewDistance.getIntegerSetting().getValue());
    return dataGroup;
  }

  @Override
  public void read(DataGroup dataGroup) {
    username = dataGroup.getString("username");
    renderDistance = dataGroup.getInteger("renderDistance");
  }

  @Override
  public void handlePacket() {
    new PlayerManager(this);
  }
}
