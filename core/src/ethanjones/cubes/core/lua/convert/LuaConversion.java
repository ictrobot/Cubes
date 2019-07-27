package ethanjones.cubes.core.lua.convert;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.lua.LuaVector;
import ethanjones.cubes.core.lua.java.LuaClass;
import ethanjones.cubes.core.lua.java.LuaObject;
import ethanjones.cubes.world.reference.AreaReference;
import ethanjones.cubes.world.reference.BlockReference;

import com.badlogic.gdx.math.Vector3;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaUserdata;
import org.luaj.vm2.LuaValue;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;

import static org.luaj.vm2.LuaValue.NIL;

public class LuaConversion {
  public static HashMap<Class, LuaConverter> map = new HashMap<Class, LuaConverter>();

  public static LuaValue complexToLua(Object o) {
    try {
      Class<?> c = o.getClass();
      Field[] fields = c.getFields();
      LuaTable table = new LuaTable();
      for (Field field : fields) {
        if (!(Modifier.isPublic(field.getModifiers()) || field.isAnnotationPresent(LuaInclude.class)) || field.isAnnotationPresent(LuaExclude.class))
          continue;
        String name = field.getName();
        Object instance = field.get(o);
        LuaValue l = convertToLua(instance);
        table.set(name, l);
      }
      return table;
    } catch (Exception e) {
      Log.warning(e);
    }
    return NIL;
  }

  public static void complexToJava(LuaValue l, Object o) {
    try {
      Field[] fields = o.getClass().getFields();
      LuaTable table = l.checktable();
      for (Field field : fields) {
        if (!(Modifier.isPublic(field.getModifiers()) || field.isAnnotationPresent(LuaInclude.class)) || field.isAnnotationPresent(LuaExclude.class))
          continue;
        if (Modifier.isFinal(field.getModifiers())) continue;
        String name = field.getName();
        Class<?> type = field.getType();
        LuaValue v = table.get(name);
        Object instance = convertToJava(type, v);
        field.set(o, instance);
      }
    } catch (Exception e) {
      Log.warning(e);
    }
  }

  public static LuaValue convertToLua(Object o) {
    if (o == null) return NIL;
    LuaConverter luaConverter = converter(o.getClass());
    if (luaConverter == null) {
      throw new ConversionError("Cannot convert Java to Lua: " + o.getClass().getName());
    }
    return luaConverter.toLua(o);
  }

  public static Object convertToJava(Class<?> c, LuaValue o) {
    if (o == null || o.isnil()) return null;
    LuaConverter luaConverter = converter(c);
    if (luaConverter == null) {
      if (o instanceof LuaObject) {
        luaConverter = map.get(Object.class);
      } else {
        throw new ConversionError("Cannot convert Lua to Java: " + c.getName());
      }
    }
    return luaConverter.toJava(o, c);
  }

  private static LuaConverter converter(Class c) {
    LuaConverter l = null;
    outer:
    while (c != null) {
      l = map.get(c);
      if (l != null) break;
      for (Class c2 : c.getInterfaces()) {
        l = map.get(c2);
        if (l != null) break outer;
      }
      if (Object.class.equals(c)) break;
      c = c.getSuperclass();
    }
    return l;
  }

  private static void register(LuaConverter converter, Class<?>... classes) {
    for (Class<?> clazz : classes) {
      map.put(clazz, converter);
    }
  }

