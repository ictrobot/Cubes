package ethanjones.cubes.core.lua;

import ethanjones.cubes.core.logging.Log;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

public class LuaMappingLog {

  public static final OneArgFunction error = new OneArgFunction() {
    @Override
    public LuaValue call(LuaValue arg) {
      String s = arg.tojstring();
      Log.error(s);
      return NIL;
    }
  };

  public static final OneArgFunction warning = new OneArgFunction() {
    @Override
    public LuaValue call(LuaValue arg) {
      String s = arg.tojstring();
      Log.warning(s);
      return NIL;
    }
  };

  public static final OneArgFunction debug = new OneArgFunction() {
    @Override
    public LuaValue call(LuaValue arg) {
      String s = arg.tojstring();
      Log.debug(s);
      return NIL;
    }
  };

  public static final OneArgFunction info = new OneArgFunction() {
    @Override
    public LuaValue call(LuaValue arg) {
      String s = arg.tojstring();
      Log.info(s);
      return NIL;
    }
  };

}
