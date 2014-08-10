package ethanjones.modularworld.core.data.core;

import ethanjones.modularworld.core.data.DataBasic;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class DataDouble extends DataBasic<DataDouble, Double> {

  public DataDouble() {
    this(0);
  }

  public DataDouble(double d) {
    super(7, d);
  }

  @Override
  public void write(DataOutput output) throws IOException {
    output.writeDouble(obj);
  }

  @Override
  public void read(DataInput input) throws IOException {
    obj = input.readDouble();
  }

  @Override
  public String writeNotation() {
    return obj.toString();
  }

  @Override
  public void readNotation(String str) {
    obj = Double.valueOf(str);
  }
}