  static {
    register(new LuaConverter<Boolean>() {
      @Override
      public LuaValue toLua(Boolean aBoolean) {
        return LuaValue.valueOf(aBoolean);
      }

      @Override
      public Boolean toJava(LuaValue l, Class<? extends Boolean> c) {
        return l.checkboolean();
      }
    }, Boolean.class, boolean.class);

    register(new LuaConverter<Byte>() {
      @Override
      public LuaValue toLua(Byte aByte) {
        return LuaValue.valueOf(aByte);
      }

      @Override
      public Byte toJava(LuaValue l, Class<? extends Byte> c) {
        return l.tobyte();
      }
    }, Byte.class, byte.class);

    register(new LuaConverter<Character>() {
      @Override
      public LuaValue toLua(Character aCharacter) {
        return LuaValue.valueOf(aCharacter);
      }

      @Override
      public Character toJava(LuaValue l, Class<? extends Character> c) {
        return l.tochar();
      }
    }, Character.class, char.class);

    register(new LuaConverter<Short>() {
      @Override
      public LuaValue toLua(Short aShort) {
        return LuaValue.valueOf(aShort);
      }

      @Override
      public Short toJava(LuaValue l, Class<? extends Short> c) {
        return l.toshort();
      }
    }, Short.class, short.class);

    register(new LuaConverter<Integer>() {
      @Override
      public LuaValue toLua(Integer anInt) {
        return LuaValue.valueOf(anInt);
      }

      @Override
      public Integer toJava(LuaValue l, Class<? extends Integer> c) {
        return l.toint();
      }
    }, Integer.class, int.class);

    register(new LuaConverter<Long>() {
      @Override
      public LuaValue toLua(Long aLong) {
        return LuaValue.valueOf(aLong);
      }

      @Override
      public Long toJava(LuaValue l, Class<? extends Long> c) {
        return l.tolong();
      }
    }, Long.class, long.class);

    register(new LuaConverter<Float>() {
      @Override
      public LuaValue toLua(Float aFloat) {
        return LuaValue.valueOf(aFloat);
      }

      @Override
      public Float toJava(LuaValue l, Class<? extends Float> c) {
        return l.tofloat();
      }
    }, Float.class, float.class);

    register(new LuaConverter<Double>() {
      @Override
      public LuaValue toLua(Double aDouble) {
        return LuaValue.valueOf(aDouble);
      }

      @Override
      public Double toJava(LuaValue l, Class<? extends Double> c) {
        return l.todouble();
      }
    }, Double.class, double.class);

    register(new LuaConverter<String>() {
      @Override
      public LuaValue toLua(String str) {
        return LuaValue.valueOf(str);
      }

      @Override
      public String toJava(LuaValue l, Class<? extends String> c) {
        return l.toString();
      }
    }, String.class);

    map.put(Class.class, new LuaClassConverter());
    map.put(BlockReference.class, new BlockReferenceConverter());
    map.put(Vector3.class, new Vector3Converter());
    map.put(AreaReference.class, new AreaReferenceConverter());
    map.put(Object.class, new LuaObjectConverter());
  }

  private static class BlockReferenceConverter implements LuaConverter<BlockReference> {

    @Override
    public LuaValue toLua(BlockReference blockReference) {
      return new LuaVector(blockReference).toUserdata();
    }

    @Override
    public BlockReference toJava(LuaValue l, Class<? extends BlockReference> c) {
      return ((LuaVector) l.checkuserdata(LuaVector.class)).toBlockReference();
    }

  }

  private static class Vector3Converter implements LuaConverter<Vector3> {

    @Override
    public LuaValue toLua(Vector3 vector3) {
      return new LuaVector(vector3).toUserdata();
    }

    @Override
    public Vector3 toJava(LuaValue l, Class<? extends Vector3> c) {
      return ((LuaVector) l.checkuserdata(LuaVector.class)).toVector3();
    }

  }

  private static class AreaReferenceConverter implements LuaConverter<AreaReference> {

    @Override
    public LuaValue toLua(AreaReference areaReference) {
      return new LuaVector(areaReference).toUserdata();
    }

    @Override
    public AreaReference toJava(LuaValue l, Class<? extends AreaReference> c) {
      return ((LuaVector) l.checkuserdata(LuaVector.class)).toAreaReference();
    }

  }

  private static class LuaObjectConverter implements LuaConverter {
    @Override
    public LuaValue toLua(Object o) {
      return new LuaObject(o);
    }
  
    @Override
    public Object toJava(LuaValue l, Class c) {
      Object o;
      if (l instanceof LuaObject) {
        o = ((LuaObject) l).getObject();
      } else if (l instanceof LuaUserdata) {
        o = ((LuaUserdata) l).m_instance;
      } else {
        throw new LuaError("Cannot convert Lua to Java: " + l.typename() + " cannot be converted to " + c.getName());
      }

      if (c.isInstance(o)) {
        return o;
      } else {
        throw new LuaError("Cannot convert Lua to Java: " + o.getClass().getName() + " cannot be converted to " + c.getName());
      }
    }
  }
  
  private static class LuaClassConverter implements LuaConverter<Class> {
  
    @Override
    public LuaValue toLua(Class aClass) {
      return new LuaClass(aClass);
    }
  
    @Override
    public Class toJava(LuaValue l, Class<? extends Class> c) {
      if (l instanceof LuaClass) return ((LuaClass) l).getJavaClass();
      throw new LuaError("Cannot convert Lua to Java: " + l.typename() + " cannot be converted to " + c.getName());
    }
  }
  
  public static class ConversionError extends LuaError {
  
    public ConversionError(Throwable cause) {
      super(cause);
    }
  
    public ConversionError(String message) {
      super(message);
    }
  
    public ConversionError(String message, int level) {
      super(message, level);
    }
  
    public ConversionError(LuaValue message_object) {
      super(message_object);
    }
  }
}
