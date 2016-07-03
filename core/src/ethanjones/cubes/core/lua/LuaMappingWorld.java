package ethanjones.cubes.core.lua;

import ethanjones.cubes.block.Block;
import ethanjones.cubes.world.World;

import org.luaj.vm2.LuaUserdata;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

public final class LuaMappingWorld {

  private final World world;

  public LuaMappingWorld(World world) {
    this.world = world;
  }

  public ThreeArgFunction getBlock = new ThreeArgFunction() {
    @Override
    public LuaValue call(LuaValue arg1, LuaValue arg2, LuaValue arg3) {
      int x = arg1.checkint();
      int y = arg2.checkint();
      int z = arg3.checkint();
      return new LuaUserdata(world.getBlock(x, y, z));
    }
  };

  public ThreeArgFunction getLight = new ThreeArgFunction() {
    @Override
    public LuaValue call(LuaValue arg1, LuaValue arg2, LuaValue arg3) {
      int x = arg1.checkint();
      int y = arg2.checkint();
      int z = arg3.checkint();
      return LuaValue.valueOf(world.getLight(x, y, z));
    }
  };

  public ThreeArgFunction getSunLight = new ThreeArgFunction() {
    @Override
    public LuaValue call(LuaValue arg1, LuaValue arg2, LuaValue arg3) {
      int x = arg1.checkint();
      int y = arg2.checkint();
      int z = arg3.checkint();
      return LuaValue.valueOf(world.getSunLight(x, y, z));
    }
  };

  public ThreeArgFunction getLightRaw = new ThreeArgFunction() {
    @Override
    public LuaValue call(LuaValue arg1, LuaValue arg2, LuaValue arg3) {
      int x = arg1.checkint();
      int y = arg2.checkint();
      int z = arg3.checkint();
      return LuaValue.valueOf(world.getLightRaw(x, y, z));
    }
  };

  public VarArgFunction setBlock = new VarArgFunction() {
    @Override
    public Varargs invoke(Varargs args) {
      int x = args.checkint(1);
      int y = args.checkint(2);
      int z = args.checkint(3);
      Block block = (Block) args.checkuserdata(4, Block.class);
      int meta = args.optint(5, 0);
      world.setBlock(block, x, y, z, meta);
      return NIL;
    }
  };

  public OneArgFunction setTime = new OneArgFunction() {
    @Override
    public LuaValue call(LuaValue arg) {
      int time = arg.checkint();
      world.setTime(time);
      return NIL;
    }
  };

  public ZeroArgFunction getTime = new ZeroArgFunction() {
    @Override
    public LuaValue call() {
      return LuaValue.valueOf(world.time);
    }
  };
}
