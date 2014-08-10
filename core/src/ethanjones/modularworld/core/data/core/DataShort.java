package ethanjones.modularworld.core.data.core;

import ethanjones.modularworld.core.data.DataBasic;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class DataShort extends DataBasic<DataShort, Short> {

  public DataShort() {
    this((short) 0);
  }

  public DataShort(Short s) {
    super(3, s);
  }

  @Override
  public void write(DataOutput output) throws IOException {
    output.writeShort(obj);
  }

  @Override
  public void read(DataInput input) throws IOException {
    obj = input.readShort();
  }

  @Override
  public String writeNotation() {
    return obj.toString();
  }

  @Override
  public void readNotation(String str) {
    obj = Short.valueOf(str);
  }
}
