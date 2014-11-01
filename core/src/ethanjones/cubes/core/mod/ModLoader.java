package ethanjones.cubes.core.mod;

import com.badlogic.gdx.files.FileHandle;

public interface ModLoader {

  enum ModType {
    jar, dex
  }

  public boolean supports(ModType type);

  public Class<?> loadClass(FileHandle file, String className) throws Exception;
}
