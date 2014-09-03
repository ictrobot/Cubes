package ethanjones.modularworld.core.mod;

import com.badlogic.gdx.files.FileHandle;

public interface ModLoader {
  public static enum Type {
    jar, dex
  }

  public boolean supports(Type type);

  public void load(FileHandle file) throws Exception;

  public Class<?> loadClass(String fileName, String className) throws Exception;
}
