package ethanjones.cubes.core.mod;

import com.badlogic.gdx.files.FileHandle;

public interface ModLoader {

  public boolean supports(ModType type);

  public Class<?> loadClass(FileHandle file, String className) throws Exception;
}
