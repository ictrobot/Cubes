package ethanjones.modularworld.core.data;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ByteShort extends ByteBase {

  public short data;

  public ByteShort(ByteMode mode) {
    super(mode);
  }

  public ByteShort(ByteMode mode, short s) {
    super(mode);
    this.data = s;
  }

  @Override
  public void writeData(DataOutput output) throws IOException {
    output.writeShort(this.data);
  }

  @Override
  public void readData(DataInput input) throws IOException {
    this.data = input.readShort();
  }

  @Override
  public String toString() {
    return "" + this.data;
  }

  @Override
  protected ByteBase clone(ByteMode mode) {
    return new ByteShort(mode, this.data);
  }

  @Override
  public boolean equals(Object o) {
    if (super.equals(o)) {
      ByteShort bb = (ByteShort) o;
      return this.data == bb.data;
    } else {
      return false;
    }
  }

  @Override
  public byte getID() {
    return 2;
  }

  @Override
  public int hashCode() {
    return super.hashCode() ^ this.data;
  }
}
