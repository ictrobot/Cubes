package ethanjones.cubes.core.gwt;

public final class FakeAtomic {
  private FakeAtomic() {
  }

  public final static class AtomicBoolean {
    private boolean b;

    public AtomicBoolean() {
      b = false;
    }

    public AtomicBoolean(boolean b) {
      this.b = b;
    }

    public boolean get() {
      return b;
    }

    public void set(boolean b) {
      this.b = b;
    }

    public boolean compareAndSet(boolean expect, boolean update) {
      if (this.b != expect) return false;
      this.b = update;
      return true;
    }

    public boolean getAndSet(boolean b) {
      boolean prev = this.b;
      this.b = b;
      return prev;
    }
  }

  public final static class AtomicInteger {
    private int i;

    public AtomicInteger() {
      this.i = 0;
    }

    public AtomicInteger(int i) {
      this.i = i;
    }

    public int get() {
      return i;
    }

    public void set(int i) {
      this.i = i;
    }

    public boolean compareAndSet(int expect, int update) {
      if (this.i != expect) return false;
      this.i = update;
      return true;
    }

    public int incrementAndGet() {
      this.i++;
      return i;
    }

    public int getAndIncrement() {
      int i = this.i;
      this.i++;
      return i;
    }
  }

  public final static class AtomicLong {
    private long l;

    public AtomicLong() {
      this.l = 0;
    }

    public AtomicLong(long l) {
      this.l = l;
    }

    public long get() {
      return l;
    }

    public void set(long i) {
      this.l = l;
    }

    public boolean compareAndSet(long expect, long update) {
      if (this.l != expect) return false;
      this.l = update;
      return true;
    }

    public long incrementAndGet() {
      this.l++;
      return l;
    }

    public long getAndIncrement() {
      long l = this.l;
      this.l++;
      return l;
    }
  }

  public final static class AtomicReference<T> {
    private T obj;

    public AtomicReference() {
      this.obj = null;
    }

    public AtomicReference(T obj) {
      this.obj = obj;
    }

    public T get() {
      return obj;
    }

    public void set(T obj) {
      this.obj = obj;
    }

    public boolean compareAndSet(T expect, T update) {
      if (this.obj != expect) return false;
      this.obj = update;
      return true;
    }
  }
}
