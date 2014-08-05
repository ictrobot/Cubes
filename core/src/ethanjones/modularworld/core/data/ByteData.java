package ethanjones.modularworld.core.data;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ByteData extends ByteBase {

  public Map<String, ByteBase> data = new HashMap<String, ByteBase>();

  public ByteData() {
    this(new ByteMode.Normal());
  }

  public ByteData(ByteMode mode) {
    super(mode);
  }

  @Override
  public void writeData(DataOutput output) throws IOException {
    for (ByteBase bb : data.values()) {
      ByteBase.write(bb, output);
    }
    output.writeByte(new ByteEnd(new ByteMode.Normal()).getID());
  }

  @Override
  public void readData(DataInput input) throws IOException {
    ByteBase bb;
    while (!((bb = ByteBase.read(input)) instanceof ByteEnd)) {
      if (bb != null) {
        data.put(((ByteMode.Named) bb.mode).name, bb);
      }
    }
  }

  @Override
  public String toString() {
    return data.toString();
  }

  @Override
  protected ByteBase clone(ByteMode mode) {
    ByteData bb = new ByteData(mode);
    for (String str : data.keySet()) {
      bb.data.put(str, data.get(str).clone());
    }
    return bb;
  }

  @Override
  public boolean equals(Object o) {
    if (super.equals(o)) {
      ByteData bb = (ByteData) o;
      return data == bb.data;
    } else {
      return false;
    }
  }

  @Override
  public byte getID() {
    return 0;
  }

  @Override
  public int hashCode() {
    return super.hashCode() ^ data.hashCode();
  }

  public boolean contains(String tagName) {
    return data.containsKey(tagName);
  }

  public boolean contains(ByteBase bb) {
    return data.containsValue(bb);
  }

  // Get
  public ByteBase getBase(String tagName) {
    if (!contains(tagName)) {
      return new ByteEnd(new ByteMode.Named(tagName));
    } else {
      return data.get(tagName);
    }
  }

  public byte getByte(String tagName) {
    if (!contains(tagName) || !(data.get(tagName) instanceof ByteByte)) {
      return (byte) 0;
    } else {
      return ((ByteByte) data.get(tagName)).data;
    }
  }

  public ByteData getData(String tagName) {
    if (!contains(tagName) || !(data.get(tagName) instanceof ByteData)) {
      return new ByteData(new ByteMode.Named(tagName));
    } else {
      return (ByteData) data.get(tagName);
    }
  }

  public double getDouble(String tagName) {
    if (!contains(tagName) || !(data.get(tagName) instanceof ByteDouble)) {
      return 0D;
    } else {
      return ((ByteDouble) data.get(tagName)).data;
    }
  }

  public float getFloat(String tagName) {
    if (!contains(tagName) || !(data.get(tagName) instanceof ByteFloat)) {
      return 0F;
    } else {
      return ((ByteFloat) data.get(tagName)).data;
    }
  }

  public int getInteger(String tagName) {
    if (!contains(tagName) || !(data.get(tagName) instanceof ByteInteger)) {
      return 0;
    } else {
      return ((ByteInteger) data.get(tagName)).data;
    }
  }

  public ByteList getList(String tagName) {
    if (!contains(tagName) || !(data.get(tagName) instanceof ByteList)) {
      return new ByteList(new ByteMode.Named(tagName));
    } else {
      return (ByteList) data.get(tagName);
    }
  }

  public long getLong(String tagName) {
    if (!contains(tagName) || !(data.get(tagName) instanceof ByteLong)) {
      return 0L;
    } else {
      return ((ByteLong) data.get(tagName)).data;
    }
  }

  public short getShort(String tagName) {
    if (!contains(tagName) || !(data.get(tagName) instanceof ByteShort)) {
      return (short) 0;
    } else {
      return ((ByteShort) data.get(tagName)).data;
    }
  }

  public String getString(String tagName) {
    if (!contains(tagName) || !(data.get(tagName) instanceof ByteString)) {
      return "";
    } else {
      return ((ByteString) data.get(tagName)).data;
    }
  }

  public boolean getBoolean(String tagName) {
    if (!contains(tagName) || !(data.get(tagName) instanceof ByteBoolean)) {
      return false;
    } else {
      return ((ByteBoolean) data.get(tagName)).data;
    }
  }

  // Set
  public void setBase(String tagName, ByteBase bb) {
    if (!(bb.mode instanceof ByteMode.Named)) {
      bb = bb.clone(new ByteMode.Named(tagName));
    }
    if (contains(tagName)) {
      data.remove(tagName);
    }
    data.put(tagName, bb);
  }

  public void setByte(String tagName, byte b) {
    setBase(tagName, new ByteByte(new ByteMode.Named(tagName), b));
  }

  public void setData(String tagName, ByteData idata) {
    setBase(tagName, idata);
  }

  public void setDouble(String tagName, double d) {
    setBase(tagName, new ByteDouble(new ByteMode.Named(tagName), d));
  }

  public void setFloat(String tagName, float f) {
    setBase(tagName, new ByteFloat(new ByteMode.Named(tagName), f));
  }

  public void setInteger(String tagName, int i) {
    setBase(tagName, new ByteInteger(new ByteMode.Named(tagName), i));
  }

  public void setList(String tagName, ByteList ilist) {
    setBase(tagName, ilist);
  }

  public void setLong(String tagName, long l) {
    setBase(tagName, new ByteLong(new ByteMode.Named(tagName), l));
  }

  public void setShort(String tagName, short s) {
    setBase(tagName, new ByteShort(new ByteMode.Named(tagName), s));
  }

  public void setString(String tagName, String s) {
    setBase(tagName, new ByteString(new ByteMode.Named(tagName), s));
  }

  public void setBoolean(String tagName, Boolean b) {
    setBase(tagName, new ByteBoolean(new ByteMode.Named(tagName), b));
  }
}
