package ethanjones.cubes.core.mod;

import com.badlogic.gdx.files.FileHandle;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.mod.event.ModEvent;
import ethanjones.cubes.graphics.assets.AssetManager;
import ethanjones.cubes.graphics.assets.Assets;

public final class ModInstance<T> {

  private final T mod;
  private final String modName;
  private final FileHandle modFile;
  private HashMap<Class<? extends ModEvent>, Method> modEventHandlers;

  public ModInstance(T mod, String modName, FileHandle modFile) {
    this.mod = mod;
    this.modName = modName;
    this.modFile = modFile;
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
          Log.error("Mod:" + modName + " Class:" + mod.getClass().getName() + " Method:" + method);
          continue;
        }
        modEventHandlers.put(method.getParameterTypes()[0].asSubclass(ModEvent.class), method);
      }
    }
  }

  protected void event(ModEvent modEvent) throws Exception {
    Method method = modEventHandlers.get(modEvent.getClass());
    if (method == null) {
      Log.debug("No method to handle " + modEvent.getClass().getSimpleName() + " in mod " + modName);
      return;
    }
    Log.debug("Passing " + modEvent.getClass().getSimpleName() + " to mod " + modName);
    method.invoke(mod, modEvent);
  }

  public String toString() {
    return modName + " " + mod.getClass().getName();
  }

  public T getMod() {
    return mod;
  }

  public FileHandle getModFile() {
    return modFile;
  }

  public String getModName() {
    return modName;
  }

  public AssetManager getAssetManager() {
    return Assets.getAssetManager(modFile.name());
  }
}
