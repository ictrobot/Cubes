package ethanjones.cubes.core.mod;

import com.badlogic.gdx.files.FileHandle;

public interface ModLoader {
  
  enum ModType {
    jar, dex
  }

  // in order of preference
  ModType[] getTypes();

  Class<?> loadClass(FileHandle file, String className, ModType modType) throws Exception;
}
