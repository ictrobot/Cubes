package ethanjones.modularworld.core.data.core;

import ethanjones.modularworld.core.data.Data;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class DataEnd extends Data {
  @Override
  public void write(DataOutput output) throws IOException {

  }

  @Override
  public void read(DataInput input) throws IOException {

  }

  @Override
  public String writeNotation() {
    return "";
  }

  @Override
  public void readNotation(String str) {

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
