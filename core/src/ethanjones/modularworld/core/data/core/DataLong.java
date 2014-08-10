package ethanjones.modularworld.core.data.core;

import ethanjones.modularworld.core.data.DataBasic;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class DataLong extends DataBasic<DataLong, Long> {

  public DataLong() {
    this(0l);
  }

  public DataLong(Long l) {
    super(5, l);
  }

  @Override
  public void write(DataOutput output) throws IOException {
    output.writeLong(obj);
  }

  @Override
  public void read(DataInput input) throws IOException {
    obj = input.readLong();
  }

  @Override
  public String writeNotation() {
    return obj.toString();
  }

  @Override
  public void readNotation(String str) {
    obj = Long.valueOf(str);
  }
}
