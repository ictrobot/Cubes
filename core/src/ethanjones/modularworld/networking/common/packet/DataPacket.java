package ethanjones.modularworld.networking.common.packet;

import ethanjones.data.DataGroup;
import ethanjones.data.DataTools;
import ethanjones.data.other.DataParser;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public abstract class DataPacket extends Packet implements DataParser<DataGroup> {

  public DataPacket() {
    super();
  }

  public DataPacket(PacketPriority priority) {
    super(priority);
  }

  @Override
  public void read(DataInputStream dataInputStream) throws Exception {
    read((DataGroup) DataTools.read(dataInputStream));
  }

  @Override
  public void write(DataOutputStream dataOutputStream) throws Exception {
    DataTools.write(write(), dataOutputStream);
  }

  @Override
  public abstract DataGroup write();

  @Override
  public abstract void read(DataGroup data);

  @Override
  public abstract void handlePacket();
}
