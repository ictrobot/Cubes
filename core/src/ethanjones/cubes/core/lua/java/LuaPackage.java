package ethanjones.cubes.core.lua.java;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaUserdata;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

public class LuaPackage extends LuaUserdata {
  
  private final String path;
  
  public LuaPackage(final String path) {
    super(new Object());
    this.path = path;
    
    LuaTable metatable = new LuaTable();
    metatable.rawset("__index", new TwoArgFunction() {
      @Override
      public LuaValue call(LuaValue arg1, LuaValue arg2) {
        if (arg2.checkjstring().equals("class")) {
          try {
            Class<?> cla = Class.forName(path);
            return new LuaClass(cla);
          } catch (ClassNotFoundException e) {
            throw new LuaError("No such class: " + path);
          }
        }
        String name = path.isEmpty() ? arg2.checkjstring() : path + "." + arg2.checkjstring();
        return new LuaPackage(name);
      }
    });
    metatable.rawset("__tostring", new ZeroArgFunction() {
      @Override
      public LuaValue call() {
        if (path.isEmpty()) return LuaValue.valueOf("[JPackage Root]");
        return LuaValue.valueOf("[JPackage: " + path + "]");
      }
    });
    metatable.rawset("__metatable", LuaValue.FALSE);
    setmetatable(metatable);
  }
}
