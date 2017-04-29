package ethanjones.cubes.core.lua;

import ethanjones.cubes.world.reference.AreaReference;
import ethanjones.cubes.world.reference.BlockReference;

import com.badlogic.gdx.math.Vector3;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;

import static ethanjones.cubes.world.storage.Area.SIZE_BLOCKS;

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

    LuaTable m = new LuaTable();

    m.set("__newindex", new ThreeArgFunction() {
      @Override
      public LuaValue call(LuaValue lv, LuaValue k, LuaValue v) {
        String s = k.tojstring();
        switch (s) {
          case "x":
          case "blockX":
            ensure(lv).x = v.checkdouble();
            break;
          case "y":
          case "blockY":
            ensure(lv).y = v.checkdouble();
            break;
          case "z":
          case "blockZ":
            ensure(lv).z = v.checkdouble();
            break;
          case "areaX":
            ensure(lv).x = v.checkint() * SIZE_BLOCKS;
            break;
          case "areaZ":
            ensure(lv).z = v.checkint() * SIZE_BLOCKS;
            break;
          default:
            return error("Cannot set \"" + s + "\"");
        }
        return NIL;
      }
    });
    m.set("__tostring", new OneArgFunction() {
      @Override
      public LuaValue call(LuaValue arg) {
        return valueOf(ensure(arg).toString());
      }
    });
    m.set("__len", new OneArgFunction() {
      final LuaInteger three = valueOf(3);

      @Override
      public LuaValue call(LuaValue arg) {
        return three;
      }
    });
    m.set("__metatable", LuaValue.TRUE);

    final VarArgFunction values = new VarArgFunction() {
      @Override
      public Varargs invoke(Varargs args) {
        LuaVector l = ensure(args.arg1());
        return varargsOf(new LuaValue[]{valueOf(l.x), valueOf(l.y), valueOf(l.z)});
      }
    };
    final VarArgFunction blockValues = new VarArgFunction() {
      @Override
      public Varargs invoke(Varargs args) {
        LuaVector l = ensure(args.arg1());
        return varargsOf(new LuaValue[]{valueOf((int) Math.floor(l.x)), valueOf((int) Math.floor(l.y)), valueOf((int) Math.floor(l.z))});
      }
    };
    final VarArgFunction areaValues = new VarArgFunction() {
      @Override
      public Varargs invoke(Varargs args) {
        LuaVector l = ensure(args.arg1());
        return varargsOf(valueOf((int) Math.floor(l.x / SIZE_BLOCKS)), valueOf((int) Math.floor(l.z / SIZE_BLOCKS)));
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

    final TwoVectorArgFunction add = new TwoVectorArgFunction() {
      @Override
      public LuaValue function(LuaVector a, LuaVector b) {
        return a.add(b).toUserdata();
      }
    };
    final TwoVectorArgFunction sub = new TwoVectorArgFunction() {
      @Override
      public LuaValue function(LuaVector a, LuaVector b) {
        return a.sub(b).toUserdata();
      }
    };
    final TwoVectorArgFunction dot = new TwoVectorArgFunction() {
      @Override
      public LuaValue function(LuaVector a, LuaVector b) {
        return valueOf(a.dot(b));
      }
    };
    final TwoVectorArgFunction cross = new TwoVectorArgFunction() {
      @Override
      public LuaValue function(LuaVector a, LuaVector b) {
        return a.cross(b).toUserdata();
      }
    };
    final TwoArgFunction mul = new TwoArgFunction() {
      @Override
      public LuaValue call(LuaValue arg1, LuaValue arg2) {
        return ensure(arg1).mul(arg2.checkdouble()).toUserdata();
      }
    };

    m.set("__add", add);
    m.set("__sub", sub);
    m.set("__mul", mul);

    m.set("__index", new TwoArgFunction() {
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
          case "blockX":
            return valueOf((int) Math.floor(l.x));
          case "blockY":
            return valueOf((int) Math.floor(l.y));
          case "blockZ":
            return valueOf((int) Math.floor(l.z));
          case "areaX":
            return valueOf((int) Math.floor(l.x / SIZE_BLOCKS));
          case "areaZ":
            return valueOf((int) Math.floor(l.z / SIZE_BLOCKS));
          case "values":
            return values;
          case "blockValues":
            return blockValues;
          case "areaValues":
            return areaValues;
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
    m.rawset("__metatable", LuaValue.FALSE);
    metatable = new ReadOnlyLuaTable(m);
  }

  private static abstract class TwoVectorArgFunction extends VarArgFunction {

    @Override
    public Varargs invoke(Varargs args) {
      LuaVector a = ensure(args.arg1());
      LuaVector b = null;
      if (args.arg(2).isuserdata(LuaVector.class)) {
        b = ensure(args.arg(2));
      } else {
        b = new LuaVector(args.checkdouble(2), args.checkdouble(3), args.checkdouble(4));
      }
      return function(a, b);
    }

    public abstract LuaValue function(LuaVector a, LuaVector b);

  }
}
