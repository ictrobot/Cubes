package ethanjones.modularworld.data;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ByteFloat extends ByteBase {
  
  public float data;
  
  public ByteFloat(ByteMode mode) {
    super(mode);
  }
  
  public ByteFloat(ByteMode mode, float f) {
    super(mode);
    this.data = f;
  }
  
  @Override
  public void writeData(DataOutput output) throws IOException {
    output.writeFloat(this.data);
  }
  
  @Override
  public void readData(DataInput input) throws IOException {
    this.data = input.readFloat();
  }
  
  @Override
  public String toString() {
    return "" + this.data;
  }
  
  @Override
  protected ByteBase clone(ByteMode mode) {
    return new ByteFloat(mode, this.data);
  }
  
  @Override
  public boolean equals(Object o) {
    if (super.equals(o)) {
      ByteFloat bb = (ByteFloat) o;
      return this.data == bb.data;
    } else {
      return false;
    }
  }
  
  @Override
  public byte getID() {
    return 5;
  }
  
  @Override
  public int hashCode() {
    return super.hashCode() ^ ((Float) this.data).hashCode();
  }
}
