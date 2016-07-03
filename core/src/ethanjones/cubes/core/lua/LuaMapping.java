package ethanjones.cubes.core.lua;

import ethanjones.cubes.core.system.CubesException;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public final class LuaMapping {

  public static LuaTable mapping(Class<?> c) {
    try {
      LuaTable luaTable = new LuaTable();
      for (Field field : c.getFields()) {
        if (!Modifier.isStatic(field.getModifiers())) continue;
        if (LuaValue.class.isAssignableFrom(field.getType())) {
          luaTable.set(field.getName(), (LuaValue) field.get(null));
        }
        if (field.getType().equals(Class.class)) {
          luaTable.set(field.getName(), mapping((Class<?>) field.get(null)));
        }
      }
      return luaTable;
    } catch (Exception e) {
      throw new CubesException("Failed to create lua api", e);
    }
  }

  public static LuaTable mapping(Object o) {
    try {
      LuaTable luaTable = new LuaTable();
      for (Field field : o.getClass().getFields()) {
        if (Modifier.isStatic(field.getModifiers())) continue;
        if (LuaValue.class.isAssignableFrom(field.getType())) {
          luaTable.set(field.getName(), (LuaValue) field.get(o));
        }
        if (field.getType().equals(Class.class)) {
          luaTable.set(field.getName(), mapping((Class<?>) field.get(o)));
        }
      }
      return luaTable;
    } catch (Exception e) {
      throw new CubesException("Failed to create lua api", e);
    }
  }
}
