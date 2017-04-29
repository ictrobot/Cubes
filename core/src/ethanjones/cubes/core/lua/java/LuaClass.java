package ethanjones.cubes.core.lua.java;

import ethanjones.cubes.core.lua.convert.LuaConversion;
import ethanjones.cubes.core.lua.convert.LuaConversion.ConversionError;
import ethanjones.cubes.core.lua.convert.LuaExclude;

import org.luaj.vm2.*;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;

public class LuaClass extends LuaUserdata {
  private final Class<?> c;
  
  public LuaClass(final Class c) {
    super(new Object());
    this.c = c;
  
    LuaTable metatable = new LuaTable();
    metatable.rawset("__index", new TwoArgFunction() {
      @Override
      public LuaValue call(LuaValue arg1, LuaValue arg2) {
        String s = arg2.tojstring();
        try {
          Field field = c.getField(s);
          int modifiers = field.getModifiers();
          if (Modifier.isStatic(modifiers) && field.getAnnotation(LuaExclude.class) == null) {
            return LuaConversion.convertToLua(field.get(null));
          }
        } catch (Exception ignored) {
        }
        try {
          Method[] methods = c.getMethods();
          ArrayList<Method> validMethods = new ArrayList<Method>();
          for (Method method : methods) {
            int modifiers = method.getModifiers();
            if (Modifier.isStatic(modifiers) && method.getAnnotation(LuaExclude.class) == null && method.getName().equals(s)) {
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
            Object o = LuaConversion.convertToJava(type, arg3);
            field.set(null, o);
            return NIL;
          }
        } catch (Exception e) {
          if (e instanceof ConversionError) throw (ConversionError) e;
        }
        error("No such field: " + s);
        return null;
      }
    });
    metatable.rawset("__call", new VarArgFunction() {
  
      @Override
      public Varargs invoke(Varargs args) {
        Constructor[] constructors = c.getConstructors();
        if (constructors.length == 0) error("No constructors");
        constructors: for (Constructor constructor : constructors) {
          if (constructor.getAnnotation(LuaExclude.class) != null) continue;
          Class[] parameters = constructor.getParameterTypes();
          if (parameters.length + 1 != args.narg()) continue;
          Object[] param = new Object[parameters.length];
          for (int i = 0; i < parameters.length; i++) {
            try {
              param[i] = LuaConversion.convertToJava(parameters[i], args.arg(i + 2));
            } catch (Exception ignored) {
              continue constructors;
            }
          }
          try {
            return LuaConversion.convertToLua(constructor.newInstance(param));
          } catch (Exception e) {
            throw new LuaError(e);
          }
        }
        argerror(Arrays.toString(constructors));
        return null;
      }
    });
    metatable.rawset("__tostring", new ZeroArgFunction() {
      @Override
      public LuaValue call() {
        return LuaValue.valueOf("[JClass: " + LuaClass.this.c.getName() + "]");
      }
    });
    metatable.rawset("__metatable", LuaValue.FALSE);
    setmetatable(metatable);
  }
  
  public Class getJavaClass() {
    return c;
  }
}
