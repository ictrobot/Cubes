package ethanjones.modularworld.core.data;

import ethanjones.modularworld.core.data.basic.*;
import ethanjones.modularworld.core.data.other.DataEnd;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DataGroup extends Data {

  private static DataEnd dataEnd = new DataEnd();
  private HashMap<String, Data> map;

  public DataGroup() {
    map = new HashMap<String, Data>();
  }

  @Override
  protected void write(DataOutput output) throws IOException {
    for (Map.Entry<String, Data> entry : map.entrySet()) {
      DataTools.write(entry.getValue(), output);
      output.writeUTF(entry.getKey());
    }
    DataTools.write(dataEnd, output);
  }

  @Override
  protected void read(DataInput input) throws IOException {
    map.clear();
    Data data;
    while (!((data = DataTools.read(input)) instanceof DataEnd)) {
      map.put(input.readUTF(), data);
    }
  }

  @Override
  public byte getId() {
    return -1;
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof DataGroup) return ((DataGroup) o).map.equals(this.map);
    return false;
  }

  @Override
  public String toString() {
    return map.toString();
  }

  @Override
  public int hashCode() {
    return map.hashCode();
  }

  public int size() {
    return map.size();
  }

  public Set<Map.Entry<String, Data>> getEntrySet() {
    return map.entrySet();
  }

  //Contains
  public boolean contains(String key) {
    return map.containsKey(key);
  }

  public boolean contains(Data value) {
    return map.containsValue(value);
  }

  //Get
  public Data getValue(String key) {
    return map.get(key);
  }

  public DataList getDataList(String key) {
    Data data = getValue(key);
    if (data != null && data instanceof DataList) return (DataList) data;
    return (DataList) setValue(key, new DataList());
  }

  public DataGroup getGroup(String key) {
    Data data = getValue(key);
    if (data != null && data instanceof DataGroup) return (DataGroup) data;
    return (DataGroup) setValue(key, new DataGroup());
  }

  public Byte getByte(String key) {
    Data data = getValue(key);
    if (data != null && data instanceof DataByte) return ((DataByte) data).get();
    return (byte) 0;
  }

  public Boolean getBoolean(String key) {
    Data data = getValue(key);
    if (data != null && data instanceof DataBoolean) return ((DataBoolean) data).get();
    return false;
  }

  public Short getShort(String key) {
    Data data = getValue(key);
    if (data != null && data instanceof DataShort) return ((DataShort) data).get();
    return 0;
  }

  public Integer getInteger(String key) {
    Data data = getValue(key);
    if (data != null && data instanceof DataInteger) return ((DataInteger) data).get();
    return 0;
  }

  public Long getLong(String key) {
    Data data = getValue(key);
    if (data != null && data instanceof DataLong) return ((DataLong) data).get();
    return 0l;
  }

  public Float getFloat(String key) {
    Data data = getValue(key);
    if (data != null && data instanceof DataFloat) return ((DataFloat) data).get();
    return 0f;
  }

  public Double getDouble(String key) {
    Data data = getValue(key);
    if (data != null && data instanceof DataDouble) return ((DataDouble) data).get();
    return 0d;
  }

  public String getString(String key) {
    Data data = getValue(key);
    if (data != null && data instanceof DataString) return ((DataString) data).get();
    return "";
  }

  //Set

  /**
   * @return value
   */
  public Data setValue(String key, Data value) {
    map.put(key, value);
    return value;
  }

  public void setList(String key, DataList value) {
    setValue(key, value);
  }

  public void setGroup(String key, DataGroup value) {
    setValue(key, value);
  }

  public void setByte(String key, Byte value) {
    setValue(key, new DataByte(value));
  }

  public void setBoolean(String key, Boolean value) {
    setValue(key, new DataBoolean(value));
  }

  public void setShort(String key, Short value) {
    setValue(key, new DataShort(value));
  }

  public void setInteger(String key, Integer value) {
    setValue(key, new DataInteger(value));
  }

  public void setLong(String key, Long value) {
    setValue(key, new DataLong(value));
  }

  public void setFloat(String key, Float value) {
    setValue(key, new DataFloat(value));
  }

  public void setDouble(String key, Double value) {
    setValue(key, new DataDouble(value));
  }

  public void setString(String key, String value) {
    setValue(key, new DataString(value));
  }
}
