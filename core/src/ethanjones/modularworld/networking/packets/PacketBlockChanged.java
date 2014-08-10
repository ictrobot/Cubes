package ethanjones.modularworld.networking.packets;

import ethanjones.modularworld.core.data.core.DataGroup;
import ethanjones.modularworld.networking.common.packet.Packet;
import ethanjones.modularworld.side.client.ModularWorldClient;
import ethanjones.modularworld.world.reference.AreaReference;

public class PacketBlockChanged extends Packet {

  public int x;
  public int y;
  public int z;
  public int factory;

  @Override
  public void handlePacket() {
    ModularWorldClient.instance.world.getArea(new AreaReference().setFromBlock(x, y, z)).handleChange(this);
  }

  @Override
  public DataGroup write() {
    DataGroup dataGroup = new DataGroup();
    dataGroup.setInteger("x", x);
    dataGroup.setInteger("y", y);
    dataGroup.setInteger("z", z);
    dataGroup.setInteger("factory", factory);
    return dataGroup;
  }

  @Override
  public void read(DataGroup dataGroup) {
    x = dataGroup.getInteger("x");
    y = dataGroup.getInteger("y");
    z = dataGroup.getInteger("z");
    factory = dataGroup.getInteger("factory");
  }
}
