package ethanjones.modularworld.core.platform.desktop;

import com.badlogic.gdx.files.FileHandle;
import ethanjones.modularworld.core.mod.ModLoader;
import ethanjones.modularworld.core.mod.ModType;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class DesktopModLoader implements ModLoader {

  ExternalJarLoader externalJarLoader = new ExternalJarLoader();

  public class ExternalJarLoader extends URLClassLoader {

    public ExternalJarLoader() {
      super(new URL[0]);
    }

    public void addFile(FileHandle fileHandle) throws MalformedURLException {
      URL url = fileHandle.file().toURI().toURL();
      super.addURL(url);
    }

  }

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
