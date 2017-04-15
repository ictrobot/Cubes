package ethanjones.data;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DataGroup {

  protected Map<String, Object> map = new HashMap<String, Object>();

  @Override
  public boolean equals(Object o) {
    return o instanceof DataGroup && ((DataGroup) o).map.equals(this.map);
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

  public Set<Map.Entry<String, Object>> entrySet() {
    return map.entrySet();
  }

  public void remove(String key) {
    map.remove(key);
  }

  //Contains
  public boolean containsKey(String key) {
    return map.containsKey(key);
  }

  public boolean containsValue(Object value) {
    return map.containsValue(value);
  }

  //Get
  public Object get(String key) {
    return map.get(key);
  }

  public HashMap getHashMap(String key) {
    Object obj = get(key);
    if (obj instanceof HashMap) return (HashMap) obj;
    return (HashMap) put(key, new HashMap());
  }

  public DataGroup getGroup(String key) {
    Object obj = get(key);
    if (obj instanceof DataGroup) return (DataGroup) obj;
    return (DataGroup) put(key, new DataGroup());
  }

  public ArrayList getList(String key) {
    Object obj = get(key);
    if (obj instanceof ArrayList) return (ArrayList) obj;
    return (ArrayList) put(key, new ArrayList());
  }

  public Object[] getArray(String key) {
    Object obj = get(key);
    if (obj != null && obj.getClass().isArray()) return (Object[]) obj;
    return new Object[0];
  }

  public <T> T[] getArray(String key, Class<T> tClass) {
    Object obj = get(key);
    if (obj != null && obj.getClass().isArray()) {
      Class root = obj.getClass();
      while (root.isArray()) {
        root = root.getComponentType();
      }
      if (root == tClass) return (T[]) obj;
    }
    return (T[]) Array.newInstance(tClass, 0);
  }

  public Boolean getBoolean(String key) {
    Object obj = get(key);
    if (obj instanceof Boolean) return (Boolean) obj;
    return false;
  }

  public Byte getByte(String key) {
    Object obj = get(key);
    if (obj instanceof Byte) return (Byte) obj;
    return (byte) 0;
  }

  public Short getShort(String key) {
    Object obj = get(key);
    if (obj instanceof Short) return (Short) obj;
    return 0;
  }

  public Integer getInteger(String key) {
    Object obj = get(key);
    if (obj instanceof Integer) return (Integer) obj;
    return 0;
  }

  public Float getFloat(String key) {
    Object obj = get(key);
    if (obj instanceof Float) return (Float) obj;
    return 0f;
  }

  public Long getLong(String key) {
    Object obj = get(key);
    if (obj instanceof Long) return (Long) obj;
    return 0l;
  }

  public Double getDouble(String key) {
    Object obj = get(key);
    if (obj instanceof Double) return (Double) obj;
    return 0d;
  }

  public String getString(String key) {
    Object obj = get(key);
    if (obj instanceof String) return (String) obj;
    return "";
  }

  //Put
  public Object put(String key, Object value) {
    map.put(key, value);
    return value;
  }
}