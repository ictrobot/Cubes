package ethanjones.modularworld.core.mod;

import com.badlogic.gdx.files.FileHandle;

import java.util.Properties;

public interface ModLoader {
  public static enum Type {
    jar, dex
  }

  public boolean supports(Type type);

  public void load(FileHandle file) throws Exception;

  public Class<?> loadClass(Properties properties) throws Exception;
}
