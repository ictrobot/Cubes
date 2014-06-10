package ethanjones.modularworld.data;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ByteString extends ByteBase {
  
  public String data;
  
  public ByteString(ByteMode mode) {
    super(mode);
  }
  
  public ByteString(ByteMode mode, String s) {
    super(mode);
    this.data = s;
  }
  
  @Override
  public void writeData(DataOutput output) throws IOException {
    output.writeUTF(this.data);
  }
  
  @Override
  public void readData(DataInput input) throws IOException {
    this.data = input.readUTF();
  }
  
  @Override
  public String toString() {
    return this.data;
  }
  
  @Override
  protected ByteBase clone(ByteMode mode) {
    return new ByteString(mode, this.data);
  }
  
  @Override
  public boolean equals(Object o) {
    if (super.equals(o)) {
      ByteString bb = (ByteString) o;
      return this.data == bb.data;
    } else {
      return false;
    }
  }
  
  @Override
  public byte getID() {
    return 7;
  }
  
  @Override
  public int hashCode() {
    return super.hashCode() ^ this.data.hashCode();
  }
}
