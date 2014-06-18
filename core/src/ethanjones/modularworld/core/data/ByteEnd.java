package ethanjones.modularworld.core.data;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ByteEnd extends ByteBase {

  public ByteEnd(ByteMode mode) {
    super(mode);
  }

  @Override
  public void writeData(DataOutput output) throws IOException {

  }

  @Override
  public void readData(DataInput input) throws IOException {

  }

  @Override
  protected ByteBase clone(ByteMode mode) {
    return new ByteEnd(mode);
  }

  @Override
  public byte getID() {
    return 15;
  }
}
