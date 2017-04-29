package ethanjones.cubes.core.lua.generation;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.lua.convert.LuaConversion;
import ethanjones.cubes.core.platform.Compatibility;
import ethanjones.cubes.core.system.CubesException;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.NamingStrategy.SuffixingRandom;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.dynamic.DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition;
import net.bytebuddy.dynamic.DynamicType.Loaded;
import net.bytebuddy.dynamic.DynamicType.Unloaded;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.SuperMethodCall;
import net.bytebuddy.implementation.bind.annotation.*;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatcher.Junction;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.concurrent.Callable;

import static net.bytebuddy.matcher.ElementMatchers.*;

public class LuaGeneration {
  private static final ByteBuddy b = new ByteBuddy(ClassFileVersion.JAVA_V6).with(new SuffixingRandom("LuaDynamic", "ethanjones.cubes.core.lua.redefined"));
  
  public static Class extendClass(Class<?> extend, final LuaTable delegations, Class<?>... inherit) {
    long startTime = System.nanoTime();
  
    ArrayDeque<Class> toCheck = new ArrayDeque<Class>();
    toCheck.add(extend);
    toCheck.addAll(Arrays.asList(inherit));
    while (!toCheck.isEmpty()) {
      Class check = toCheck.pop();
      for (Method method : check.getDeclaredMethods()) {
        if (Modifier.isAbstract(method.getModifiers())) {
          if (delegations.get(method.getName()).isnil())
            throw new DynamicDelegationError("No delegation for abstract method " + method);
        }
      }
      check = check.getSuperclass();
      if (check != null && check != Object.class) toCheck.add(check);
    }
    
    try {
      ReceiverTypeDefinition<?> build = b.subclass(extend).implement(inherit)
              .method(not(isConstructor()).and(isAbstract())).intercept(MethodDelegation.to(new AbstractInterceptor(delegations)));
      if (!delegations.get("__new__").isnil()) {
        build = build.constructor(isConstructor()).intercept(SuperMethodCall.INSTANCE.andThen(MethodDelegation.to(new ConstructorInterceptor(delegations))));
      }
      Junction<MethodDescription> publicMethods = not(isConstructor().or(isAbstract())).and(isPublic()).and(new ElementMatcher<MethodDescription>() {
        @Override
        public boolean matches(MethodDescription target) {
          return !delegations.get(target.getName()).isnil();
        }
      });
      build = build.method(publicMethods).intercept(MethodDelegation.to(new PublicInterceptor(delegations)));
      
      Unloaded unloaded = build.make();
      Loaded loaded = Compatibility.get().load(unloaded);
      Class c = loaded.getLoaded();
      Log.debug("Created dynamic class " + c.getName() + " in " + ((System.nanoTime() - startTime) / 1000000) + "ms");
      return c;
    } catch (Exception e) {
      Log.error("Failed to create dynamic class " + extend.getName() + " " + Arrays.toString(inherit));
      throw new CubesException("Failed to make dynamic class", e);
    }
  }
  
  public static class PublicInterceptor {
    private final LuaTable delegations;
  
    public PublicInterceptor(LuaTable delegations) {
      this.delegations = delegations;
    }
  
    @RuntimeType
    public Object method(@Origin Method method, @This Object o, @AllArguments Object[] objects, @SuperCall Callable supercall) {
      LuaValue value = delegations.get(method.getName());
      if (value.isnil()) {
        try {
          return supercall.call();
        } catch (Exception e) {
          throw new DynamicDelegationError("Supercall threw exception - no delegation supplied", e);
        }
      }
      if (value.isfunction()) {
        LuaFunction function = value.checkfunction();
        LuaValue[] parameters = convertParamsToLua(o, objects, supercall);
        Varargs invoke = function.invoke(parameters);
        if (method.getReturnType() == Void.class || invoke.narg() == 0) return null;
        return LuaConversion.convertToJava(method.getReturnType(), invoke.checkvalue(1));
      } else {
        return LuaConversion.convertToJava(method.getReturnType(), value);
      }
    }
  }
  
  public static class AbstractInterceptor {
    private final LuaTable delegations;
    
    public AbstractInterceptor(LuaTable delegations) {
      this.delegations = delegations;
    }
    
    @RuntimeType
    public Object method(@Origin Method method, @This Object o, @AllArguments Object[] objects) {
      LuaValue value = delegations.get(method.getName());
      if (value.isnil()) return null;
      if (value.isfunction()) {
        LuaFunction function = value.checkfunction();
        LuaValue[] parameters = convertParamsToLua(o, objects);
        Varargs invoke = function.invoke(parameters);
        if (method.getReturnType() == Void.class || invoke.narg() == 0) return null;
        return LuaConversion.convertToJava(method.getReturnType(), invoke.checkvalue(1));
      } else {
        return LuaConversion.convertToJava(method.getReturnType(), value);
      }
    }
  }
  
  public static class ConstructorInterceptor {
    private final LuaTable delegations;
    
    public ConstructorInterceptor(LuaTable delegations) {
      this.delegations = delegations;
    }
    
    public void constructor(@This Object o, @AllArguments Object[] objects) { // @Origin Constructor constructor,
      LuaValue value = delegations.get("__new__");
      if (value.isnil()) return;
      if (!value.isfunction()) throw new DynamicDelegationError("__new__ for " + o.getClass().getName() + " is a " + value.typename());
      LuaFunction function = value.checkfunction();
      LuaValue[] parameters = convertParamsToLua(o, objects);
      function.invoke(parameters);
    }
  }
  
  public static LuaValue[] convertParamsToLua(Object instance, Object[] objects) {
    LuaValue[] parameters = new LuaValue[objects.length + 1];
    parameters[0] = LuaConversion.convertToLua(instance);
    for (int i = 0; i < objects.length; i++) {
      parameters[i + 1] = LuaConversion.convertToLua(objects[i]);
    }
    return parameters;
  }
  
  public static LuaValue[] convertParamsToLua(Object instance, Object[] objects, final Callable supercall) {
    LuaValue[] parameters = new LuaValue[objects.length + 2];
    parameters[0] = LuaConversion.convertToLua(instance);
    for (int i = 0; i < objects.length; i++) {
      parameters[i + 1] = LuaConversion.convertToLua(objects[i]);
    }
    parameters[parameters.length - 1] = new VarArgFunction() {
      @Override
      public Varargs invoke(Varargs args) {
        try {
          Object o = supercall.call();
          return LuaConversion.convertToLua(o);
        } catch (Exception e) {
          throw new DynamicDelegationError("Supercall threw exception - called from supplied delegation", e);
        }
      }
    };
    return parameters;
  }
  
  public static class DynamicDelegationError extends RuntimeException {
  
    public DynamicDelegationError(Throwable cause) {
      super(cause);
    }
  
    public DynamicDelegationError(String message) {
      super(message);
    }
    
    public DynamicDelegationError(String message, Throwable cause) {
      super(message, cause);
    }
  }
}
