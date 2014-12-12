package ethanjones.cubes.core.mod.java;

import com.badlogic.gdx.files.FileHandle;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.mod.ModEventHandler;
import ethanjones.cubes.core.mod.ModInstance;
import ethanjones.cubes.core.mod.ModState;
import ethanjones.cubes.core.mod.event.ModEvent;
import ethanjones.cubes.graphics.assets.AssetManager;

public class JavaModInstance<T> extends ModInstance {

  private final T mod;
  private ArrayList<ModState> modStates;
  private HashMap<Class<? extends ModEvent>, Method> modEventHandlers;

  public JavaModInstance(String name, FileHandle file, AssetManager assetManager, T mod) {
    super(name, file, assetManager);
    this.mod = mod;
    modStates = new ArrayList<ModState>();
    modEventHandlers = new HashMap<Class<? extends ModEvent>, Method>();
  }

  protected void init() {
    for (Method method : mod.getClass().getMethods()) {
      if (method.getAnnotation(ModEventHandler.class) != null) {
        boolean error = false;
        if (Modifier.isStatic(method.getModifiers())) {
          Log.error("Methods annotated with " + ModEventHandler.class.getSimpleName() + " must not be static");
          error = true;
        }
        if (method.getParameterTypes().length != 1 || method.getParameterTypes()[0].getSuperclass() != ModEvent.class) {
          Log.error("Methods annotated with " + ModEventHandler.class.getSimpleName() + " must have only one parameter which must be the " + ModEvent.class.getSimpleName() + " instance");
          error = true;
        }
        if (error) {
          Log.error("Mod:" + name + " Class:" + mod.getClass().getName() + " Method:" + method);
          continue;
        }
        modEventHandlers.put(method.getParameterTypes()[0].asSubclass(ModEvent.class), method);
      }
    }
  }

  protected void event(ModEvent modEvent) throws Exception {
    Method method = modEventHandlers.get(modEvent.getClass());
    if (method == null) {
      Log.debug("No method to handle " + modEvent.getClass().getSimpleName() + " in mod " + name);
      return;
    }
    addState(modEvent.getModState());
    Log.debug("Posting " + modEvent.getClass().getSimpleName() + " to mod " + name);
    method.invoke(mod, modEvent);
  }

  protected void addState(ModState modState) {
    modStates.add(modState);
  }

  public T getMod() {
    return mod;
  }

  public List<ModState> getModStates() {
    return Collections.unmodifiableList(modStates);
  }
}
