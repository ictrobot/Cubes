package ethanjones.modularworld;

import ethanjones.modularworld.core.data.ByteBase;
import ethanjones.modularworld.core.data.ByteMode;
import ethanjones.modularworld.core.data.ByteString;
import ethanjones.modularworld.networking.common.packet.Packet;

public class PacketTest extends Packet {
  @Override
  public ByteBase write() {
    return new ByteString(new ByteMode.Normal(), "Hi");
  }

  @Override
  public void read(ByteBase bytebase) {

  }
}
