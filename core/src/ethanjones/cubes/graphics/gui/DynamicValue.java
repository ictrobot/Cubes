package ethanjones.cubes.graphics.gui;

import com.badlogic.gdx.Gdx;

public abstract class DynamicValue {

  public abstract int get();

  private static DynamicValue zero = new DynamicValue() {
    @Override
    public int get() {
      return 0;
    }
  };

  private static DynamicValue gdxWidth = new DynamicValue() {
    @Override
    public int get() {
      return Gdx.graphics.getWidth();
    }
  };

  private static DynamicValue gdxHeight = new DynamicValue() {
    @Override
    public int get() {
      return Gdx.graphics.getHeight();
    }
  };

  public static DynamicValue zero() {
    return zero;
  }

  public static DynamicValue gdxWidth() {
    return gdxWidth;
  }

  public static DynamicValue gdxHeight() {
    return gdxHeight;
  }

  public static DynamicValue value(final int i) {
    return new DynamicValue() {
      @Override
      public int get() {
        return i;
      }
    };
  }

  public static DynamicValue add(final DynamicValue value1, final DynamicValue value2) {
    return new DynamicValue() {
      @Override
      public int get() {
        return value1.get() + value2.get();
      }
    };
  }

  public static DynamicValue minus(final DynamicValue value1, final DynamicValue value2) {
    return new DynamicValue() {
      @Override
      public int get() {
        return value1.get() - value2.get();
      }
    };
  }

  public static DynamicValue divide(final DynamicValue value1, final DynamicValue value2) {
    return new DynamicValue() {
      @Override
      public int get() {
        return value1.get() / value2.get();
      }
    };
  }

  public static DynamicValue multiply(final DynamicValue value1, final DynamicValue value2) {
    return new DynamicValue() {
      @Override
      public int get() {
        return value1.get() * value2.get();
      }
    };
  }
}
