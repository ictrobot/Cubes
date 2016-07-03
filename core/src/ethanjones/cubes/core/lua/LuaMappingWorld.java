package ethanjones.cubes.core.lua;

import ethanjones.cubes.block.Block;
import ethanjones.cubes.side.Sided;

import org.luaj.vm2.LuaUserdata;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

public class LuaMappingWorld {

  public static ThreeArgFunction getBlock = new ThreeArgFunction() {
    @Override
    public LuaValue call(LuaValue arg1, LuaValue arg2, LuaValue arg3) {
      int x = arg1.checkint();
      int y = arg2.checkint();
      int z = arg3.checkint();
      return new LuaUserdata(Sided.getCubes().world.getBlock(x, y, z));
    }
  };

  public static ThreeArgFunction getLight = new ThreeArgFunction() {
    @Override
    public LuaValue call(LuaValue arg1, LuaValue arg2, LuaValue arg3) {
      int x = arg1.checkint();
      int y = arg2.checkint();
      int z = arg3.checkint();
      return LuaValue.valueOf(Sided.getCubes().world.getLight(x, y, z));
    }
  };

  public static ThreeArgFunction getSunLight = new ThreeArgFunction() {
    @Override
    public LuaValue call(LuaValue arg1, LuaValue arg2, LuaValue arg3) {
      int x = arg1.checkint();
      int y = arg2.checkint();
      int z = arg3.checkint();
      return LuaValue.valueOf(Sided.getCubes().world.getSunLight(x, y, z));
    }
  };

  public static ThreeArgFunction getLightRaw = new ThreeArgFunction() {
    @Override
    public LuaValue call(LuaValue arg1, LuaValue arg2, LuaValue arg3) {
      int x = arg1.checkint();
      int y = arg2.checkint();
      int z = arg3.checkint();
      return LuaValue.valueOf(Sided.getCubes().world.getLightRaw(x, y, z));
    }
  };

  public static VarArgFunction setBlock = new VarArgFunction() {
    @Override
    public Varargs invoke(Varargs args) {
      int x = args.checkint(1);
      int y = args.checkint(2);
      int z = args.checkint(3);
      Block block = (Block) args.checkuserdata(4, Block.class);
      int meta = args.optint(5, 0);
      Sided.getCubes().world.setBlock(block, x, y, z, meta);
      return NIL;
    }
  };

  public static OneArgFunction setTime = new OneArgFunction() {
    @Override
    public LuaValue call(LuaValue arg) {
      int time = arg.checkint();
      Sided.getCubes().world.setTime(time);
      return NIL;
    }
  };

  public static ZeroArgFunction getTime = new ZeroArgFunction() {
    @Override
    public LuaValue call() {
      return LuaValue.valueOf(Sided.getCubes().world.time);
    }
  };
}
