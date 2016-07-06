package ethanjones.cubes.core.lua;

import ethanjones.cubes.world.reference.AreaReference;
import ethanjones.cubes.world.reference.BlockReference;

import com.badlogic.gdx.math.Vector3;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;

public class LuaVector {
  protected static final VarArgFunction create;
  private static final LuaTable metatable;

  double x = 0; // doubles can represent 32 bit ints
  double y = 0;
  double z = 0;

  LuaUserdata luaUserdata;

  public LuaVector() {

  }

  public LuaVector(double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public LuaVector(LuaVector l) {
    this.x = l.x;
    this.y = l.y;
    this.z = l.z;
  }

  public LuaVector(BlockReference r) {
    this.x = r.blockX;
    this.y = r.blockY;
    this.z = r.blockZ;
  }

  public LuaVector(Vector3 v) {
    this.x = v.x;
    this.y = v.y;
    this.z = v.z;
  }

  public LuaVector(AreaReference a) {
    this.x = a.minBlockX();
    this.y = 0; // unknown value?
    this.z = a.minBlockZ();
  }

  public LuaUserdata toUserdata() {
    if (luaUserdata == null) {
      luaUserdata = new LuaUserdata(this, metatable);
    }
    return luaUserdata;
  }

  public BlockReference toBlockReference() {
    return new BlockReference().setFromPosition((float) x, (float) y, (float) z);
  }

  public Vector3 toVector3() {
    return new Vector3((float) x, (float) y, (float) z);
  }

  public AreaReference toAreaReference() {
    return new AreaReference().setFromPosition((float) x, (float) z);
  }

  @Override
  public String toString() {
    if (isBlock()) {
      return ((int) x) + "," + ((int) y) + "," + ((int) z);
    } else {
      return x + "," + y + "," + z;
    }
  }

  public boolean isBlock() {
    return ((int) x) == x && ((int) y) == y && ((int) z) == z;
  }

  public LuaVector block() {
    return new LuaVector(Math.floor(x), Math.floor(y), Math.floor(z));
  }

  public LuaVector round() {
    return new LuaVector((double) Math.round(x), (double) Math.round(y), (double) Math.round(z));
  }

  public double len() {
    return Math.sqrt((x * x) + (y * y) + (z * z));
  }

  public LuaVector nor() {
    double len = len();
    if (len == 0 || len == 1) return new LuaVector(this);
    return new LuaVector(x / len, y / len, z / len);
  }

  public LuaVector add(LuaVector b) {
    return new LuaVector(this.x + b.x, this.y + b.y, this.z + b.z);
  }

  public LuaVector sub(LuaVector b) {
    return new LuaVector(this.x - b.x, this.y - b.y, this.z - b.z);
  }

  public LuaVector mul(double d) {
    return new LuaVector(this.x * d, this.y + d, this.z + d);
  }

  public double dot(LuaVector b) {
    return ((this.x * b.x) + (this.y * b.y) + (this.z * b.z));
  }

  public LuaVector cross(LuaVector b) {
    return new LuaVector(
            (this.y * b.z) - (this.z * b.y),
            (this.z * b.x) - (this.x * b.z),
            (this.x * b.y) - (this.y * b.x)
    );
  }

  private static LuaVector ensure(LuaValue v) {
    return (LuaVector) v.checkuserdata(LuaVector.class);
  }

  public static LuaVector of(Object o) {
    if (o instanceof LuaVector) return new LuaVector((LuaVector) o);
    if (o instanceof BlockReference) return new LuaVector((BlockReference) o);
    if (o instanceof AreaReference) return new LuaVector((AreaReference) o);
    if (o instanceof Vector3) return new LuaVector((Vector3) o);
    if (o instanceof LuaValue) {
      LuaValue l = (LuaValue) o;
      if (l.isuserdata(LuaValue.class)) return new LuaVector((LuaVector) l.checkuserdata());
    }
    throw new IllegalArgumentException(String.valueOf(o));
  }

  static {
    create = new VarArgFunction() {
      @Override
      public Varargs invoke(Varargs args) {
        int i = args.narg();
        if (i == 0) {
          return new LuaVector().toUserdata();
        } else if (i == 3) {
          return new LuaVector(args.checkdouble(1), args.checkdouble(2), args.checkdouble(3)).toUserdata();
        }
        argerror("Expected 0 or 3 arguments");
        return NIL;
      }
    };

    metatable = new LuaTable();

    metatable.set("__newindex", new ThreeArgFunction() {
      @Override
      public LuaValue call(LuaValue lv, LuaValue k, LuaValue v) {
        String s = k.tojstring();
        if ("x".equals(s)) ensure(lv).x = v.checkdouble();
        if ("y".equals(s)) ensure(lv).y = v.checkdouble();
        if ("z".equals(s)) ensure(lv).z = v.checkdouble();
        return NIL;
      }
    });
    metatable.set("__tostring", new OneArgFunction() {
      @Override
      public LuaValue call(LuaValue arg) {
        return valueOf(ensure(arg).toString());
      }
    });
    metatable.set("__len", new OneArgFunction() {
      final LuaInteger three = valueOf(3);

      @Override
      public LuaValue call(LuaValue arg) {
        return three;
      }
    });
    metatable.set("__metatable", LuaValue.NIL);

    final VarArgFunction values = new VarArgFunction() {
      @Override
      public Varargs invoke(Varargs args) {
        LuaVector l = ensure(args.arg1());
        return varargsOf(new LuaValue[]{valueOf(l.x), valueOf(l.y), valueOf(l.z)});
      }
    };

    final OneArgFunction isBlock = new OneArgFunction() {
      @Override
      public LuaValue call(LuaValue arg) {
        return valueOf(ensure(arg).isBlock());
      }
    };
    final OneArgFunction block = new OneArgFunction() {
      @Override
      public LuaValue call(LuaValue arg) {
        return ensure(arg).block().toUserdata();
      }
    };
    final OneArgFunction round = new OneArgFunction() {
      @Override
      public LuaValue call(LuaValue arg) {
        return ensure(arg).round().toUserdata();
      }
    };

    final OneArgFunction len = new OneArgFunction() {
      @Override
      public LuaValue call(LuaValue arg) {
        return valueOf(ensure(arg).len());
      }
    };
    final OneArgFunction nor = new OneArgFunction() {
      @Override
      public LuaValue call(LuaValue arg) {
        return ensure(arg).nor().toUserdata();
      }
    };

    final TwoArgFunction add = new TwoArgFunction() {
      @Override
      public LuaValue call(LuaValue arg1, LuaValue arg2) {
        return ensure(arg1).add(ensure(arg2)).toUserdata();
      }
    };
    final TwoArgFunction sub = new TwoArgFunction() {
      @Override
      public LuaValue call(LuaValue arg1, LuaValue arg2) {
        return ensure(arg1).sub(ensure(arg2)).toUserdata();
      }
    };
    final TwoArgFunction mul = new TwoArgFunction() {
      @Override
      public LuaValue call(LuaValue arg1, LuaValue arg2) {
        return ensure(arg1).mul(arg2.checkdouble()).toUserdata();
      }
    };
    final TwoArgFunction dot = new TwoArgFunction() {
      @Override
      public LuaValue call(LuaValue arg1, LuaValue arg2) {
        return valueOf(ensure(arg1).dot(ensure(arg2)));
      }
    };
    final TwoArgFunction cross = new TwoArgFunction() {
      @Override
      public LuaValue call(LuaValue arg1, LuaValue arg2) {
        return ensure(arg1).cross(ensure(arg2)).toUserdata();
      }
    };

    metatable.set("__add", add);
    metatable.set("__sub", sub);
    metatable.set("__mul", mul);

    metatable.set("__index", new TwoArgFunction() {
      @Override
      public LuaValue call(LuaValue arg1, LuaValue arg2) {
        LuaVector l = ensure(arg1);
        String s = arg2.tojstring();
        switch (s) {
          case "x":
            return valueOf(l.x);
          case "y":
            return valueOf(l.y);
          case "z":
            return valueOf(l.z);
          case "values":
            return values;
          case "add":
            return add;
          case "sub":
          case "subtract":
            return sub;
          case "mul":
          case "multiply":
            return mul;
          case "dot":
            return dot;
          case "cross":
            return cross;
          case "len":
          case "length":
            return len;
          case "nor":
          case "normalize":
            return nor;
          case "isBlock":
            return isBlock;
          case "block":
            return block;
          case "round":
            return round;
          case "isVector":
            return TRUE;
        }
        return NIL;
      }
    });
  }
}
