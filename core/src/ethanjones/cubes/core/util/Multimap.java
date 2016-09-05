package ethanjones.cubes.core.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Multimap<K, V> {
  private final List<V> u = Collections.emptyList();
  private final HashMap<K, List<V>> map = new HashMap<K, List<V>>();

  public List<V> get(K o) {
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

  public List<V> remove(K stage) {
    List<V> l = map.remove(stage);
    return l != null ? l : u;
  }
}
