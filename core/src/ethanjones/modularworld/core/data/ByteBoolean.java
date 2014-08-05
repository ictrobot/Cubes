package ethanjones.modularworld.core.data;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ByteBoolean extends ByteBase {

  public boolean data;

  public ByteBoolean(ByteMode mode) {
    super(mode);
  }

  public ByteBoolean(ByteMode mode, boolean b) {
    super(mode);
    this.data = b;
  }

  @Override
  public void writeData(DataOutput output) throws IOException {
    output.writeBoolean(this.data);
  }

  @Override
  public void readData(DataInput input) throws IOException {
    this.data = input.readBoolean();
  }

  @Override
  public String toString() {
    return "" + this.data;
  }

  @Override
  protected ByteBase clone(ByteMode mode) {
    return new ByteBoolean(mode, this.data);
  }

  @Override
  public boolean equals(Object o) {
    if (super.equals(o)) {
      ByteBoolean bb = (ByteBoolean) o;
      return this.data == bb.data;
    } else {
      return false;
    }
  }

  @Override
  public byte getID() {
    return 8;
  }

  @Override
  public int hashCode() {
    return super.hashCode() ^ ((Boolean) data).hashCode();
  }
}
