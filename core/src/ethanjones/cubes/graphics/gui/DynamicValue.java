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

  public static DynamicValue zero() {
    return zero;
  }

  public static DynamicValue divideWidth(final int d) {
    return new DynamicValue() {
      @Override
      public int get() {
        return Gdx.graphics.getWidth() / d;
      }
    };
  }

  public static DynamicValue divideHeight(final int d) {
    return new DynamicValue() {
      @Override
      public int get() {
        return Gdx.graphics.getHeight() / d;
      }
    };
  }

  public static DynamicValue divideMultiplyWidth(final int d, final int m) {
    return new DynamicValue() {
      @Override
      public int get() {
        return (Gdx.graphics.getWidth() / d) * m;
      }
    };
  }

  public static DynamicValue divideMultiplyHeight(final int d, final int m) {
    return new DynamicValue() {
      @Override
      public int get() {
        return (Gdx.graphics.getHeight() / d) * m;
      }
    };
  }

  public static DynamicValue value(final int i) {
    return new DynamicValue() {
      @Override
      public int get() {
        return i;
      }
    };
  }
}
