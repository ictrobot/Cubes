package ethanjones.cubes.core.lua.java;

import ethanjones.cubes.core.lua.convert.LuaConversion;
import ethanjones.cubes.core.lua.convert.LuaConversion.ConversionError;
import ethanjones.cubes.core.lua.convert.LuaExclude;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaUserdata;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

public class LuaObject extends LuaUserdata {
  private final Object o;
  private final Class<?> c;
  
  public LuaObject(final Object o) {
    super(new Object());
    this.o = o;
    this.c = o.getClass();
  
    LuaTable metatable = new LuaTable();
    metatable.rawset("__index", new TwoArgFunction() {
      @Override
      public LuaValue call(LuaValue arg1, LuaValue arg2) {
        String s = arg2.tojstring();
        try {
          Field field = c.getField(s);
          if (field.getAnnotation(LuaExclude.class) == null) {
            return LuaConversion.convertToLua(field.get(o));
          }
        } catch (Exception ignored) {
        }
        try {
          Method[] methods = c.getMethods();
          ArrayList<Method> validMethods = new ArrayList<Method>();
          for (Method method : methods) {
            if (method.getAnnotation(LuaExclude.class) == null && method.getName().equals(s)) {
              validMethods.add(method);
            }
          }
          if (validMethods.size() > 0) return new LuaMethod(validMethods, c, s);
        } catch (Exception ignored) {
        }
        error("No such field or method: " + s);
        return null;
      }
    });
    metatable.rawset("__newindex", new ThreeArgFunction() {
      @Override
      public LuaValue call(LuaValue arg1, LuaValue arg2, LuaValue arg3) {
        String s = arg2.tojstring();
        try {
          Field field = c.getField(s);
          int modifiers = field.getModifiers();
          if (Modifier.isStatic(modifiers) && !Modifier.isFinal(modifiers) && field.getAnnotation(LuaExclude.class) == null) {
            Class<?> type = field.getType();
            Object value = LuaConversion.convertToJava(type, arg3);
            field.set(o, value);
            return NIL;
          }
        } catch (Exception e) {
          if (e instanceof ConversionError) throw (ConversionError) e;
        }
        error("No such field: " + s);
        return null;
      }
    });
    metatable.rawset("__tostring", new ZeroArgFunction() {
      @Override
      public LuaValue call() {
        return LuaValue.valueOf(String.valueOf(o));
      }
    });
    metatable.rawset("__metatable", LuaValue.FALSE);
    setmetatable(metatable);
  }
  
  public Object getObject() {
    return o;
  }
}
