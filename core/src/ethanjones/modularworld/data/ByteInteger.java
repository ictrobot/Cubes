package ethanjones.modularworld.data;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ByteInteger extends ByteBase {
  
  public int data;
  
  public ByteInteger(ByteMode mode) {
    super(mode);
  }
  
  public ByteInteger(ByteMode mode, int i) {
    super(mode);
    this.data = i;
  }
  
  @Override
  public void writeData(DataOutput output) throws IOException {
    output.writeInt(this.data);
  }
  
  @Override
  public void readData(DataInput input) throws IOException {
    this.data = input.readInt();
  }
  
  @Override
  public String toString() {
    return "" + this.data;
  }
  
  @Override
  protected ByteBase clone(ByteMode mode) {
    return new ByteInteger(mode, this.data);
  }
  
  @Override
  public boolean equals(Object o) {
    if (super.equals(o)) {
      ByteInteger bb = (ByteInteger) o;
      return this.data == bb.data;
    } else {
      return false;
    }
  }
  
  @Override
  public byte getID() {
    return 3;
  }
  
  @Override
  public int hashCode() {
    return super.hashCode() ^ this.data;
  }
}
