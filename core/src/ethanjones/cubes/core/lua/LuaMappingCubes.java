package ethanjones.cubes.core.lua;

import org.luaj.vm2.LuaTable;

public class LuaMappingCubes {

  // populated by IDManager.loaded()
  public static LuaTable blocks = new LuaTable();
  public static LuaTable items = new LuaTable();

  public static Class world = LuaMappingWorld.class;
}
