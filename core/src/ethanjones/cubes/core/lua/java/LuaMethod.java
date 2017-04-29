package ethanjones.cubes.core.lua.java;

import ethanjones.cubes.core.lua.convert.LuaConversion;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

public class LuaMethod extends VarArgFunction {
  
  private final List<Method> methods;
  private final Class<?> c;
  private final String methodName;
  
  public LuaMethod(List<Method> methods, Class<?> c, String methodName) {
    this.methods = methods;
    this.c = c;
    this.methodName = methodName;
  }
  
  @Override
  public Varargs invoke(Varargs args) {
    methods: for (Method method : methods) {
      if (Modifier.isStatic(method.getModifiers())) {
        Class[] parameters = method.getParameterTypes();
        if (parameters.length != args.narg()) continue;
        Object[] param = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
          try {
            param[i] = LuaConversion.convertToJava(parameters[i], args.arg(i + 1));
          } catch (Exception ignored) {
            continue methods;
          }
        }
        try {
          Object invoke = method.invoke(null, param);
          return LuaConversion.convertToLua(invoke);
        } catch (Exception e) {
          throw new LuaError(e);
        }
      } else {
        Class[] parameters = method.getParameterTypes();
        if (parameters.length + 1 != args.narg()) continue;
        Object[] param = new Object[parameters.length];
        Object instance = LuaConversion.convertToJava(c, args.arg(1));
        for (int i = 0; i < parameters.length; i++) {
          try {
            param[i] = LuaConversion.convertToJava(parameters[i], args.arg(i + 2));
          } catch (Exception ignored) {
            continue methods;
          }
        }
        try {
          Object invoke = method.invoke(instance, param);
          return LuaConversion.convertToLua(invoke);
        } catch (Exception e) {
          throw new LuaError(e);
        }
      }
    }
    error("No method " + c.getName() + " " + methodName + " for parameters " + args.toString());
    return NIL;
  }
}
