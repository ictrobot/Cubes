package ethanjones.cubes.networking.packets;

import ethanjones.cubes.input.ClickType;
import ethanjones.cubes.networking.packet.Packet;
import ethanjones.cubes.networking.packet.PacketDirection;
import ethanjones.cubes.networking.packet.PacketDirection.Direction;
import ethanjones.cubes.side.common.Cubes;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@Direction(PacketDirection.TO_SERVER)
public class PacketClick extends Packet {
  
  public ClickType type;
  
  public PacketClick() {
    this.type = ClickType.none;
  }
  
  public PacketClick(ClickType type) {
    this.type = type;
  }
  
  
  @Override
  public void write(DataOutputStream dataOutputStream) throws IOException {
    dataOutputStream.writeByte(type.ordinal());
  }
  
  @Override
  public void read(DataInputStream dataInputStream) throws IOException {
    type = ClickType.values()[dataInputStream.readByte()];
  }
  
  @Override
  public void handlePacket() {
    Cubes.getServer().getClient(getSocketMonitor()).getPlayerManager().clickType = type;
  }
}
