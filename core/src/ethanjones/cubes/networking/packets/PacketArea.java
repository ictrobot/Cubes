package ethanjones.cubes.networking.packets;

import ethanjones.cubes.networking.packet.Packet;
import ethanjones.cubes.networking.packet.PacketDirection;
import ethanjones.cubes.networking.packet.PacketDirection.Direction;
import ethanjones.cubes.networking.packet.PacketPriority;
import ethanjones.cubes.networking.packet.PacketPriority.Priority;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.side.server.PlayerManager;
import ethanjones.cubes.world.storage.Area;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@Direction(PacketDirection.TO_CLIENT)
@Priority(PacketPriority.LOW)
public class PacketArea extends Packet {

  public Area area;
  public PlayerManager playerManager;

  @Override
  public void write(DataOutputStream dataOutputStream) throws IOException {
    area.writeNetworking(dataOutputStream);
  }

  @Override
  public void read(DataInputStream dataInputStream) throws IOException {
    area = Area.readArea(dataInputStream);
  }

  @Override
  public void handlePacket() {
    Cubes.getClient().world.setArea(area);
  }

  @Override
  public boolean shouldSend() {
    return !area.isUnloaded() && (playerManager == null || playerManager.shouldSendArea(area.areaX, area.areaZ));
  }

  @Override
  public boolean shouldCompress() {
    return true;
  }

  @Override
  public Packet copy() {
    PacketArea p = new PacketArea();
    if (this.area.shared) {
      p.area = this.area;
    } else {
      p.area = new Area(this.area);
    }
    return p;
  }

  @Override
  public String toString() {
    return super.toString() + " " + area.areaX + "," + area.areaZ;
  }
}
