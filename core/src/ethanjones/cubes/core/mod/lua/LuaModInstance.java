package ethanjones.cubes.core.mod.lua;

import ethanjones.cubes.core.event.EventBus;
import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.lua.CubesLua;
import ethanjones.cubes.core.mod.ModInstance;
import ethanjones.cubes.core.mod.ModState;
import ethanjones.cubes.core.mod.event.ModEvent;
import ethanjones.cubes.core.mod.event.PreInitializationEvent;
import ethanjones.cubes.core.mod.event.StartingClientEvent;
import ethanjones.cubes.core.mod.event.StartingServerEvent;
import ethanjones.cubes.core.mod.lua.LuaMappingMod.LuaEventListener;
import ethanjones.cubes.core.util.Multimap;
import ethanjones.cubes.graphics.assets.AssetManager;
import ethanjones.cubes.side.common.Side;

import com.badlogic.gdx.files.FileHandle;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class LuaModInstance extends ModInstance {

  public final Map<String, FileHandle> luaFiles;
  public final FileHandle luaFolder;
  public final String initFile;
  private List<ModState> modStates;

  private Globals globals;
  protected Multimap<ModState, LuaFunction> luaModEvent;
  protected ArrayList<LuaEventListener> globalEventListeners;
  protected ArrayList<LuaEventListener> clientEventListeners;
  protected ArrayList<LuaEventListener> serverEventListeners;

  public LuaModInstance(String name, FileHandle file, AssetManager assetManager, Map<String, FileHandle> luaFiles, FileHandle luaFolder) {
    super(name, file, assetManager);
    this.luaFiles = luaFiles;
    this.luaFolder = luaFolder;
    this.modStates = new ArrayList<ModState>();

    this.globals = CubesLua.globals(); // each mod has its own globals
    this.luaModEvent = new Multimap<ModState, LuaFunction>();
    this.globalEventListeners = new ArrayList<LuaEventListener>();
    this.clientEventListeners = new ArrayList<LuaEventListener>();
    this.serverEventListeners = new ArrayList<LuaEventListener>();

    int length = Integer.MAX_VALUE;
    String current = luaFiles.keySet().iterator().next();
    for (Entry<String, FileHandle> entry : luaFiles.entrySet()) {
      String s = entry.getKey();
      if (s.toLowerCase().contains("init") && s.length() < length) {
        length = s.length();
        current = s;
      }
    }
    initFile = name + "|" + current;
    Log.debug("Using '" + initFile + "' as init file");
  }

  @Override
  protected void init() throws Exception {

  }

  @Override
  protected void event(ModEvent modEvent) throws Exception {
    if (modEvent instanceof PreInitializationEvent) {
      LuaValue chunk = globals.loadfile(initFile);
      chunk.call();

      EventBus bus = EventBus.getGlobalEventBus();
      for (LuaEventListener lel : globalEventListeners) {
        bus.register(lel);
      }
    } else {
      if (modEvent instanceof StartingClientEvent) {
        EventBus bus = Side.getSidedEventBus();
        for (LuaEventListener lel : clientEventListeners) {
          bus.register(lel);
        }
      } else if (modEvent instanceof StartingServerEvent) {
        EventBus bus = Side.getSidedEventBus();
        for (LuaEventListener lel : serverEventListeners) {
          bus.register(lel);
        }
      }

      List<LuaFunction> l = luaModEvent.get(modEvent.getModState());
      for (LuaFunction f : l) {
        f.call(modEvent.getModState().name());
      }
    }
    addState(modEvent.getModState());
  }

  protected void addState(ModState modState) {
    modStates.add(modState);
  }

  public List<ModState> getModStates() {
    return Collections.unmodifiableList(modStates);
  }

  @Override
  public Object getMod() {
    return this;
  }
}
