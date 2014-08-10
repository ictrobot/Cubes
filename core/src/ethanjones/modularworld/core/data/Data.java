package ethanjones.modularworld.core.data;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public abstract class Data {

  protected abstract void write(DataOutput output) throws IOException;

  protected abstract void read(DataInput input) throws IOException;

  public abstract byte getId();

  @Override
  public abstract boolean equals(Object o);

  @Override
  public abstract String toString();

  @Override
  public abstract int hashCode();
}
