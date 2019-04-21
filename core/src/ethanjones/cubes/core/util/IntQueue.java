package ethanjones.cubes.core.util;

public class IntQueue {
  private final int[] array;
  private final int maxElements;
  private final int arraySize;
  private int front = 0;
  private int end = 0;

  public IntQueue(int maxElements) {
    this.maxElements = maxElements;
    this.arraySize = maxElements+1;
    this.array = new int[arraySize];
  }

  public int peek() {
    return array[front];
  }

  public int poll() {
    int value = array[front];
    if (++front == arraySize) front = 0;
    return value;
  }

  public void enqueue(int v) {
    array[end] = v;
    if (++end == arraySize) end = 0;
    if (end == front) throw new IllegalStateException();
  }

  public int size() {
    if (front > end) {
      return (end + arraySize - front);
    } else {
      return end - front;
    }
  }

  public boolean isEmpty() {
    return front == end;
  }

  public void clear() {
    front = end = 0;
  }
}