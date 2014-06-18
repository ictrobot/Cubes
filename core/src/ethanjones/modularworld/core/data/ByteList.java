package ethanjones.modularworld.core.data;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ByteList extends ByteBase {

  private List<ByteBase> data = new ArrayList<ByteBase>();

  public ByteList(ByteMode mode) {
    super(mode);
  }

  @Override
  public void writeData(DataOutput output) throws IOException {
    for (ByteBase bb : data) {
      ByteBase.write(bb, output);
    }

    output.writeByte(new ByteEnd(new ByteMode.Normal()).getID());
  }

  @Override
  public void readData(DataInput input) throws IOException {
    ByteBase bb;
    while (!((bb = ByteBase.read(input)) instanceof ByteEnd)) {
      if (bb != null) {
        this.data.add(bb);
      }
    }
  }

  public ByteList add(ByteBase bytebase) {
    ByteBase bb = bytebase;
    if (!(bb.mode instanceof ByteMode.Normal)) {
      bb = bb.clone(new ByteMode.Normal());
    }
    this.data.add(bb);
    return this;
  }

  public ByteList remove(ByteBase bb) {
    this.data.remove(bb);
    return this;
  }

  public ByteList remove(int pos) {
    this.data.remove(pos);
    return this;
  }

  public ByteBase pos(int pos) {
    try {
      return this.data.get(pos);
    } catch (IndexOutOfBoundsException e) {
      return new ByteEnd(new ByteMode.Normal());
    }
  }

  public List<ByteBase> readOnly() {
    List<ByteBase> l = new ArrayList<ByteBase>();
    for (ByteBase bb : this.data) {
      l.add(bb.clone());
    }
    return l;
  }

  @Override
  public String toString() {
    return this.data.toString();
  }

  @Override
  protected ByteBase clone(ByteMode mode) {
    ByteList bl = new ByteList(mode);
    for (ByteBase bb : this.data) {
      bl.add(bb.clone());
    }
    return bl;
  }

  @Override
  public boolean equals(Object o) {
    if (super.equals(o)) {
      ByteList bb = (ByteList) o;
      return this.data == bb.data;
    } else {
      return false;
    }
  }

  @Override
  public byte getID() {
    return 10;
  }

  @Override
  public int hashCode() {
    return super.hashCode() ^ this.data.hashCode();
  }
}
