package ethanjones.modularworld.core.data.other;

import ethanjones.modularworld.core.data.Data;

public interface DataParser<T extends Data> {

  public T write();

  public void read(T data);
}
