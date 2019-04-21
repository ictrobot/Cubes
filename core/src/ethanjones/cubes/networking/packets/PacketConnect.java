package ethanjones.cubes.networking.packets;

import ethanjones.cubes.core.settings.Settings;
import ethanjones.cubes.core.gwt.UUID;
import ethanjones.cubes.networking.packet.DataPacket;
import ethanjones.cubes.networking.packet.PacketDirection;
import ethanjones.cubes.networking.packet.PacketDirection.Direction;
import ethanjones.cubes.networking.packet.PacketPriority;
import ethanjones.cubes.networking.packet.PacketPriority.Priority;
import ethanjones.cubes.networking.server.ClientIdentifier;
import ethanjones.cubes.side.client.CubesClient;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.data.DataGroup;

@Direction(PacketDirection.TO_SERVER)
@Priority(PacketPriority.CONNECTION_INITIALIZATION)
public class PacketConnect extends DataPacket {

  public UUID uuid = CubesClient.uuid;
  public String username = Settings.getStringSettingValue(Settings.USERNAME);
  public int renderDistance = Settings.getIntegerSettingValue(Settings.GRAPHICS_VIEW_DISTANCE);

  @Override
  public void handlePacket() {
    Cubes.getServer().addClient(new ClientIdentifier(getSocketMonitor(), this));
  }

  @Override
  public DataGroup write() {
    DataGroup dataGroup = new DataGroup();
    dataGroup.put("uuid", uuid);
    dataGroup.put("username", username);
    dataGroup.put("renderDistance", renderDistance);
    return dataGroup;
  }

  @Override
  public void read(DataGroup dataGroup) {
    uuid = (UUID) dataGroup.get("uuid");
    username = dataGroup.getString("username");
    renderDistance = dataGroup.getInteger("renderDistance");
  }

  @Override
  public String toString() {
    return super.toString() + " " + username + " " + renderDistance;
  }
}
