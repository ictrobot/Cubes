package ethanjones.modularworld.data;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ByteByte extends ByteBase {

  public byte data;

  public ByteByte(ByteMode mode) {
    super(mode);
  }

  public ByteByte(ByteMode mode, byte b) {
    super(mode);
    this.data = b;
  }

  @Override
  public void writeData(DataOutput output) throws IOException {
    output.writeByte(this.data);
  }

  @Override
  public void readData(DataInput input) throws IOException {
    this.data = input.readByte();
  }

  @Override
  public String toString() {
    return "" + this.data;
  }

  @Override
  protected ByteBase clone(ByteMode mode) {
    return new ByteByte(mode, this.data);
  }

  @Override
  public boolean equals(Object o) {
    if (super.equals(o)) {
      ByteByte bb = (ByteByte) o;
      return this.data == bb.data;
    } else {
      return false;
    }
  }

  @Override
  public byte getID() {
    return 1;
  }

  @Override
  public int hashCode() {
    return super.hashCode() ^ this.data;
  }
}
