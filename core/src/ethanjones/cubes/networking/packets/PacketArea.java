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

  private static AreaReference areaReference = new AreaReference();
  public int areaX;
  public int areaY;
  public int areaZ;
  public int[] area;
  public PlayerManager playerManager;

  public PacketArea() {
    setPacketPriority(PacketPriority.Low);
  }

  @Override
  public void write(DataOutputStream dataOutputStream) throws Exception {
    dataOutputStream.writeInt(areaX);
    dataOutputStream.writeInt(areaY);
    dataOutputStream.writeInt(areaZ);
    dataOutputStream.writeInt(area.length);
    for (int i = 0; i < area.length; i++) {
      dataOutputStream.writeInt(area[i]);
    }
  }

  @Override
  public void read(DataInputStream dataInputStream) throws Exception {
    areaX = dataInputStream.readInt();
    areaY = dataInputStream.readInt();
    areaZ = dataInputStream.readInt();
    area = new int[dataInputStream.readInt()];
    for (int i = 0; i < area.length; i++) {
      area[i] = dataInputStream.readInt();
    }
  }

  @Override
  public void handlePacket() {
    if (Sided.getSide() != Side.Client) return;
    areaReference.setFromAreaCoordinates(areaX, areaY, areaZ);
    Area a = new Area(areaX, areaY, areaZ);
    a.fromIntArray(area);
    Cubes.getClient().world.setAreaInternal(areaReference, a);
  }

  @Override
  public boolean shouldSend() {
    if (playerManager == null) return true;
    return playerManager.shouldSendArea(areaX, areaY, areaZ);
  }
}
