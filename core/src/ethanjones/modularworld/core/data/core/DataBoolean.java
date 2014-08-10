package ethanjones.modularworld.core.data.core;

import ethanjones.modularworld.core.data.DataBasic;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class DataBoolean extends DataBasic<DataBoolean, Boolean> {

  public DataBoolean() {
    this(false);
  }

  public DataBoolean(boolean b) {
    super(2, b);
  }

  @Override
  public void write(DataOutput output) throws IOException {
    output.writeBoolean(obj);
  }

  @Override
  public void read(DataInput input) throws IOException {
    obj = input.readBoolean();
  }

  @Override
  public String writeNotation() {
    return obj.toString();
  }

  @Override
  public void readNotation(String str) {
    obj = Boolean.valueOf(str);
  }
}
