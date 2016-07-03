package ethanjones.cubes.core.mod.lua;

import ethanjones.cubes.core.event.EventBus;
import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.lua.CubesGlobals;
import ethanjones.cubes.core.mod.ModInstance;
import ethanjones.cubes.core.mod.ModState;
import ethanjones.cubes.core.mod.event.ModEvent;
import ethanjones.cubes.core.mod.event.StartingClientEvent;
import ethanjones.cubes.core.mod.event.StartingServerEvent;
import ethanjones.cubes.core.mod.lua.LuaMappingMod.LuaEventListener;
import ethanjones.cubes.core.util.OneManyMap;
import ethanjones.cubes.graphics.assets.AssetManager;
import ethanjones.cubes.side.Sided;

import com.badlogic.gdx.files.FileHandle;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class LuaModInstance extends ModInstance {

  public final Map<String, FileHandle> luaFiles;
  public final FileHandle initFile;
  private List<ModState> modStates;

  protected OneManyMap<ModState, LuaFunction> luaModEvent;
  protected ArrayList<LuaEventListener> clientEventListeners;
  protected ArrayList<LuaEventListener> serverEventListeners;

  public LuaModInstance(String name, FileHandle file, AssetManager assetManager, Map<String, FileHandle> luaFiles) {
    super(name, file, assetManager);
    this.luaFiles = luaFiles;
    List<ModState> modStates = new ArrayList<ModState>();
    modStates.add(ModState.Json);
    this.modStates = Collections.unmodifiableList(modStates);

    this.luaModEvent = new OneManyMap<ModState, LuaFunction>();
    this.clientEventListeners = new ArrayList<LuaEventListener>();
    this.serverEventListeners = new ArrayList<LuaEventListener>();

    int length = Integer.MAX_VALUE;
    String current = luaFiles.keySet().iterator().next();
    FileHandle currentFile = luaFiles.values().iterator().next();
    for (Entry<String, FileHandle> entry : luaFiles.entrySet()) {
      String s = entry.getKey().toLowerCase();
      if (s.contains("init") && s.length() < length) {
        length = s.length();
        current = s;
        currentFile = entry.getValue();
      }
    }
    initFile = currentFile;
    Log.debug("Using '" + current + "' as init file");
  }

  @Override
  protected void init() throws Exception {
    LuaValue chunk = CubesGlobals.modGlobals.loadfile(initFile.file().getAbsolutePath());
    chunk.call();
  }

  @Override
  protected void event(ModEvent modEvent) throws Exception {
    if (modEvent instanceof StartingClientEvent) {
      EventBus bus = Sided.getEventBus();
      for (LuaEventListener lel : clientEventListeners) {
        bus.register(lel);
      }
    } else if (modEvent instanceof StartingServerEvent) {
      EventBus bus = Sided.getEventBus();
      for (LuaEventListener lel : serverEventListeners) {
        bus.register(lel);
      }
    }

    List<LuaFunction> l = luaModEvent.get(modEvent.getModState());
    for (LuaFunction f : l) {
      f.call(modEvent.getModState().name());
    }
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
