package ethanjones.cubes.core.lua;

import ethanjones.cubes.core.IDManager;
import ethanjones.cubes.core.event.EventBus;

import static ethanjones.cubes.core.lua.LuaMapping.mapping;

import ethanjones.cubes.core.mod.lua.LuaMappingMod;
import ethanjones.cubes.core.timing.Timing;
import ethanjones.cubes.networking.Networking;
import ethanjones.cubes.side.Side;
import ethanjones.cubes.side.Sided;
import ethanjones.cubes.side.common.Cubes;

import org.luaj.vm2.Globals;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

public class CubesGlobals {
  public static final Globals globals = globals();
  public static final Globals modGlobals = modGlobals();

  private static Globals globals() {
    Globals globals = JsePlatform.standardGlobals();

    globals.rawset("cubes", mapping(LuaMappingCubes.class));
    globals.rawset("log", mapping(LuaMappingLog.class));
    globals.rawset("sided", CoerceJavaToLua.coerce(new SidedWrapper()));

    return globals;
  }

  private static Globals modGlobals() {
    Globals globals = globals();

    globals.rawset("mod", mapping(LuaMappingMod.class));

    return globals;
  }

  public static class SidedWrapper {
    public EventBus getEventBus() {
      return Sided.getEventBus();
    }

    public Side getSide() {
      return Sided.getSide();
    }

    public boolean isMainThread(Side side) {
      return Sided.isMainThread(side);
    }

    public Timing getTiming() {
      return Sided.getTiming();
    }

    public IDManager getIDManager() {
      return Sided.getIDManager();
    }

    public Networking getNetworking() {
      return Sided.getNetworking();
    }

    public Cubes getCubes() {
      return Sided.getCubes();
    }
  }
}
