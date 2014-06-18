package ethanjones.modularworld.core.data;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ByteMap extends ByteBase {

  public Map<ByteBase, ByteBase> data = new HashMap<ByteBase, ByteBase>();

  public ByteMap(ByteMode mode) {
    super(mode);
  }

  @Override
  public void writeData(DataOutput output) throws IOException {
    for (ByteBase bb : data.keySet()) {
      ByteBase.write(bb, output);
      ByteBase.write(data.get(bb), output);
    }
    output.writeByte(new ByteEnd(new ByteMode.Normal()).getID());
    output.writeByte(new ByteEnd(new ByteMode.Normal()).getID());
  }

  @Override
  public void readData(DataInput input) throws IOException {
    ByteBase a;
    ByteBase b;
    while (!((a = ByteBase.read(input)) instanceof ByteEnd) && !((b = ByteBase.read(input)) instanceof ByteEnd)) {
      if (a != null && b != null) {
        data.put(a, b);
      }
    }
  }

  @Override
  public String toString() {
    return data.toString();
  }

  @Override
  protected ByteBase clone(ByteMode mode) {
    ByteMap bm = new ByteMap(mode);
    for (ByteBase bb : data.keySet()) {
      bm.data.put(bb.clone(), data.get(bb).clone());
    }
    return bm;
  }

  @Override
  public boolean equals(Object o) {
    if (super.equals(o)) {
      ByteMap bb = (ByteMap) o;
      return data == bb.data;
    } else {
      return false;
    }
  }

  @Override
  public byte getID() {
    return 14;
  }

  @Override
  public int hashCode() {
    return super.hashCode() ^ data.hashCode();
  }

  public boolean containsKey(ByteBase bb) {
    return data.containsKey(bb);
  }

  public boolean containsValue(ByteBase bb) {
    return data.containsValue(bb);
  }

  public ByteBase get(ByteBase bb) {
    if (!(bb.mode instanceof ByteMode.Normal)) {
      bb = bb.clone(new ByteMode.Normal());
    }
    if (!containsKey(bb)) {
      return new ByteEnd(new ByteMode.Normal());
    } else {
      return data.get(bb);
    }
  }

  public void put(ByteBase key, ByteBase value) {
    if (!(key.mode instanceof ByteMode.Normal)) {
      key = key.clone(new ByteMode.Normal());
    }
    if (!(value.mode instanceof ByteMode.Normal)) {
      value = value.clone(new ByteMode.Normal());
    }
    if (containsKey(key)) {
      data.remove(key);
    }
    data.put(key, value);
  }
}
