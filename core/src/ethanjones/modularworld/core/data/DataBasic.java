package ethanjones.modularworld.core.data;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * @param <T> type, e.g. "DataString"
 * @param <O> object, e.g. "java.lang.String"
 */
public abstract class DataBasic<T extends DataBasic, O> extends Data<O> {

  private final byte id;
  protected O obj;

  /**
   * @param id should be a byte
   */
  public DataBasic(int id, O obj) {
    this.id = (byte) id;
    set(obj);
  }

  public abstract void write(DataOutput output) throws IOException;

  public abstract void read(DataInput input) throws IOException;

  public O get() {
    return obj;
  }

  public void set(O obj) {
    this.obj = obj;
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof Data) {
      DataBasic data = (DataBasic) o;
      if (this.getId() == data.getId() && this.obj.equals(data.obj)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public String toString() {
    return obj.toString();
  }

  @Override
  public int hashCode() {
    return obj.hashCode() ^ id;
  }

  public byte getId() {
    return id;
  }
}