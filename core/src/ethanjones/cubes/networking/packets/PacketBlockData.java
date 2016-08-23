package ethanjones.cubes.networking.packets;

import ethanjones.cubes.block.data.BlockData;
import ethanjones.cubes.networking.NetworkingManager;
import ethanjones.cubes.networking.packet.DataPacket;
import ethanjones.cubes.networking.server.ClientIdentifier;
import ethanjones.cubes.side.Side;
import ethanjones.cubes.side.Sided;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.world.storage.Area;
import ethanjones.data.DataGroup;

public class PacketBlockData extends DataPacket {
  public int areaX, areaZ, blockX, blockY, blockZ;
  public DataGroup dataGroup;

  @Override
  public void handlePacket() {
    Area area = Sided.getCubes().world.getArea(areaX, areaZ);
    if (area != null) {
      BlockData blockData = area.getBlockData(blockX, blockY, blockZ);
      blockData.read(dataGroup);
    }
    if (Sided.getSide() == Side.Server) {
      ClientIdentifier client = Cubes.getServer().getClient(getSocketMonitor());
      if (client != null) {
        PacketBlockData n = new PacketBlockData();
        n.areaX = this.areaX;
        n.areaZ = this.areaZ;
        n.blockX = this.blockX;
        n.blockY = this.blockY;
        n.blockZ = this.blockZ;
        n.dataGroup = this.dataGroup;
        NetworkingManager.sendPacketToOtherClients(n, client);
      }
    }
  }

  @Override
  public DataGroup write() {
    DataGroup dataGroup = new DataGroup();
    dataGroup.put("areaX", areaX);
    dataGroup.put("areaZ", areaZ);
    dataGroup.put("blockX", blockX);
    dataGroup.put("blockY", blockY);
    dataGroup.put("blockZ", blockZ);
    dataGroup.put("data", this.dataGroup);
    return dataGroup;
  }

  @Override
  public void read(DataGroup data) {
    areaX = data.getInteger("areaX");
    areaZ = data.getInteger("areaZ");
    blockX = data.getInteger("blockX");
    blockY = data.getInteger("blockY");
    blockZ = data.getInteger("blockZ");
    dataGroup = data.getGroup("data");
  }
}
