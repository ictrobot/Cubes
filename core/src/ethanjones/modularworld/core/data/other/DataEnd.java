package ethanjones.modularworld.core.data.other;

import ethanjones.modularworld.core.data.Data;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class DataEnd extends Data {
  @Override
  protected void write(DataOutput output) throws IOException {

  }

  @Override
  protected void read(DataInput input) throws IOException {

  }

  @Override
  public byte getId() {
    return 127;
  }

  @Override
  public boolean equals(Object o) {
    return o instanceof DataEnd;
  }

  @Override
  public String toString() {
    return "END";
  }

  @Override
  public int hashCode() {
    return 0;
  }
}
