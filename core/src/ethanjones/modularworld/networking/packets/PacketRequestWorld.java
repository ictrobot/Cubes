package ethanjones.modularworld.networking.packets;

import ethanjones.modularworld.core.data.ByteBase;
import ethanjones.modularworld.core.data.ByteData;
import ethanjones.modularworld.networking.common.packet.Packet;

public class PacketRequestWorld extends Packet {

  public int areaX;
  public int areaY;
  public int areaZ;

  @Override
  public ByteBase write() {
    ByteData byteData = new ByteData();
    byteData.setInteger("areaX", areaX);
    byteData.setInteger("areaY", areaY);
    byteData.setInteger("areaZ", areaZ);
    return byteData;
  }

  @Override
  public void read(ByteBase bytebase) {
    ByteData byteData = (ByteData) bytebase;
    areaX = byteData.getInteger("areaX");
    areaY = byteData.getInteger("areaY");
    areaZ = byteData.getInteger("areaZ");
  }
}
