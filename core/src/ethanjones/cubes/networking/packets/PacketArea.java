package ethanjones.cubes.networking.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import ethanjones.cubes.networking.packet.Packet;
import ethanjones.cubes.networking.packet.PacketPriority;
import ethanjones.cubes.side.Side;
import ethanjones.cubes.side.Sided;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.side.server.PlayerManager;
import ethanjones.cubes.world.reference.AreaReference;
import ethanjones.cubes.world.storage.Area;

public class PacketArea extends Packet {

  public Area area;
  public PlayerManager playerManager;

  public PacketArea() {
    setPacketPriority(PacketPriority.Low);
  }

  @Override
  public void write(DataOutputStream dataOutputStream) throws Exception {
    area.write(dataOutputStream);
  }

  @Override
  public void read(DataInputStream dataInputStream) throws Exception {
    area = Area.read(dataInputStream);
  }

  @Override
  public boolean shouldSend() {
    return playerManager == null || playerManager.shouldSendArea(area.areaX, area.areaZ);
  }

  @Override
  public void handlePacket() {
    if (Sided.getSide() != Side.Client) return;
    Cubes.getClient().world.setAreaInternal(new AreaReference().setFromArea(area), area);
  }

  @Override
  public boolean shouldCompress() {
    return true;
  }
}
