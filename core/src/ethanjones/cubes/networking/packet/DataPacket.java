package ethanjones.cubes.networking.packet;

import ethanjones.data.Data;
import ethanjones.data.DataGroup;
import ethanjones.data.DataParser;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class DataPacket extends Packet implements DataParser {

  @Override
  public void write(DataOutputStream dataOutputStream) throws IOException {
    Data.output(write(), dataOutputStream);
  }

  @Override
  public void read(DataInputStream dataInputStream) throws IOException {
    read((DataGroup) Data.input(dataInputStream));
  }

  @Override
  public abstract void handlePacket();

  @Override
  public abstract DataGroup write();

  @Override
  public abstract void read(DataGroup data);
}
