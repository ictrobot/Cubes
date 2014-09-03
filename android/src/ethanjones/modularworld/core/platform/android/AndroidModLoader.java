package ethanjones.modularworld.core.platform.android;

import android.content.Context;
import com.badlogic.gdx.files.FileHandle;
import dalvik.system.DexClassLoader;
import ethanjones.modularworld.core.mod.ModLoader;

import java.util.HashMap;

public class AndroidModLoader implements ModLoader {

  private final AndroidCompatibility androidCompatibility;
  private HashMap<String, DexClassLoader> map = new HashMap<String, DexClassLoader>();

  public AndroidModLoader(AndroidCompatibility androidCompatibility) {
    this.androidCompatibility = androidCompatibility;
  }

  @Override
  public boolean supports(Type type) {
    return type == Type.dex;
  }

  @Override
  public void load(FileHandle file) throws Exception {
    DexClassLoader classLoader = new DexClassLoader(file.path(), androidCompatibility.androidLauncher.getDir("dex", Context.MODE_PRIVATE).getAbsolutePath(), null, AndroidModLoader.class.getClassLoader());
    map.put(file.name(), classLoader);
  }

  @Override
  public Class<?> loadClass(String file, String className) throws Exception {
    return map.get(file).loadClass(className);
  }
}
