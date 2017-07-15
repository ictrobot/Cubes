package ethanjones.cubes.core.platform.desktop;

import ethanjones.cubes.core.mod.ModLoader;

import com.badlogic.gdx.files.FileHandle;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

public class DesktopModLoader implements ModLoader {
  private static final ModType[] TYPES = new ModType[]{ModType.jar};

  public static class ExternalJarLoader extends URLClassLoader {

    public ExternalJarLoader(FileHandle fileHandle) throws IOException {
      super(new URL[]{fileHandle.file().toURI().toURL()});
    }

  }

  @Override
  public ModType[] getTypes() {
    return TYPES;
  }

  @Override
  public Class<?> loadClass(FileHandle classFile, String className, ModType modType) throws Exception {
    if (modType == ModType.jar) {
      return new ExternalJarLoader(classFile).loadClass(className);
    } else {
      throw new IllegalStateException(String.valueOf(modType));
    }
  }
}
