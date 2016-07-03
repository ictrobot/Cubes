package ethanjones.cubes.core.mod.lua;

import ethanjones.cubes.core.event.Event;
import ethanjones.cubes.core.event.EventAlias;
import ethanjones.cubes.core.event.EventHandler;
import ethanjones.cubes.core.mod.ModManager;
import ethanjones.cubes.core.mod.ModState;
import ethanjones.cubes.side.Side;
import ethanjones.cubes.side.Sided;

import org.luaj.vm2.*;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

public class LuaMappingMod {

  public static TwoArgFunction modEvent = new TwoArgFunction() {
    @Override
    public LuaValue call(LuaValue arg1, LuaValue arg2) {
      ModState modState = (ModState) arg1.checkuserdata(ModState.class);
      LuaFunction callback = arg2.checkfunction();
      ((LuaModInstance) ModManager.getCurrentMod()).luaModEvent.put(modState, callback);
      return NIL;
    }
  };

  public static VarArgFunction eventBus = new VarArgFunction() {
    @Override
    public Varargs invoke(Varargs args) {
      String event = args.checkjstring(1);
      Class<? extends Event> c = EventAlias.getEventClass(event);
      LuaFunction callback = args.checkfunction(2);
      Side side = (Side) args.optuserdata(3, Side.class, null);
      LuaModInstance m = (LuaModInstance) ModManager.getCurrentMod();

      if (side == Side.Client || side == null) {
        m.clientEventListeners.add(new LuaEventListener(Side.Client, c, callback));
      }
      if (side == Side.Server || side == null) {
        m.serverEventListeners.add(new LuaEventListener(Side.Server, c, callback));
      }
      return NIL;
    }
  };

  public static final Class<?> state = LState.class;

  public static final Class<?> side = LSide.class;

  protected static class LState {

    public static LuaValue preInitialization = new LuaUserdata(ModState.PreInitialization);

    public static LuaValue initialization = new LuaUserdata(ModState.Initialization);

    public static LuaValue postInitialization = new LuaUserdata(ModState.PostInitialization);

    public static LuaValue startingClient = new LuaUserdata(ModState.StartingClient);

    public static LuaValue startingServer = new LuaUserdata(ModState.StartingServer);

    public static LuaValue stoppingClient = new LuaUserdata(ModState.StoppingClient);

    public static LuaValue stoppingServer = new LuaUserdata(ModState.StoppingServer);
  }

  public static class LSide {

    public static LuaValue client = new LuaUserdata(Side.Client);

    public static LuaValue server = new LuaUserdata(Side.Server);

    public static ZeroArgFunction getSide = new ZeroArgFunction() {
      @Override
      public LuaValue call() {
        Side s = Sided.getSide();
        if (s == Side.Client) return client;
        if (s == Side.Server) return server;
        return NIL;
      }
    };
  }

  public static class LuaEventListener {
    public final Side side;
    public final Class<? extends Event> c;
    public final LuaFunction callback;

    public LuaEventListener(Side side, Class<? extends Event> c, LuaFunction callback) {
      this.side = side;
      this.c = c;
      this.callback = callback;
    }

    @EventHandler
    public void event(final Event e) {
      if (c.isAssignableFrom(e.getClass())) {
        LuaTable event = new LuaTable();
        event.set("type", e.getClass().getName());
        event.set("isCancelable", LuaValue.valueOf(e.isCancelable()));
        event.set("cancel", new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            e.setCanceled(true);
            return NIL;
          }
        });
        event.set("data", CoerceJavaToLua.coerce(e));

        callback.call(event);
      }
    }
  }
}
