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
        Class<?> type = field.getType();
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
      throw new ConversionError("Cannot convert Lua to Java: " + c.getName());
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

  static {
    BooleanConverter booleanConverter = new BooleanConverter();
    CharConverter charConverter = new CharConverter();
    IntConverter intConverter = new IntConverter();
    DoubleConverter doubleConverter = new DoubleConverter();
    map.put(Boolean.class, booleanConverter);
    map.put(boolean.class, booleanConverter);
    map.put(Byte.class, intConverter);
    map.put(Character.class, charConverter);
    map.put(char.class, charConverter);
    map.put(Short.class, intConverter);
    map.put(short.class, intConverter);
    map.put(Integer.class, intConverter);
    map.put(int.class, intConverter);
    map.put(Long.class, doubleConverter);
    map.put(long.class, doubleConverter);
    map.put(Float.class, doubleConverter);
    map.put(float.class, doubleConverter);
    map.put(Double.class, doubleConverter);
    map.put(double.class, doubleConverter);
    map.put(String.class, new StringConverter());
    map.put(Class.class, new LuaClassConverter());
    map.put(BlockReference.class, new BlockReferenceConverter());
    map.put(Vector3.class, new Vector3Converter());
    map.put(AreaReference.class, new AreaReferenceConverter());
    map.put(Object.class, new LuaObjectConverter());
  }

  public static class BooleanConverter implements LuaConverter<Boolean> {

    @Override
    public LuaValue toLua(Boolean aBoolean) {
      return LuaValue.valueOf(aBoolean);
    }

    @Override
    public Boolean toJava(LuaValue l, Class<? extends Boolean> c) {
      return l.checkboolean();
    }
  }

  public static class IntConverter implements LuaConverter<Number> {

    @Override
    public LuaValue toLua(Number number) {
      return LuaValue.valueOf(number.intValue());
    }

    @Override
    public Number toJava(LuaValue l, Class<? extends Number> c) {
      return l.checkint();
    }
  }

  public static class DoubleConverter implements LuaConverter<Number> {

    @Override
    public LuaValue toLua(Number number) {
      return LuaValue.valueOf(number.doubleValue());
    }

    @Override
    public Number toJava(LuaValue l, Class<? extends Number> c) {
      return l.checkdouble();
    }
  }

  public static class CharConverter implements LuaConverter<Character> {

    @Override
    public LuaValue toLua(Character character) {
      return LuaValue.valueOf(character);
    }

    @Override
    public Character toJava(LuaValue l, Class<? extends Character> c) {
      return (char) l.checkint();
    }
  }

  public static class StringConverter implements LuaConverter<String> {

    @Override
    public LuaValue toLua(String s) {
      return LuaValue.valueOf(s);
    }

    @Override
    public String toJava(LuaValue l, Class<? extends String> c) {
      return l.checkjstring();
    }
  }
  
  public static class BlockReferenceConverter implements LuaConverter<BlockReference> {

    @Override
    public LuaValue toLua(BlockReference blockReference) {
      return new LuaVector(blockReference).toUserdata();
    }

    @Override
    public BlockReference toJava(LuaValue l, Class<? extends BlockReference> c) {
      return ((LuaVector) l.checkuserdata(LuaVector.class)).toBlockReference();
    }

  }

  public static class Vector3Converter implements LuaConverter<Vector3> {

    @Override
    public LuaValue toLua(Vector3 vector3) {
      return new LuaVector(vector3).toUserdata();
    }

    @Override
    public Vector3 toJava(LuaValue l, Class<? extends Vector3> c) {
      return ((LuaVector) l.checkuserdata(LuaVector.class)).toVector3();
    }

  }

  public static class AreaReferenceConverter implements LuaConverter<AreaReference> {

    @Override
    public LuaValue toLua(AreaReference areaReference) {
      return new LuaVector(areaReference).toUserdata();
    }

    @Override
    public AreaReference toJava(LuaValue l, Class<? extends AreaReference> c) {
      return ((LuaVector) l.checkuserdata(LuaVector.class)).toAreaReference();
    }

  }
  
  public static class LuaObjectConverter implements LuaConverter {
    @Override
    public LuaValue toLua(Object o) {
      return new LuaObject(o);
    }
  
    @Override
    public Object toJava(LuaValue l, Class c) {
      if (l instanceof LuaObject) {
        Object o = ((LuaObject) l).getObject();
        if (c.isInstance(o)) {
          return o;
        } else {
          throw new LuaError("Cannot convert Lua to Java: " + o.getClass().getName() + " cannot be converted to " + c.getName());
        }
      }
      throw new LuaError("Cannot convert Lua to Java: " + l.typename() + " cannot be converted to " + c.getName());
    }
  }
  
  public static class LuaClassConverter implements LuaConverter<Class> {
  
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
