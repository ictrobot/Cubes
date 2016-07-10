package ethanjones.cubes.core.lua;

import ethanjones.cubes.block.Block;
import ethanjones.cubes.core.IDManager;
import ethanjones.cubes.core.platform.Adapter;
import ethanjones.cubes.core.platform.Compatibility;
import ethanjones.cubes.core.system.Branding;
import ethanjones.cubes.item.Item;
import ethanjones.cubes.side.Sided;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaUserdata;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import java.util.HashMap;

public class LuaMappingCubes {

  public static VarArgFunction blocks = new VarArgFunction() {
    @Override
    public Varargs invoke(Varargs args) {
      BlocksItems.load();
      if (args.isstring(1)) {
        LuaUserdata u = BlocksItems.blockUserdata.get(args.checkjstring(1));
        return u != null ? u : NIL;
      } else {
        return BlocksItems.blockTable;
      }
    }
  };
  public static VarArgFunction items = new VarArgFunction() {
    @Override
    public Varargs invoke(Varargs args) {
      BlocksItems.load();
      if (args.isstring(1)) {
        LuaUserdata u = BlocksItems.itemUserdata.get(args.checkjstring(1));
        return u != null ? u : NIL;
      } else {
        return BlocksItems.itemTable;
      }
    }
  };

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

  private static class BlocksItems {
    public static HashMap<String, LuaUserdata> blockUserdata = new HashMap<String, LuaUserdata>();
    public static HashMap<String, LuaUserdata> itemUserdata = new HashMap<String, LuaUserdata>();
    public static LuaValue blockTable = LuaValue.NIL;
    public static LuaValue itemTable = LuaValue.NIL;

    public static void load() {
      if (blockTable == LuaValue.NIL && IDManager.isLoaded()) {
        LuaTable blockTemp = new LuaTable();
        for (Block block : IDManager.getBlocks()) {
          LuaUserdata u = new LuaUserdata(block);
          blockUserdata.put(block.id, u);
          blockTemp.set(block.id, u);
        }
        blockTable = new ReadOnlyLuaTable(blockTemp);

        LuaTable itemTemp = new LuaTable();
        for (Item item : IDManager.getItems()) {
          LuaUserdata u = new LuaUserdata(item);
          itemUserdata.put(item.id, u);
          itemTemp.set(item.id, u);
        }
        for (Item itemblock : IDManager.getItemBlocks()) {
          LuaUserdata u = new LuaUserdata(itemblock);
          itemUserdata.put(itemblock.id, u);
          itemTemp.set(itemblock.id, u);
        }
        itemTable = new ReadOnlyLuaTable(itemTemp);
      }
    }
  }
}
