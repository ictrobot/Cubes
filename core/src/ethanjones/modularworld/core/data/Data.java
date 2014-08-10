package ethanjones.modularworld.core.data;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public abstract class Data {

  public abstract void write(DataOutput output) throws IOException;

  public abstract void read(DataInput input) throws IOException;

  public abstract String writeNotation();

  public abstract void readNotation(String str);

  public abstract byte getId();

  @Override
  public abstract boolean equals(Object o);

  @Override
  public abstract String toString();

  @Override
  public abstract int hashCode();
}
