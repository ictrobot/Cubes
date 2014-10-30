package ethanjones.cubes.core.platform.desktop;

import com.badlogic.gdx.files.FileHandle;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import ethanjones.cubes.core.mod.ModLoader;
import ethanjones.cubes.core.mod.ModType;

public class DesktopModLoader implements ModLoader {

  public class ExternalJarLoader extends URLClassLoader {

    public ExternalJarLoader() {
      super(new URL[0]);
    }

    public void addFile(FileHandle fileHandle) throws MalformedURLException {
      URL url = fileHandle.file().toURI().toURL();
      super.addURL(url);
    }

  }

  ExternalJarLoader externalJarLoader = new ExternalJarLoader();

  @Override
  public boolean supports(ModType type) {
    return type == ModType.jar;
  }

  @Override
  public Class<?> loadClass(FileHandle classFile, String className) throws Exception {
    externalJarLoader.addFile(classFile);
    return externalJarLoader.loadClass(className);
  }
}
