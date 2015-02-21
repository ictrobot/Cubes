package ethanjones.cubes.common.mod;

import com.badlogic.gdx.files.FileHandle;

public interface ModLoader {

  enum ModType {
    jar, dex
  }

  public ModType getType();

  public Class<?> loadClass(FileHandle file, String className) throws Exception;
}
