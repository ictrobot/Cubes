package ethanjones.cubes.core.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class OneManyMap<K, V> {
  private final List<V> u = Collections.emptyList();
  private final HashMap<K, List<V>> map = new HashMap<K, List<V>>();

  public List<V> get(Object o) {
    List<V> l = map.get(o);
    return l == null ? u : l;
  }

  public void put(K key, V value) {
    List<V> l = map.get(key);
    if (l == null) {
      l = new ArrayList<V>();
      map.put(key, l);
    }
    l.add(value);
  }
}
