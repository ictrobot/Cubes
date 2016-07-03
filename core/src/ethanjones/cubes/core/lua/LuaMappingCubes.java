package ethanjones.cubes.core.lua;

import ethanjones.cubes.core.platform.Adapter;
import ethanjones.cubes.core.platform.Compatibility;
import ethanjones.cubes.core.system.Branding;
import ethanjones.cubes.side.Sided;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

public class LuaMappingCubes {

  // populated by IDManager.loaded()
  public static LuaTable blocks = new LuaTable();
  public static LuaTable items = new LuaTable();

  public static ZeroArgFunction world = new ZeroArgFunction() {
    @Override
    public LuaValue call() {
      return Sided.getCubes().world.lua;
    }
  };

  public static ZeroArgFunction isDedicatedServer = new ZeroArgFunction() {
    @Override
    public LuaValue call() {
      return LuaValue.valueOf(Adapter.isDedicatedServer());
    }
  };

  public static ZeroArgFunction getApplicationType = new ZeroArgFunction() {
    @Override
    public LuaValue call() {
      return LuaValue.valueOf(Compatibility.get().getApplicationType().toString());
    }
  };

  public static ZeroArgFunction getVersion = new ZeroArgFunction() {
    @Override
    public LuaValue call() {
      return LuaValue.valueOf(Branding.VERSION_MAJOR_MINOR_POINT);
    }
  };

  public static ZeroArgFunction getBuild = new ZeroArgFunction() {
    @Override
    public LuaValue call() {
      return LuaValue.valueOf(Branding.VERSION_BUILD);
    }
  };
}
