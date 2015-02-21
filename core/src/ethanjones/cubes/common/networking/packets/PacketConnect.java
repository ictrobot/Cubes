package ethanjones.cubes.common.networking.packets;

import ethanjones.data.DataGroup;

import ethanjones.cubes.common.core.settings.Settings;
import ethanjones.cubes.common.networking.packet.DataPacket;
import ethanjones.cubes.common.networking.packet.PacketPriority;
import ethanjones.cubes.common.networking.server.ClientIdentifier;
import ethanjones.cubes.Cubes;

public class PacketConnect extends DataPacket {

  public String username = Settings.getStringSettingValue(Settings.USERNAME);
  public int renderDistance = Settings.getIntegerSettingValue(Settings.GRAPHICS_VIEW_DISTANCE);

  public PacketConnect() {
    setPacketPriority(PacketPriority.High);
  }

  @Override
  public void handlePacket() {
    Cubes.getServer().addClient(new ClientIdentifier(getSocketMonitor(), this));
  }

  @Override
  public DataGroup write() {
    DataGroup dataGroup = new DataGroup();
    dataGroup.setString("username", username);
    dataGroup.setInteger("renderDistance", renderDistance);
    return dataGroup;
  }

  @Override
  public void read(DataGroup dataGroup) {
    username = dataGroup.getString("username");
    renderDistance = dataGroup.getInteger("renderDistance");
  }
}
