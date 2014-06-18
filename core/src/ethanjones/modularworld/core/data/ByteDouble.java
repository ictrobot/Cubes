package ethanjones.modularworld.core.data;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ByteDouble extends ByteBase {

  public double data;

  public ByteDouble(ByteMode mode) {
    super(mode);
  }

  public ByteDouble(ByteMode mode, double d) {
    super(mode);
    this.data = d;
  }

  @Override
  public void writeData(DataOutput output) throws IOException {
    output.writeDouble(this.data);
  }

  @Override
  public void readData(DataInput input) throws IOException {
    this.data = input.readDouble();
  }

  @Override
  public String toString() {
    return "" + this.data;
  }

  @Override
  protected ByteBase clone(ByteMode mode) {
    return new ByteDouble(mode, this.data);
  }

  @Override
  public boolean equals(Object o) {
    if (super.equals(o)) {
      ByteDouble bb = (ByteDouble) o;
      return this.data == bb.data;
    } else {
      return false;
    }
  }

  @Override
  public byte getID() {
    return 6;
  }

  @Override
  public int hashCode() {
    return super.hashCode() ^ ((Double) this.data).hashCode();
  }
}
