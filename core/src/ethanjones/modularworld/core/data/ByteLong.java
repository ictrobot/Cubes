package ethanjones.modularworld.core.data;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ByteLong extends ByteBase {

  public long data;

  public ByteLong(ByteMode mode) {
    super(mode);
  }

  public ByteLong(ByteMode mode, long l) {
    super(mode);
    this.data = l;
  }

  @Override
  public void writeData(DataOutput output) throws IOException {
    output.writeLong(this.data);
  }

  @Override
  public void readData(DataInput input) throws IOException {
    this.data = input.readLong();
  }

  @Override
  public String toString() {
    return "" + this.data;
  }

  @Override
  protected ByteBase clone(ByteMode mode) {
    return new ByteLong(mode, this.data);
  }

  @Override
  public boolean equals(Object o) {
    if (super.equals(o)) {
      ByteLong bb = (ByteLong) o;
      return this.data == bb.data;
    } else {
      return false;
    }
  }

  @Override
  public byte getID() {
    return 4;
  }

  @Override
  public int hashCode() {
    return super.hashCode() ^ ((Long) this.data).hashCode();
  }
}
