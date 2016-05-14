package ethanjones.cubes.networking.packets;

import ethanjones.cubes.networking.packet.Packet;
import ethanjones.cubes.networking.packet.PacketPriority;
import ethanjones.cubes.side.Side;
import ethanjones.cubes.side.Sided;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.side.server.PlayerManager;
import ethanjones.cubes.world.storage.Area;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class PacketArea extends Packet {

  public Area area;
  public PlayerManager playerManager;

  public PacketArea() {
    setPacketPriority(PacketPriority.Low);
  }

  @Override
  public void write(DataOutputStream dataOutputStream) throws Exception {
    area.write(dataOutputStream, false);
  }

  @Override
  public void read(DataInputStream dataInputStream) throws Exception {
    area = Area.read(dataInputStream);
  }

  @Override
  public void handlePacket() {
    if (Sided.getSide() != Side.Client) return;
    Cubes.getClient().world.setAreaInternal(area);
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
    PacketArea copy = new PacketArea();
    copy.area = new Area(this.area);
    return copy;
  }

  @Override
  public String toString() {
    return super.toString() + " " + area.areaX + "," + area.areaZ;
  }
}
