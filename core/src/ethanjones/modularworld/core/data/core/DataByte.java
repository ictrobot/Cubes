package ethanjones.modularworld.core.data.core;

import ethanjones.modularworld.core.data.DataBasic;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class DataByte extends DataBasic<DataByte, Byte> {

  public DataByte() {
    this((byte) 0);
  }

  public DataByte(byte b) {
    super(1, b);
  }

  @Override
  public void write(DataOutput output) throws IOException {
    output.write(obj);
  }

  @Override
  public void read(DataInput input) throws IOException {
    obj = input.readByte();
  }

  @Override
  public String writeNotation() {
    return obj.toString();
  }

  @Override
  public void readNotation(String str) {
    obj = Byte.valueOf(str);
  }
}
