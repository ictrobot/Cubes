package ethanjones.cubes.networking.packets;

import ethanjones.cubes.core.settings.Settings;
import ethanjones.cubes.networking.packet.DataPacket;
import ethanjones.cubes.networking.packet.PacketPriority;
import ethanjones.cubes.networking.server.ClientIdentifier;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.data.DataGroup;

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
    dataGroup.put("username", username);
    dataGroup.put("renderDistance", renderDistance);
    return dataGroup;
  }

  @Override
  public void read(DataGroup dataGroup) {
    username = dataGroup.getString("username");
    renderDistance = dataGroup.getInteger("renderDistance");
  }

  @Override
  public String toString() {
    return super.toString() + " " + username + " " + renderDistance;
  }
}
