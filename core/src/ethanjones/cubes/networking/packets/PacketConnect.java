package ethanjones.cubes.networking.packets;

import ethanjones.data.DataGroup;

import ethanjones.cubes.core.settings.Settings;
import ethanjones.cubes.networking.packet.DataPacket;
import ethanjones.cubes.networking.packet.environment.PacketPriority;
import ethanjones.cubes.side.server.PlayerManager;

public class PacketConnect extends DataPacket {

  public int renderDistance;
  public String username;

  public PacketConnect() {
    getPacketEnvironment().getSending().setPacketPriority(PacketPriority.High);
  }

  @Override
  public void handlePacket() {
    new PlayerManager(this);
  }

  @Override
  public DataGroup write() {
    DataGroup dataGroup = new DataGroup();
    dataGroup.setString("username", Settings.getStringSettingValue(Settings.USERNAME));
    dataGroup.setInteger("renderDistance", Settings.getIntegerSettingValue(Settings.GRAPHICS_VIEW_DISTANCE));
    return dataGroup;
  }

  @Override
  public void read(DataGroup dataGroup) {
    username = dataGroup.getString("username");
    renderDistance = dataGroup.getInteger("renderDistance");
  }
}
