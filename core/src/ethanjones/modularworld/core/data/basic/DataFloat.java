package ethanjones.modularworld.core.data.basic;

import ethanjones.modularworld.core.data.DataBasic;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class DataFloat extends DataBasic<DataFloat, Float> {

  public DataFloat() {
    this(0);
  }

  public DataFloat(float f) {
    super(6, f);
  }

  @Override
  public void write(DataOutput output) throws IOException {
    output.writeFloat(obj);
  }

  @Override
  public void read(DataInput input) throws IOException {
    obj = input.readFloat();
  }
}
