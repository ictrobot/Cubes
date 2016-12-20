package ethanjones.cubes.core.util;

import java.util.ArrayDeque;

public class PerSecond {
  private long second;
  private int counter;
  private ArrayDeque<Integer> previous;
  private int store;
  
  public PerSecond(int store) {
    this.second = System.currentTimeMillis();
    this.counter = 0;
    this.previous = new ArrayDeque<Integer>(store);
    this.store = store;
  }
  
  public synchronized void tick() {
    while ((second + 1000) < System.currentTimeMillis()) {
      addPrevious();
      second += 1000;
    }
    counter++;
  }
  
  public synchronized float average() {
    int size = previous.size();
    if (size == 0) return 0f;
    int total = 0;
    for (Integer i : previous) {
      total += i;
    }
    return ((float) total) / ((float) size);
  }
  
  public synchronized int last() {
    Object o = previous.peekLast();
    return o == null ? 0 : (int) o;
  }
  
  private void addPrevious() {
    previous.addLast(counter);
    counter = 0;
    while (previous.size() > store) {
      previous.removeFirst();
    }
  }
}
